package mekanism.api.fluid;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public interface IMekanismFluidHandler extends IContentsListener {

    /**
     * Used to check if an instance of {@link IMekanismFluidHandler} actually has the ability to handle fluid.
     *
     * @return True if we are actually capable of handling fluid.
     *
     * @apiNote If for some reason you are comparing to {@link IMekanismFluidHandler} without having gotten the object via the fluid handler capability, then you must
     * call this method to make sure that it really can handle fluid. As most mekanism tiles have this class in their hierarchy.
     * @implNote If this returns false the capability should not be exposed AND methods should turn reasonable defaults for not doing anything.
     */
    default boolean canHandleFluid() {
        return true;
    }

    /**
     * Returns the list of IExtendedFluidTanks that this fluid handler exposes on the given side.
     *
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The list of all IExtendedFluidTanks that this {@link IMekanismFluidHandler} contains for the given side. If there are no tanks for the side or
     * {@link #canHandleFluid()} is false then it returns an empty list.
     *
     * @implNote When side is null (an internal request), this method <em>MUST</em> return all tanks in the handler. Additionally, if {@link #canHandleFluid()} is false,
     * this <em>MUST</em> return an empty list.
     */
    Storage<FluidVariant> getFluidTanks(@Nullable Direction side);

}