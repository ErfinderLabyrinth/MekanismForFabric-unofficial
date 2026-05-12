package mekanism.common.util.text;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import net.minecraft.network.chat.Component;
import team.reborn.energy.api.EnergyStorage;

@NothingNullByDefault
public class EnergyDisplay implements IHasTextComponent {

    public static final EnergyDisplay ZERO = of(FloatingLong.ZERO);

    private final long energy;
    private final long max;

    private EnergyDisplay(long energy, long max) {
        this.energy = energy;
        this.max = max;
    }

    public static EnergyDisplay of(IEnergyContainer container) {
        return of(container.getEnergy(), container.getMaxEnergy());
    }

    public static EnergyDisplay of(EnergyStorage container) {
        return of(container.getAmount(), container.getCapacity());
    }

    public static EnergyDisplay of(long energy, long max) {
        return new EnergyDisplay(energy, max);
    }

    @Deprecated(forRemoval = true)
    public static EnergyDisplay of(FloatingLong energy, FloatingLong max) {
        return of(energy.longValue(), max.longValue());
    }

    public static EnergyDisplay of(FloatingLong energy) {
        return of(energy, FloatingLong.ZERO);
    }

    public static EnergyDisplay of(long energy) {
        return of(energy, 0);
    }

    @Override
    public Component getTextComponent() {
        if (energy == Long.MAX_VALUE) {
            return MekanismLang.INFINITE.translate();
        } else if (max == 0) {
            return MekanismUtils.getEnergyDisplayShort(energy);
        }
        //Pass max back as a new Energy Display so that if we have 0/infinite it shows that properly without us having to add extra handling
        return MekanismLang.GENERIC_FRACTION.translate(MekanismUtils.getEnergyDisplayShort(energy), of(max));
    }
}