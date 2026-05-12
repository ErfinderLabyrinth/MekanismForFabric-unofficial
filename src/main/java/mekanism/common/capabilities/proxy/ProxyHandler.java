package mekanism.common.capabilities.proxy;

import mekanism.api.annotations.FieldsAreNotNullByDefault;
import mekanism.common.capabilities.holder.IHolder;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

@FieldsAreNotNullByDefault
public class ProxyHandler {

    private static final BooleanSupplier alwaysFalse = () -> false;

    @Nullable
    protected final Direction side;
    protected final boolean readOnly;
    protected final BooleanSupplier readOnlyInsert;
    protected final BooleanSupplier readOnlyExtract;

    protected ProxyHandler(@Nullable Direction side, @Nullable IHolder holder) {
        this.side = side;
        this.readOnly = this.side == null;
        this.readOnlyInsert = holder == null ? alwaysFalse : () -> !holder.canInsert(side);
        this.readOnlyExtract = holder == null ? alwaysFalse : () -> !holder.canExtract(side);
    }

    public void updateSnapshots(TransactionContext t) {

    }


}