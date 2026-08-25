package mekanism.tools.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mekanism.api.providers.IItemProvider;
import mekanism.client.model.BaseItemModelProvider;
import mekanism.common.Mekanism;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.item.ItemMekanismPaxel;
import mekanism.tools.common.item.ItemMekanismShield;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Optional;

public class ToolsItemModelProvider extends BaseItemModelProvider {
    ModelTemplate SHIELD_BLOCKING = new ModelTemplate(Optional.of(new ResourceLocation("item/shield_blocking")), Optional.empty());
    ModelTemplate SHIELD = new ModelTemplate(Optional.of(new ResourceLocation("item/shield")), Optional.empty());

    public ToolsItemModelProvider(FabricDataOutput output) {
        super(output, MekanismTools.MODID);
    }

    @Override
    public void generateItemModels(ItemModelGenerators i) {
        //Shields
        addShieldModel(i, ToolsItems.BRONZE_SHIELD, Mekanism.rl("block/block_bronze"));
        addShieldModel(i, ToolsItems.LAPIS_LAZULI_SHIELD, new ResourceLocation("block/lapis_block"));
        addShieldModel(i, ToolsItems.OSMIUM_SHIELD, Mekanism.rl("block/block_osmium"));
        addShieldModel(i, ToolsItems.REFINED_GLOWSTONE_SHIELD, Mekanism.rl("block/block_refined_glowstone"));
        addShieldModel(i, ToolsItems.REFINED_OBSIDIAN_SHIELD, Mekanism.rl("block/block_refined_obsidian"));
        addShieldModel(i, ToolsItems.STEEL_SHIELD, Mekanism.rl("block/block_steel"));
        //Armor items are generated textures, all other tools module items are handheld
        for (IItemProvider itemProvider : ToolsItems.ITEMS.getAllItems()) {
            Item item = itemProvider.asItem();
            if (item instanceof ItemMekanismShield) {
                //Skip shields, we manually handle them above
                continue;
            }
            ResourceLocation texture;
            if (isVanilla(itemProvider)) {
                texture = itemTexture(itemProvider);
            } else {
                String name = itemProvider.getName();
                int index = name.lastIndexOf('_');
                texture = Mekanism.rl("item/" + name.substring(0, index) + '/' + name.substring(index + 1));
            }
            if (item instanceof ArmorItem armorItem) {
                armorWithTrim(i, armorItem);
            } else {
                handheld(i, item);
            }
        }
    }

    private boolean isVanilla(IItemProvider itemProvider) {
        if (itemProvider.asItem() instanceof ItemMekanismPaxel) {
            String name = itemProvider.getName();
            return name.startsWith("netherite") || name.startsWith("diamond") || name.startsWith("gold") || name.startsWith("iron") ||
                   name.startsWith("stone") || name.startsWith("wood");
        }
        return false;
    }

    private void addShieldModel(ItemModelGenerators i, IItemProvider shield, ResourceLocation particle) {
        ResourceLocation blockingModel = SHIELD_BLOCKING.create(shield.getRegistryName().withSuffix("_blocking"), TextureMapping.singleSlot(TextureSlot.PARTICLE, particle), i.output);

        JsonObject object = SHIELD.createBaseTemplate(shield.getRegistryName(), Map.of(TextureSlot.PARTICLE, particle));
        JsonArray overrides = new JsonArray();
        JsonObject override = new JsonObject();

        JsonObject predicate = new JsonObject();
        predicate.addProperty("blocking", 1);
        override.add("predicate", predicate);
        override.addProperty("model", blockingModel.toString());
        overrides.add(override);
        object.add("overrides", overrides);

        i.output.accept(shield.getRegistryName(), () -> object);
    }
}