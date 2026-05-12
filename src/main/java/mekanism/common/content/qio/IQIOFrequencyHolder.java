package mekanism.common.content.qio;

import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IFrequencyHandler;
import mekanism.common.tile.interfaces.ITileWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IQIOFrequencyHolder extends IFrequencyHandler, ITileWrapper, IQIOComponent {

    @Nullable
    @Override
    default QIOFrequency getQIOFrequency() {
        return getFrequency(FrequencyType.QIO);
    }

    default List<QIOFrequency> getPublicFrequencies() {
        return getPublicCache(FrequencyType.QIO);
    }

    default List<QIOFrequency> getPrivateFrequencies() {
        return getPrivateCache(FrequencyType.QIO);
    }
}
