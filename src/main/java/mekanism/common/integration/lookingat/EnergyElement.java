package mekanism.common.integration.lookingat;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

public class EnergyElement extends LookingAtElement {

    protected final long energy;
    protected final long maxEnergy;

    public EnergyElement(long energy, long maxEnergy) {
        super(0xFF000000, 0xFFFFFF);
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int getScaledLevel(int level) {
        if (energy == Long.MAX_VALUE) {
            return level;
        }
        return (int) (level * (double)energy / (double)maxEnergy);
    }

    public long getEnergy() {
        return energy;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return MekanismRenderer.energyIcon;
    }

    @Override
    public Component getText() {
        return EnergyDisplay.of(energy, maxEnergy).getTextComponent();
    }
}