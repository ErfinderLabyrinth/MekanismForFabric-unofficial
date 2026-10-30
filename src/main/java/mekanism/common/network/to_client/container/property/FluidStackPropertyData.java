package mekanism.common.network.to_client.container.property;

import mekanism.api.FluidStack;
import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class FluidStackPropertyData extends PropertyData {

    @NotNull
    private final FluidStack value;

    public FluidStackPropertyData(short property, @NotNull FluidStack value) {
        super(PropertyType.FLUID_STACK, property);
        this.value = value;
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(FriendlyByteBuf buffer) {
        super.writeToPacket(buffer);
        value.writeToBuffer(buffer);
    }
}