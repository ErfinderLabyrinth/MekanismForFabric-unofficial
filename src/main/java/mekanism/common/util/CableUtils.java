package mekanism.common.util;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.network.distribution.EnergyAcceptorTarget;
import mekanism.common.integration.energy.EnergyCompatUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.EnumSet;
import java.util.Set;

public final class CableUtils {

    private CableUtils() {
    }

    public static void emit(IEnergyContainer energyContainer, BlockEntity from) {
        emit(EnumSet.allOf(Direction.class), energyContainer, from);
    }

    public static void emit(Set<Direction> outputSides, IEnergyContainer energyContainer, BlockEntity from) {
        emit(outputSides, energyContainer, from, energyContainer.getMaxEnergy());
    }

    public static void emit(Set<Direction> outputSides, IEnergyContainer energyContainer, BlockEntity from, long maxOutput) {
        if (!energyContainer.isEmpty() && maxOutput != 0) {
            long simulatedExtract;
            try(Transaction t=Transaction.openOuter()) {
                simulatedExtract = energyContainer.extract(maxOutput, t);
            }
            long amountExtract = emit(outputSides, simulatedExtract, from);
            try(Transaction t=Transaction.openOuter()) {
                energyContainer.extract(amountExtract, t);
                t.commit();
            }
        }
    }

    /**
     * Emits energy from a central block by splitting the received stack among the sides given.
     *
     * @param sides        - the list of sides to output from
     * @param energyToSend - the energy to output
     * @param from         - the TileEntity to output from
     *
     * @return the amount of energy emitted
     */
    public static long emit(Set<Direction> sides, Long energyToSend, BlockEntity from) {
        if (energyToSend == 0 || sides.isEmpty()) {
            return 0;
        }
        EnergyAcceptorTarget target = new EnergyAcceptorTarget(6);
        EmitUtils.forEachSide(from.getLevel(), from.getBlockPos(), sides, (level, pos, side) -> {
            //Insert to access side and collect the cap if it is present
            EnergyStorage energyHandler = EnergyStorage.SIDED.find(level, pos, side.getOpposite());
            if (energyHandler != null) {
                target.addHandler(energyHandler);
            }
        });
        if (target.getHandlerCount() > 0) {
            return EmitUtils.sendToAcceptors(target, energyToSend);
        }
        return 0;
    }
}