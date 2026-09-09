package mekanism.api.energy;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public interface IMekanismStrictEnergyHandler extends IContentsListener {

    /**
     * Used to check if an instance of {@link IMekanismStrictEnergyHandler} actually has the ability to handle energy.
     *
     * @return True if we are actually capable of handling energy.
     *
     * @apiNote If for some reason you are comparing to {@link IMekanismStrictEnergyHandler} without having gotten the object via the strict energy handler capability,
     * then you must call this method to make sure that it really can handle energy. As most mekanism tiles have this class in their hierarchy.
     * @implNote If this returns false the capability should not be exposed AND methods should turn reasonable defaults for not doing anything.
     */
    default boolean canHandleEnergy() {
        return true;
    }

    /**
     * Returns the list of IEnergyContainers that this energy handler exposes on the given side.
     *
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The list of all IEnergyContainers that this {@link IMekanismStrictEnergyHandler} contains for the given side. If there are no containers for the side or
     * {@link #canHandleEnergy()} is false then it returns an empty list.
     *
     * @implNote When side is null (an internal request), this method <em>MUST</em> return all containers in the handler. Additionally, if {@link #canHandleEnergy()} is
     * false, this <em>MUST</em> return an empty list.
     */
    EnergyStorage getEnergyContainer(@Nullable Direction side);
}