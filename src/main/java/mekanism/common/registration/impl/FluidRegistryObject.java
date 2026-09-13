package mekanism.common.registration.impl;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.providers.IFluidProvider;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

@ParametersAreNotNullByDefault
@MethodsReturnNonnullByDefault
public class FluidRegistryObject<STILL extends Fluid, FLOWING extends Fluid, BLOCK extends LiquidBlock, BUCKET extends BucketItem>
      implements IFluidProvider {

    private STILL stillRO;
    private FLOWING flowingRO;
    private BLOCK blockRO;
    private BUCKET bucketRO;
    private FluidDeferredRegister.FluidTypeRenderProperties renderProperties;

    public STILL getStillFluid() {
        return stillRO;
    }

    public FLOWING getFlowingFluid() {
        return flowingRO;
    }

    public BLOCK getBlock() {
        return blockRO;
    }

    public BUCKET getBucket() {
        return bucketRO;
    }

    public FluidDeferredRegister.FluidTypeRenderProperties getRenderProperties() {
        return renderProperties;
    }

    void updateStill(STILL stillRO) {
        this.stillRO = Objects.requireNonNull(stillRO);
    }

    void updateFlowing(FLOWING flowingRO) {
        this.flowingRO = Objects.requireNonNull(flowingRO);
    }

    void updateBlock(BLOCK blockRO) {
        this.blockRO = Objects.requireNonNull(blockRO);
    }

    void updateBucket(BUCKET bucketRO) {
        this.bucketRO = Objects.requireNonNull(bucketRO);
    }

    void updateRenderProperties(FluidDeferredRegister.FluidTypeRenderProperties renderProperties) {
        this.renderProperties = Objects.requireNonNull(renderProperties);
    }

    @Override
    public STILL getFluid() {
        //Default our fluid to being the still variant
        return getStillFluid();
    }
}