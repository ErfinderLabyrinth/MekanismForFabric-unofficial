package mekanism.common.content.network.distribution;

import mekanism.api.math.FloatingLong;
import mekanism.common.content.network.transmitter.UniversalCable;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;

import java.util.Collection;

public class EnergyTransmitterSaveTarget extends Target<EnergyTransmitterSaveTarget.SaveHandler, Long, Long> {

    public EnergyTransmitterSaveTarget(Collection<UniversalCable> transmitters) {
        super(transmitters.size());
        transmitters.forEach(transmitter -> addHandler(new SaveHandler(transmitter)));
    }

    @Override
    protected void acceptAmount(EnergyTransmitterSaveTarget.SaveHandler transmitter, SplitInfo<Long> splitInfo, Long amount) {
        transmitter.acceptAmount(splitInfo, amount);
    }

    @Override
    protected Long simulate(EnergyTransmitterSaveTarget.SaveHandler transmitter, Long energyToSend) {
        return transmitter.simulate(energyToSend);
    }

    public void saveShare() {
        for (EnergyTransmitterSaveTarget.SaveHandler cable : handlers) {
            cable.saveShare();
        }
    }

    public static class SaveHandler {

        private long currentStored = 0;
        private final UniversalCable transmitter;

        public SaveHandler(UniversalCable transmitter) {
            this.transmitter = transmitter;
        }

        protected void acceptAmount(SplitInfo<Long> splitInfo, Long amount) {
            amount = Long.min(amount, transmitter.getCapacity() - currentStored);
            currentStored = currentStored + amount.longValue();
            splitInfo.send(amount);
        }

        protected Long simulate(Long energyToSend) {
            return Long.min(energyToSend, transmitter.getCapacity() - currentStored);
        }

        protected void saveShare() {
            if (currentStored != 0 || transmitter.lastWrite != 0) {
                transmitter.lastWrite = currentStored;
                transmitter.getTransmitterTile().markForSave();
            }
        }
    }
}