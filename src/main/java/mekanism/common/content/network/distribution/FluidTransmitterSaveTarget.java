package mekanism.common.content.network.distribution;

import mekanism.api.FluidStack;
import mekanism.api.math.MathUtils;
import mekanism.common.content.network.transmitter.MechanicalPipe;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

//TODO: Improve handling for fluid storage as longs
public class FluidTransmitterSaveTarget extends Target<FluidTransmitterSaveTarget.SaveHandler, Long, @NotNull FluidStack> {

    public FluidTransmitterSaveTarget(@NotNull FluidStack type, Collection<MechanicalPipe> transmitters) {
        super(transmitters.size());
        this.extra = type;
        transmitters.forEach(transmitter -> addHandler(new SaveHandler(transmitter)));
    }

    @Override
    protected void acceptAmount(FluidTransmitterSaveTarget.SaveHandler handler, SplitInfo<Long> splitInfo, Long amount) {
        handler.acceptAmount(splitInfo, amount);
    }

    @Override
    protected Long simulate(FluidTransmitterSaveTarget.SaveHandler handler, @NotNull FluidStack fluidStack) {
        return handler.simulate(fluidStack);
    }

    public void saveShare() {
        for (FluidTransmitterSaveTarget.SaveHandler handler : handlers) {
            handler.saveShare();
        }
    }

    public class SaveHandler {

        private FluidStack currentStored = FluidStack.EMPTY;
        private final MechanicalPipe transmitter;

        public SaveHandler(MechanicalPipe transmitter) {
            this.transmitter = transmitter;
        }

        protected void acceptAmount(SplitInfo<Long> splitInfo, Long amount) {
            amount = Math.min(amount, MathUtils.clampToInt(transmitter.getCapacity() - currentStored.amount()));
            if (currentStored.isEmpty()) {
                currentStored = new FluidStack(extra, amount);
            } else {
                currentStored.grow(amount);
            }
            splitInfo.send(amount);
        }

        protected Long simulate(@NotNull FluidStack fluidStack) {
            if (!currentStored.isEmpty() && !currentStored.equals(fluidStack)) {
                return 0L;
            }
            return Math.min(fluidStack.amount(), MathUtils.clampToInt(transmitter.getCapacity() - currentStored.amount()));
        }

        protected void saveShare() {
            if (currentStored.isEmpty() != transmitter.saveShare.isEmpty() || (!currentStored.isEmpty() && !currentStored.isFluidStackIdentical(transmitter.saveShare))) {
                transmitter.saveShare = currentStored;
                transmitter.getTransmitterTile().markForSave();
            }
        }
    }
}