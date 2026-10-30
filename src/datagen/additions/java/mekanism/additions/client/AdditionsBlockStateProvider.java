package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.block.BlockGlowPanel;
import mekanism.additions.common.block.plastic.BlockPlasticFenceGate;
import mekanism.additions.common.block.plastic.BlockPlasticStairs;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.api.providers.IBlockProvider;
import mekanism.client.state.BaseBlockStateProvider;
import mekanism.common.item.block.ItemBlockColoredName;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;
import java.util.Optional;

public class AdditionsBlockStateProvider extends BaseBlockStateProvider<AdditionsBlockModelProvider> {

    public AdditionsBlockStateProvider(FabricDataOutput output) {
        super(output, MekanismAdditions.MODID, AdditionsBlockModelProvider::new);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        AdditionsItemModelProvider.generateItemModels(itemModelGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        glowPanels(gen);
        coloredBlocks(gen, AdditionsBlocks.PLASTIC_BLOCKS, "block");
        coloredBlocks(gen, AdditionsBlocks.SLICK_PLASTIC_BLOCKS, "slick");
        coloredBlocks(gen, AdditionsBlocks.PLASTIC_GLOW_BLOCKS, "glow");
        coloredBlocks(gen, AdditionsBlocks.REINFORCED_PLASTIC_BLOCKS, "reinforced");
        coloredBlocks(gen, AdditionsBlocks.PLASTIC_ROADS, "road");
        coloredBlocks(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_BLOCKS, "transparent");
        coloredSlabs(gen, AdditionsBlocks.PLASTIC_SLABS, "", "block");
        coloredStairs(gen, AdditionsBlocks.PLASTIC_STAIRS, "");
        coloredFences(gen, AdditionsBlocks.PLASTIC_FENCES, "");
        coloredFenceGates(gen, AdditionsBlocks.PLASTIC_FENCE_GATES, "");
        coloredSlabs(gen, AdditionsBlocks.PLASTIC_GLOW_SLABS, "glow_", "glow");
        coloredStairs(gen, AdditionsBlocks.PLASTIC_GLOW_STAIRS, "glow_");
        coloredSlabs(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_SLABS, "transparent_", "transparent");
        coloredStairs(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_STAIRS, "transparent_");
    }

    private void glowPanels(BlockModelGenerators gen) {
        ResourceLocation model = modLoc("block/glow_panel");
        for (BlockRegistryObject<BlockGlowPanel, ItemBlockColoredName> blockRO : AdditionsBlocks.GLOW_PANELS.values()) {
            BlockGlowPanel glowPanel = blockRO.getBlock();
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(
                            glowPanel,
                            Variant.variant().with(VariantProperties.MODEL, model)
                    ).with(createDefaultUpFacingDispatch())
            );
        }
    }

    public static PropertyDispatch createDefaultUpFacingDispatch() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.UP, Variant.variant())
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private ModelTemplate modelTemplate(ResourceLocation model) {
        return new ModelTemplate(Optional.of(model), Optional.empty());
    }

    private void coloredBlocks(BlockModelGenerators gen, Map<?, ? extends IBlockProvider> blocks, String modelName) {
        ResourceLocation model = modLoc("block/plastic/" + modelName);
        for (IBlockProvider block : blocks.values()) {
            gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.getBlock(), model));
        }
    }

    private void coloredSlabs(BlockModelGenerators gen, Map<?, ? extends IBlockProvider> slabs, String existingPrefix, String doubleType) {
        ResourceLocation bottomModel = modLoc("block/plastic/" + existingPrefix + "slab");
        ResourceLocation topModel = modLoc("block/plastic/" + existingPrefix + "slab_top");
        ResourceLocation doubleModel = modLoc("block/plastic/" + doubleType);
        for (IBlockProvider slab : slabs.values()) {
            gen.blockStateOutput.accept(BlockModelGenerators.createSlab(slab.getBlock(), bottomModel, topModel, doubleModel));
        }
    }

    private void coloredStairs(BlockModelGenerators gen, Map<?, ? extends BlockRegistryObject<? extends BlockPlasticStairs, ?>> stairs, String existingPrefix) {
        ResourceLocation stairsModel = modLoc("block/plastic/" + existingPrefix + "stairs");
        ResourceLocation stairsInner = modLoc("block/plastic/" + existingPrefix + "stairs_inner");
        ResourceLocation stairsOuter = modLoc("block/plastic/" + existingPrefix + "stairs_outer");

        for (BlockRegistryObject<? extends BlockPlasticStairs, ?> stair : stairs.values()) {
            gen.blockStateOutput.accept(BlockModelGenerators.createStairs(stair.getBlock(), stairsInner, stairsModel, stairsOuter));
            /*BlockPlasticStairs block = stair.getBlock();
            //Copy of BlockStateProvider#stairsBlock, except also ignores our fluid logging extension
            getVariantBuilder(block).forAllStatesExcept(state -> {
                Direction facing = state.getValue(StairBlock.FACING);
                Half half = state.getValue(StairBlock.HALF);
                StairsShape shape = state.getValue(StairBlock.SHAPE);
                int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
                if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                    yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
                }
                if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                    yRot += 90; // Top stairs are rotated 90 degrees clockwise
                }
                yRot %= 360;
                boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
                return ConfiguredModel.builder()
                      .modelFile(shape == StairsShape.STRAIGHT ? stairsModel : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                      .rotationX(half == Half.BOTTOM ? 0 : 180)
                      .rotationY(yRot)
                      .uvLock(uvlock)
                      .build();
            }, StairBlock.WATERLOGGED, block.getFluidLoggedProperty());*/
        }
    }

    private void coloredFences(BlockModelGenerators gen, Map<?, ? extends IBlockProvider> fences, String existingPrefix) {
        ResourceLocation post = modLoc("block/plastic/" + existingPrefix + "fence_post");
        ResourceLocation side = modLoc("block/plastic/" + existingPrefix + "fence_side");
        for (IBlockProvider fence : fences.values()) {
            gen.blockStateOutput.accept(BlockModelGenerators.createFence(fence.getBlock(), post, side));
        }
    }

    private void coloredFenceGates(BlockModelGenerators gen, Map<?, ? extends BlockRegistryObject<? extends BlockPlasticFenceGate, ?>> fenceGates, String existingPrefix) {
        ResourceLocation gate = modLoc("block/plastic/" + existingPrefix + "fence_gate");
        ResourceLocation gateOpen = modLoc("block/plastic/" + existingPrefix + "fence_gate_open");
        ResourceLocation gateWall = modLoc("block/plastic/" + existingPrefix + "fence_gate_wall");
        ResourceLocation gateWallOpen = modLoc("block/plastic/" + existingPrefix + "fence_gate_wall_open");
        for (BlockRegistryObject<? extends BlockPlasticFenceGate, ?> fenceGate : fenceGates.values()) {
            gen.blockStateOutput.accept(BlockModelGenerators.createFenceGate(fenceGate.getBlock(), gateOpen, gate, gateWallOpen, gateWall, true));
            /*BlockPlasticFenceGate block = fenceGate.getBlock();
            getVariantBuilder(block).forAllStatesExcept(state -> {
                ModelFile model = gate;
                if (state.getValue(FenceGateBlock.IN_WALL)) {
                    model = gateWall;
                }
                if (state.getValue(FenceGateBlock.OPEN)) {
                    model = model == gateWall ? gateWallOpen : gateOpen;
                }
                return ConfiguredModel.builder()
                      .modelFile(model)
                      .rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot())
                      .uvLock(true)
                      .build();
            }, FenceGateBlock.POWERED, block.getFluidLoggedProperty());*/
        }
    }
}