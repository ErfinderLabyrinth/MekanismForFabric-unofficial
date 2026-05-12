package mekanism.common.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.functions.TriConsumer;
import mekanism.api.security.*;
import mekanism.api.text.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.base.MekanismPermissions;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.lib.security.SecurityFrequency;
import mekanism.common.network.to_client.PacketSecurityUpdate;
import mekanism.common.util.text.OwnerDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @apiNote Do not instantiate this class directly as it will be done via the service loader. Instead, access instances of this via {@link ISecurityUtils#INSTANCE}
 */
@NothingNullByDefault
public final class SecurityUtils implements ISecurityUtils {

    public static SecurityUtils get() {
        return (SecurityUtils) INSTANCE;
    }

    /**
     * Whether ops can bypass security and a given player is considered an Op.
     *
     * @param p - player to check
     *
     * @return if the player has operator privileges
     */
    private boolean isOp(Player p) {
        Objects.requireNonNull(p, "Player may not be null.");
        return MekanismConfig.general.opsBypassRestrictions && p instanceof ServerPlayer player &&
                MekanismPermissions.BYPASS_SECURITY.test(p);
    }

    private Optional<IOwnerObject> getOwnerObject(Object provider) {
        if (provider instanceof IOwnerObject ownerObject) {
            return Optional.of(ownerObject);
        } else if (provider instanceof ItemStack stack && stack.getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter) {
            return Optional.ofNullable(ownerObjectGetter.getOwnerObject(stack));
        }
        return Optional.empty();
    }

    private Optional<ISecurityObject> getSecurityObject(Object provider) {
        if (provider instanceof ISecurityObject ownerObject) {
            return Optional.of(ownerObject);
        } else if (provider instanceof ItemStack stack && stack.getItem() instanceof ISecurityObject ownerObject) {
            return Optional.of(ownerObject);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public UUID getOwnerUUID(Object provider) {
        Objects.requireNonNull(provider, "Capability provider may not be null.");
        return getOwnerObject(provider).map(IOwnerObject::getOwnerUUID).orElse(null);
    }

    @Override
    public boolean canAccess(Player player, @Nullable Object provider) {
        //If the player is an op allow bypassing any restrictions
        return isOp(player) || canAccess(player.getUUID(), provider, player.level().isClientSide);
    }

    @Override
    public boolean canAccessObject(Player player, ISecurityObject security) {
        //If the player is an op allow bypassing any restrictions
        return isOp(player) || canAccessObject(player.getUUID(), security, player.level().isClientSide);
    }

    @Override
    public boolean canAccess(@Nullable UUID player, @Nullable Object provider, boolean isClient) {
        if (!MekanismConfig.general.allowProtection || provider == null) {
            //If protection is disabled, access is always granted
            return true;
        }
        //Note: We don't just use getSecurityObject here as we support checking access to things that are only owned and don't have security
        Optional<ISecurityObject> securityCapability = getSecurityObject(provider);
        if (securityCapability.isEmpty()) {
            //If it is an owner item but not a security item make sure the owner matches
            Optional<IOwnerObject> ownerCapability = getOwnerObject(provider);
            if (ownerCapability.isPresent()) {
                //If it is an owner object but not a security object make sure the owner matches
                UUID owner = ownerCapability.get().getOwnerUUID();
                return owner == null || owner.equals(player);
            }
            //Otherwise, if there is no owner AND no security, access is always granted
            return true;
        }
        return canAccessObject(player, securityCapability.get(), isClient);
    }

    @Override
    public boolean canAccessObject(@Nullable UUID player, @NotNull ISecurityObject security, boolean isClient) {
        Objects.requireNonNull(security, "Security object may not be null.");
        if (!MekanismConfig.general.allowProtection) {
            //If protection is disabled, access is always granted
            return true;
        }
        UUID owner = security.getOwnerUUID();
        if (owner == null || owner.equals(player)) {
            return true;
        }
        return switch (getEffectiveSecurityMode(security, isClient)) {
            case PUBLIC -> true;
            case PRIVATE -> false;
            case TRUSTED -> {
                if (player == null) {
                    yield false;
                } else if (isClient) {
                    //If we are the client, then we just return true and assume that we can access the frequency
                    // as we don't know which players are set as trusted
                    //TODO: Technically in single player if the player is the single player owner we could hackily reach across
                    // sides but I don't think there is much benefit to doing so for how complex it is to do
                    yield true;
                }
                SecurityFrequency frequency = FrequencyType.SECURITY.getManager(null, null).getFrequency(owner);
                //If we have no frequency handle it as if it was private, otherwise check if the player is trusted
                yield frequency != null && frequency.getTrustedUUIDs().contains(player);
            }
        };
    }

    @Override
    public boolean moreRestrictive(SecurityMode base, SecurityMode overridden) {
        Objects.requireNonNull(base, "Base security mode may not be null.");
        Objects.requireNonNull(base, "Override security mode may not be null.");
        return switch (overridden) {
            //If the override mode is public it is never more restrictive than the normal level
            case PUBLIC -> false;
            //If the override mode is private it is only more restrictive if the base isn't already private
            case PRIVATE -> base != SecurityMode.PRIVATE;
            //If the override mode is trusted it is only more restrictive if the normal level was public
            case TRUSTED -> base == SecurityMode.PUBLIC;
        };
    }

    public SecurityData getFinalData(ISecurityObject securityObject, boolean isClient) {
        if (!MekanismConfig.general.allowProtection) {
            return SecurityData.DUMMY;
        }
        SecurityData data = getData(securityObject.getOwnerUUID(), isClient);
        SecurityMode mode = securityObject.getSecurityMode();
        if (data.override() && moreRestrictive(mode, data.mode())) {
            //If our frequency's data is set to override, and it is more restrictive than the current mode,
            // return the data for our frequency
            return data;
        }
        return new SecurityData(mode, false);
    }

    private SecurityData getData(@Nullable UUID uuid, boolean isClient) {
        if (uuid == null) {
            return SecurityData.DUMMY;
        } else if (isClient) {
            return MekanismClient.clientSecurityMap.getOrDefault(uuid, SecurityData.DUMMY);
        }
        SecurityFrequency frequency = FrequencyType.SECURITY.getManager(null, null).getFrequency(uuid);
        return frequency == null ? SecurityData.DUMMY : new SecurityData(frequency);
    }

    @Override
    public SecurityMode getSecurityMode(@Nullable Object provider, boolean isClient) {
        if (provider == null || !MekanismConfig.general.allowProtection) {
            return SecurityMode.PUBLIC;
        }
        return getSecurityObject(provider).map(security -> getEffectiveSecurityMode(security, isClient))
              .orElseGet(() -> getOwnerObject(provider).isPresent() ? SecurityMode.PRIVATE : SecurityMode.PUBLIC);
    }

    @Override
    public SecurityMode getEffectiveSecurityMode(ISecurityObject securityObject, boolean isClient) {
        Objects.requireNonNull(securityObject, "Security object may not be null.");
        return getFinalData(securityObject, isClient).mode();
    }

    public void incrementSecurityMode(Player player, Object provider) {
        Optional<ISecurityObject> securityOpt = getSecurityObject(provider);
        if(securityOpt.isPresent()) {
            ISecurityObject security = securityOpt.get();
            if (security.ownerMatches(player)) {
                security.setSecurityMode(security.getSecurityMode().getNext());
            }
        }
    }

    public void decrementSecurityMode(Player player, Object provider) {
        Optional<ISecurityObject> securityOpt = getSecurityObject(provider);
        if(securityOpt.isPresent()) {
            ISecurityObject security = securityOpt.get();
            if (security.ownerMatches(player)) {
                security.setSecurityMode(security.getSecurityMode().getPrevious());
            }
        }
    }

    public InteractionResultHolder<ItemStack> claimOrOpenGui(Level level, Player player, InteractionHand hand,
          TriConsumer<ServerPlayer, InteractionHand, ItemStack> openGui) {
        ItemStack stack = player.getItemInHand(hand);
        if (!tryClaimItem(level, player, stack)) {
            if (!canAccessOrDisplayError(player, stack)) {
                return InteractionResultHolder.fail(stack);
            } else if (!level.isClientSide) {
                openGui.accept((ServerPlayer) player, hand, stack);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public boolean tryClaimItem(Level level, Player player, ItemStack stack) {
        IOwnerObject ownerObject;
        if (stack.getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && (ownerObject = ownerObjectGetter.getOwnerObject(stack)) != null) {
            if (ownerObject.getOwnerUUID() == null) {
                if (!level.isClientSide) {
                    ownerObject.setOwnerUUID(player.getUUID());
                    Mekanism.packetHandler().sendToAll(new PacketSecurityUpdate(player.getUUID()), player.getServer());
                    player.sendSystemMessage(MekanismUtils.logFormat(MekanismLang.NOW_OWN));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void displayNoAccess(Player player) {
        Objects.requireNonNull(player, "Player may not be null.");
        player.sendSystemMessage(MekanismUtils.logFormat(EnumColor.RED, MekanismLang.NO_ACCESS));
    }

    public void addOwnerTooltip(ItemStack stack, List<Component> tooltip) {
        IOwnerObject ownerObject;
        if (stack.getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && (ownerObject = ownerObjectGetter.getOwnerObject(stack)) != null) {
            tooltip.add(OwnerDisplay.of(MekanismUtils.tryGetClientPlayer(), ownerObject.getOwnerUUID()).getTextComponent());
        }
    }

    @Override
    public void addSecurityTooltip(ItemStack stack, List<Component> tooltip) {
        Objects.requireNonNull(stack, "Stack to add tooltip for may not be null.");
        Objects.requireNonNull(tooltip, "List of tooltips to add to may not be null.");
        addOwnerTooltip(stack, tooltip);
        if(stack.getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && ownerObjectGetter.getOwnerObject(stack) instanceof ISecurityObject security) {
            SecurityData data = getFinalData(security, true);
            tooltip.add(MekanismLang.SECURITY.translateColored(EnumColor.GRAY, data.mode()));
            if (data.override()) {
                tooltip.add(MekanismLang.SECURITY_OVERRIDDEN.translateColored(EnumColor.RED));
            }
        }
    }

    public void securityChanged(Set<Player> playersUsing, Object target, SecurityMode old, SecurityMode mode) {
        //If the mode changed and the new security mode is more restrictive than the old one
        // and there are players using the security object
        if (moreRestrictive(old, mode) && !playersUsing.isEmpty()) {
            //then double check that all the players are actually supposed to be able to access the GUI
            for (Player player : new ObjectOpenHashSet<>(playersUsing)) {
                if (!canAccess(player, target)) {
                    //and if they can't then boot them out
                    player.closeContainer();
                }
            }
        }
    }
}