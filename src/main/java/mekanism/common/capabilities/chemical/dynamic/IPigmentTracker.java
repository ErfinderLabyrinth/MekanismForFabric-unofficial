package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface IPigmentTracker extends IContentsListener {

    Storage<Pigment> getPigmentStorage(@Nullable Direction side);
    List<IPigmentTank> getPigmentTanks();
}