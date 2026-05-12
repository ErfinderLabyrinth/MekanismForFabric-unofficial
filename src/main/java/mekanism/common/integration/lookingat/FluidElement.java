package mekanism.common.integration.lookingat;

import mekanism.api.FluidStack;
import mekanism.api.math.MathUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidTextureType;
import mekanism.common.MekanismLang;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FluidElement extends LookingAtElement {

    @NotNull
    protected final FluidStack stored;
    protected final int capacity;

    public FluidElement(@NotNull FluidStack stored, int capacity) {
        super(0xFF000000, 0xFFFFFF);
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public int getScaledLevel(int level) {
        if (capacity == 0 || stored.amount() == Integer.MAX_VALUE) {
            return level;
        }
        return MathUtils.clampToInt(level * (double) stored.amount() / capacity);
    }

    @NotNull
    public FluidStack getStored() {
        return stored;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return stored.isEmpty() ? null : MekanismRenderer.getFluidTexture(stored, FluidTextureType.STILL);
    }

    @Override
    public Component getText() {
        long amount = stored.amount();
        if (amount == Long.MAX_VALUE) {
            return MekanismLang.GENERIC_STORED.translate(stored, MekanismLang.INFINITE);
        }
        return MekanismLang.GENERIC_STORED_MB.translate(stored, TextUtils.format(amount));
    }

    @Override
    protected boolean applyRenderColor(GuiGraphics guiGraphics) {
        MekanismRenderer.color(guiGraphics, stored);
        return true;
    }
}