package mekanism.client.state;

import java.util.List;
import java.util.function.Function;
import mekanism.api.providers.IBlockProvider;
import mekanism.client.model.BaseBlockModelProvider;
import mekanism.common.DataGenJsonConstants;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.util.RegistryUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBlockStateProvider<PROVIDER extends BaseBlockModelProvider> extends FabricModelProvider {

    private final String modid;
    private final PROVIDER modelProvider;

    public BaseBlockStateProvider(FabricDataOutput output, String modid, Function<FabricDataOutput, PROVIDER> providerCreator) {
        super(output);
        this.modid = modid;
        modelProvider = providerCreator.apply(output);
    }

    @NotNull
    @Override
    public String getName() {
        return "Block state provider: " + modid;
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(modid, name);
    }

    public PROVIDER models() {
        return modelProvider;
    }

//    protected VariantBlockStateBuilder getVariantBuilder(IBlockProvider blockProvider) {
//        return getVariantBuilder(blockProvider.getBlock());
//    }
//
    protected void registerFluidBlockStates(BlockModelGenerators generators, List<FluidRegistryObject<?, ?, ?, ?>> fluidROs) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : fluidROs) {
            TextureMapping textures = TextureMapping.cube(fluidRO.getBlock()).put(TextureSlot.PARTICLE, fluidRO.getRenderProperties().stillTexture);
            generators.createTrivialBlock(fluidRO.getBlock(), textures, ModelTemplates.CUBE_ALL);
        }
    }
//
//    /**
//     * Like directionalBlock but allows us to skip specific properties
//     */
//    protected void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset, Property<?>... toSkip) {
//        getVariantBuilder(block).forAllStatesExcept(state -> {
//            Direction dir = state.getValue(BlockStateProperties.FACING);
//            return ConfiguredModel.builder()
//                  .modelFile(modelFunc.apply(state))
//                  .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
//                  .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
//                  .build();
//        }, toSkip);
//    }
//
//    protected void simpleBlockItem(IBlockProvider block, ModelFile model) {
//        super.simpleBlockItem(block.getBlock(), model);
//    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        //Unused
    }
}