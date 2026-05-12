package mekanism.common.capabilities.holder.chemical;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.capabilities.holder.BasicHolder;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ChemicalTankHolder<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
      extends BasicHolder<TANK, Object> implements IChemicalTankHolder<CHEMICAL, STACK, TANK> {

    ChemicalTankHolder(Supplier<Direction> facingSupplier) {
        super(facingSupplier);
    }

    void addTank(@NotNull TANK tank, RelativeSide... sides) {
        addSlotInternal(tank, sides);
    }

    @Override
    public @NotNull Storage<CHEMICAL> getTanks(@Nullable Direction direction) {
        return new CombinedStorage<>(getSlots(direction));
    }
}