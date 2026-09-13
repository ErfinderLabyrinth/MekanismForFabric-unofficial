package mekanism.client.state;

import java.util.Map;
import java.util.Optional;

import mekanism.client.model.MekanismBlockModelProvider;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockOre;
import mekanism.common.block.attribute.AttributeStateActive;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.resource.IResource;
import mekanism.common.resource.ore.OreBlockType;
import mekanism.common.resource.ore.OreType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MekanismBlockStateProvider extends BaseBlockStateProvider<MekanismBlockModelProvider> {
    public final ModelTemplate COLORED_CUBE = new ModelTemplate(Optional.of(modLoc("block/colored_cube")), Optional.empty(), TextureSlot.ALL);

    public MekanismBlockStateProvider(FabricDataOutput output) {
        super(output, Mekanism.MODID, MekanismBlockModelProvider::new);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        registerFluidBlockStates(generators, MekanismFluids.FLUIDS.getAllFluids());

        for (Map.Entry<IResource, BlockRegistryObject<?, ?>> entry : MekanismBlocks.PROCESSED_RESOURCE_BLOCKS.entrySet()) {
            String registrySuffix = entry.getKey().getRegistrySuffix();
            ResourceLocation texture = modLoc("block/block_" + registrySuffix);
            if (models().textureExists(texture)) {
                //If we have an override we can just use a basic cube that has no color tints in it
                createTrivialCube(generators, entry.getValue().getBlock(), "block/storage/" + registrySuffix, texture);
            } else {
                //If the texture does not exist fallback to the default texture and use a colorable base model
                ResourceLocation pathRL = modLoc("block/storage/" + registrySuffix);
                ResourceLocation rl = COLORED_CUBE.create(pathRL, TextureMapping.cube(modLoc("block/resource_block")), generators.modelOutput);
                generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(entry.getValue().getBlock(), rl));
                generators.delegateItemModel(entry.getValue().getBlock(), pathRL);
            }
        }
        for (Map.Entry<OreType, OreBlockType> entry : MekanismBlocks.ORES.entrySet()) {
            String registrySuffix = entry.getKey().getResource().getRegistrySuffix();
            OreBlockType oreBlockType = entry.getValue();
            addOreBlock(generators, oreBlockType.stone(), "block/ore/" + registrySuffix);
            addOreBlock(generators, oreBlockType.deepslate(), "block/deepslate_ore/" + registrySuffix);
        }

//        BlockModelBuilder barrelModel = models().cubeBottomTop(MekanismBlocks.PERSONAL_BARREL.getName(),
//                Mekanism.rl("block/personal_barrel/side"),
//                Mekanism.rl("block/personal_barrel/bottom"),
//                Mekanism.rl("block/personal_barrel/top")
//        );
//        BlockModelBuilder openBarrel = models().getBuilder(MekanismBlocks.PERSONAL_BARREL.getName() + "_open").parent(barrelModel)
//                .texture("top", Mekanism.rl("block/personal_barrel/top_open"));
//        directionalBlock(MekanismBlocks.PERSONAL_BARREL.getBlock(), state -> state.getValue(BlockStateProperties.OPEN) ? openBarrel : barrelModel);
//        simpleBlockItem(MekanismBlocks.PERSONAL_BARREL, barrelModel);

        ResourceLocation barrelModel = ModelTemplates.CUBE_BOTTOM_TOP.create(
                Mekanism.rl("block/" + MekanismBlocks.PERSONAL_BARREL.getName()),
                new TextureMapping()
                        .put(TextureSlot.SIDE, Mekanism.rl("block/personal_barrel/side"))
                        .put(TextureSlot.BOTTOM, Mekanism.rl("block/personal_barrel/bottom"))
               .put(TextureSlot.TOP, Mekanism.rl("block/personal_barrel/top")),
                generators.modelOutput
        );

        ModelTemplate barrelModelTemplate = new ModelTemplate(Optional.of(barrelModel), Optional.empty(), TextureSlot.TOP);

        ResourceLocation openBarrel = barrelModelTemplate.create(
                Mekanism.rl("block/" + MekanismBlocks.PERSONAL_BARREL.getName() + "_open"),
                new TextureMapping()
                    .put(TextureSlot.TOP, Mekanism.rl("block/personal_barrel/top_open")),
                generators.modelOutput
        );

        generators.delegateItemModel(MekanismBlocks.PERSONAL_BARREL.getBlock(), barrelModel);

        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MekanismBlocks.PERSONAL_BARREL.getBlock())
                .with(generators.createColumnWithFacing())
                .with(PropertyDispatch.property(BlockStateProperties.OPEN)
                        .select(
                                false,
                                Variant.variant().with(VariantProperties.MODEL, barrelModel)
                        ).select(
                                true,
                                Variant.variant().with(VariantProperties.MODEL, openBarrel)
                        )
                ));
//
//        TexturedModel barrelModel = new ModelTemplate().cr(MekanismBlocks.PERSONAL_BARREL.getName(),
//              Mekanism.rl("block/personal_barrel/side"),
//              Mekanism.rl("block/personal_barrel/bottom"),
//              Mekanism.rl("block/personal_barrel/top")
//        );
//        BlockModelBuilder openBarrel = models().getBuilder(MekanismBlocks.PERSONAL_BARREL.getName() + "_open").parent(barrelModel)
//              .texture("top", Mekanism.rl("block/personal_barrel/top_open"));
//        //generators.createRotatedVariantBlock();
//        //generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BlockModelGenerators.createRotatedVariant(MekanismBlocks.PERSONAL_BARREL.getBlock(), )));
//        barrelModel.create(MekanismBlocks.PERSONAL_BARREL)
//        directionalBlock(MekanismBlocks.PERSONAL_BARREL.getBlock(), state -> state.getValue(BlockStateProperties.OPEN) ? openBarrel : barrelModel);
//        simpleBlockItem(MekanismBlocks.PERSONAL_BARREL, barrelModel);

        ResourceLocation stabilizerModel = ModelTemplates.CUBE_BOTTOM_TOP.create(
                Mekanism.rl("block/" + MekanismBlocks.DIMENSIONAL_STABILIZER.getName()),
                new TextureMapping()
                        .put(TextureSlot.SIDE, Mekanism.rl("block/dimensional_stabilizer/side"))
                        .put(TextureSlot.BOTTOM, Mekanism.rl("block/dimensional_stabilizer/bottom"))
                        .put(TextureSlot.TOP, Mekanism.rl("block/dimensional_stabilizer/top")),
                generators.modelOutput
        );

//        BlockModelBuilder stabilizerModel = models().cubeBottomTop(MekanismBlocks.DIMENSIONAL_STABILIZER.getName(),
//              Mekanism.rl("block/dimensional_stabilizer/side"),
//              Mekanism.rl("block/dimensional_stabilizer/bottom"),
//              Mekanism.rl("block/dimensional_stabilizer/top")
//        );
//        BlockModelBuilder activeStabilizer = models().getBuilder(MekanismBlocks.DIMENSIONAL_STABILIZER.getName() + "_active").parent(stabilizerModel)
//              .texture("top", Mekanism.rl("block/dimensional_stabilizer/top_active"))
//              .texture("side", Mekanism.rl("block/dimensional_stabilizer/side_active"));

        ModelTemplate stabilizerModelTemplate = new ModelTemplate(Optional.of(barrelModel), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);

        ResourceLocation activeStabilizer = stabilizerModelTemplate.create(
                Mekanism.rl("block/" + MekanismBlocks.DIMENSIONAL_STABILIZER.getName() + "_active"),
                new TextureMapping()
                        .put(TextureSlot.TOP, Mekanism.rl("block/dimensional_stabilizer/top_active"))
                        .put(TextureSlot.SIDE, Mekanism.rl("block/dimensional_stabilizer/side_active")),
                generators.modelOutput
        );

        generators.delegateItemModel(MekanismBlocks.DIMENSIONAL_STABILIZER.getBlock(), stabilizerModel);

        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MekanismBlocks.DIMENSIONAL_STABILIZER.getBlock())
                .with(PropertyDispatch.property(AttributeStateActive.activeProperty)
                        .select(
                                false,
                                Variant.variant().with(VariantProperties.MODEL, stabilizerModel)
                        ).select(
                                true,
                                Variant.variant().with(VariantProperties.MODEL, activeStabilizer)
                        )
                ));

//        simpleBlockItem(, stabilizerModel);
//        getVariantBuilder(MekanismBlocks.DIMENSIONAL_STABILIZER.getBlock())
//              .forAllStates(state -> new ConfiguredModel[]{new ConfiguredModel(Attribute.isActive(state) ? activeStabilizer : stabilizerModel)});
    }

    private void addOreBlock(BlockModelGenerators generators, BlockRegistryObject<BlockOre, ?> oreBlock, String path) {
//        String name = oreBlock.getName();
        createTrivialCube(generators, oreBlock.getBlock(), path);
//        ModelFile file = models().cubeAll(path, modLoc("block/" + name));
//        simpleBlock(oreBlock.getBlock(), file);
//        simpleBlockItem(oreBlock, file);
    }

    private void createTrivialCube(BlockModelGenerators generators, Block block, String path) {
        ResourceLocation pathRL = modLoc(path);
        ResourceLocation rl = ModelTemplates.CUBE_ALL.create(pathRL, TextureMapping.cube(block), generators.modelOutput);
        generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, rl));
        generators.delegateItemModel(block, pathRL);
    }

    private void createTrivialCube(BlockModelGenerators generators, Block block, String path, ResourceLocation texture) {
        ResourceLocation pathRL = modLoc(path);
        ResourceLocation rl = ModelTemplates.CUBE_ALL.create(pathRL, TextureMapping.cube(texture), generators.modelOutput);
        generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, rl));
        generators.delegateItemModel(block, pathRL);
    }
}