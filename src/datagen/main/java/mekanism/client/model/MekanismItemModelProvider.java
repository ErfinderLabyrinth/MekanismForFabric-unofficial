package mekanism.client.model;

import com.google.common.collect.Table.Cell;
import mekanism.common.Mekanism;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MekanismItemModelProvider extends BaseItemModelProvider {

    public MekanismItemModelProvider(FabricDataOutput output) {
        super(output, Mekanism.MODID);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        registerBuckets(itemModelGenerator, MekanismFluids.FLUIDS);
        registerModules(itemModelGenerator, MekanismItems.ITEMS);
        for (Cell<ResourceType, PrimaryResource, ItemRegistryObject<Item>> item : MekanismItems.PROCESSED_RESOURCES.cellSet()) {
            ResourceLocation texture = itemTexture(item.getValue());
            if (textureExists(texture)) {
                generated(itemModelGenerator, item.getValue().asItem());
            } else {
                //If the texture does not exist fallback to the default texture
                resource(itemModelGenerator, item.getValue(), item.getRowKey().getRegistryName());
            }
        }
    }
}