package mekanism.common.inventory.slot;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.security.*;
import mekanism.common.lib.security.SecurityFrequency;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NothingNullByDefault
public class SecurityInventorySlot extends BasicInventorySlot {

    private static final Predicate<@NotNull ItemStack> validator = stack -> stack.getItem() instanceof IOwnerObject;

    public static SecurityInventorySlot unlock(Supplier<UUID> ownerSupplier, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(ownerSupplier, "Owner supplier cannot be null");
        return new SecurityInventorySlot(stack -> ISecurityUtils.INSTANCE.getOwnerUUID(stack) == null, stack -> {
            UUID ownerUUID = ISecurityUtils.INSTANCE.getOwnerUUID(stack);
            return ownerUUID != null && ownerUUID.equals(ownerSupplier.get());
        }, listener, x, y);
    }

    public static SecurityInventorySlot lock(@Nullable IContentsListener listener, int x, int y) {
        Predicate<@NotNull ItemStack> insertPredicate = stack -> ISecurityUtils.INSTANCE.getOwnerUUID(stack) == null;
        return new SecurityInventorySlot(insertPredicate.negate(), insertPredicate, listener, x, y);
    }

    private SecurityInventorySlot(Predicate<@NotNull ItemStack> canExtract, Predicate<@NotNull ItemStack> canInsert, @Nullable IContentsListener listener, int x, int y) {
        super(canExtract, canInsert, validator, listener, x, y);
    }

    public void unlock(UUID ownerUUID) {
        if (!isEmpty()) {
            IOwnerObject ownerObject;
            if (current.getStack().getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && (ownerObject = ownerObjectGetter.getOwnerObject(current.getStack())) != null) {
                UUID stackOwner = ownerObject.getOwnerUUID();
                if (stackOwner != null && stackOwner.equals(ownerUUID)) {
                    ownerObject.setOwnerUUID(null);
                    if (ownerObject instanceof ISecurityObject securityObject) {
                        securityObject.setSecurityMode(SecurityMode.PUBLIC);
                    }
                }
            }
        }
    }

    public void lock(UUID ownerUUID, SecurityFrequency frequency) {
        if (!isEmpty()) {
            IOwnerObject ownerObject;
            if (current.getStack().getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && (ownerObject = ownerObjectGetter.getOwnerObject(current.getStack())) != null) {
                UUID stackOwner = ownerObject.getOwnerUUID();
                if (stackOwner == null) {
                    ownerObject.setOwnerUUID(stackOwner = ownerUUID);
                }
                if (stackOwner.equals(ownerUUID)) {
                    if (ownerObject instanceof ISecurityObject securityObject) {
                        securityObject.setSecurityMode(frequency.getSecurityMode());
                    }
                }
            }
        }
    }
}