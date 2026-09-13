package mekanism.common.tile;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.entity.EntityRobit;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.function.Predicate;

public class TileEntityChargepad extends TileEntityMekanism {

    private static final Predicate<LivingEntity> CHARGE_PREDICATE = entity -> !entity.isSpectator() && (entity instanceof Player || entity instanceof EntityRobit);

    private MachineEnergyContainer<TileEntityChargepad> energyContainer;

    public TileEntityChargepad(BlockPos pos, BlockState state) {
        super(MekanismBlocks.CHARGEPAD, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener), RelativeSide.BACK, RelativeSide.BOTTOM);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean active = false;
        //Use 0.4 for y to catch entities that are partially standing on the back pane
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
              worldPosition.getX() + 1, worldPosition.getY() + 0.4, worldPosition.getZ() + 1), CHARGE_PREDICATE);
        for (LivingEntity entity : entities) {
            active = !energyContainer.isEmpty();
            if (!active) {
                //If we run out of energy, stop checking the remaining entities
                break;
            } else if (entity instanceof EntityRobit robit) {
                provideEnergy(robit.getEnergyContainer());
            } else if (entity instanceof Player player) {
                Inventory inventory = player.getInventory();
                //Optional<IItemHandler> itemHandlerCap = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
                if (!chargeHandler(inventory) && Mekanism.hooks.CuriosLoaded) {
                    //If we didn't charge anything in the inventory and curios is loaded try charging things in the curios slots
//                    chargeHandler(CuriosIntegration.getCuriosInventory(entity));
                }
            }
        }
        if (active != getActive()) {
            setActive(active);
        }
    }

    private boolean chargeHandler(Inventory inventory) {
        //Ensure that we have an item handler capability, because if for example the player is dead we will not
        PlayerInventoryStorage storage = PlayerInventoryStorage.of(inventory);
        for (SingleSlotStorage<ItemVariant> slot:storage.getSlots()) {
            EnergyStorage energyStorage = ContainerItemContext.ofPlayerSlot(inventory.player, slot).find(EnergyStorage.ITEM);
            if (!slot.isResourceBlank() && slot.getAmount() != 0 && provideEnergy(energyStorage)) {
                //Only allow charging one item per player each check
                return true;
            }
        }
        return false;
    }

    private boolean provideEnergy(@Nullable EnergyStorage energyHandler) {
        if (energyHandler == null) {
            return false;
        }
        long energyToGive = energyContainer.getEnergyPerTick();
        long simulatedRemainder;
        try(Transaction t=Transaction.openOuter()) {
            simulatedRemainder = energyHandler.insert(energyToGive, t);
        }
        if (simulatedRemainder < energyToGive) {
            //We are able to fit at least some energy from our container into the item
            long extractedEnergy;
            try(Transaction t=Transaction.openOuter()) {
                extractedEnergy = energyContainer.extract(energyToGive - simulatedRemainder, t);
                t.commit();
            }
            if (extractedEnergy != 0) {
                //If we were able to actually extract it from our energy container, then insert it into the item
                try(Transaction t=Transaction.openOuter()) {
                    MekanismUtils.logExpectedZero(energyHandler.insert(extractedEnergy, t));
                    t.commit();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if (getActive()) {
            level.addParticle(DustParticleOptions.REDSTONE, getBlockPos().getX() + level.random.nextDouble(), getBlockPos().getY() + 0.15,
                  getBlockPos().getZ() + level.random.nextDouble(), 0, 0, 0);
        }
    }

    @Override
    public void setActive(boolean active) {
        boolean wasActive = getActive();
        super.setActive(active);
        if (wasActive != active) {
            //If the state changed play pressure plate sound
            SoundEvent sound;
            float pitch;
            if (active) {
                sound = SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON;
                pitch = 0.8F;
            } else {
                sound = SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF;
                pitch = 0.7F;
            }
            level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.1, getBlockPos().getZ() + 0.5, sound, SoundSource.BLOCKS, 0.3F, pitch);
        }
    }
}