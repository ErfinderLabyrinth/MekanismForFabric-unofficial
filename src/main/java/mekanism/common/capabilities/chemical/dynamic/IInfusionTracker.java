package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface IInfusionTracker extends IContentsListener {

    Storage<InfuseType> getInfusionStorage(@Nullable Direction side);
    List<IInfusionTank> getInfusionTanks();
}