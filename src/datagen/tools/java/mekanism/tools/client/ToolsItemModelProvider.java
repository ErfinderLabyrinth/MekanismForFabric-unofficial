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
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
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
                texture = MekanismTools.rl("item/" + name.substring(0, index) + '/' + name.substring(index + 1));
            }
            if (item instanceof ArmorItem armorItem) {
                armorWithTrim(i, texture, armorItem);
            } else {
                handheld(i, texture, item);
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
        ResourceLocation blockingModel = SHIELD_BLOCKING.create(shield.getRegistryName().withPrefix("item/").withSuffix("_blocking"), TextureMapping.singleSlot(TextureSlot.PARTICLE, particle), i.output);

        JsonObject object = SHIELD.createBaseTemplate(shield.getRegistryName(), Map.of(TextureSlot.PARTICLE, particle));
        JsonArray overrides = new JsonArray();
        JsonObject override = new JsonObject();

        JsonObject predicate = new JsonObject();
        predicate.addProperty(MekanismTools.rl("blocking").toString(), 1.0);
        override.add("predicate", predicate);
        override.addProperty("model", blockingModel.toString());
        overrides.add(override);
        object.add("overrides", overrides);

        i.output.accept(shield.getRegistryName().withPrefix("item/"), () -> object);
    }

    private final String[] ARMOR_TRIMS = {
            "quartz",
            "iron",
            "netherite",
            "redstone",
            "copper",
            "gold",
            "emerald",
            "diamond",
            "lapis",
            "amethyst"
    };

    //copy of ItemModelGenerators#armorWithTrim with different texture location
    private void armorWithTrim(ItemModelGenerators gen, ResourceLocation texture, ArmorItem armorItem) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(armorItem);
        ResourceLocation resourceLocation2 = texture;
        ResourceLocation resourceLocation3 = TextureMapping.getItemTexture(armorItem, "_overlay");
        if (armorItem.getMaterial() == ArmorMaterials.LEATHER) {
            ModelTemplates.TWO_LAYERED_ITEM
                    .create(
                            resourceLocation,
                            TextureMapping.layered(resourceLocation2, resourceLocation3),
                            gen.output,
                            (resourceLocationx, map) -> gen.generateBaseArmorTrimTemplate(resourceLocationx, map, armorItem.getMaterial())
                    );
        } else {
            ModelTemplates.FLAT_ITEM
                    .create(
                            resourceLocation,
                            TextureMapping.layer0(resourceLocation2),
                            gen.output,
                            (resourceLocationx, map) -> gen.generateBaseArmorTrimTemplate(resourceLocationx, map, armorItem.getMaterial())
                    );
        }

        for (String string : ARMOR_TRIMS) {
            ResourceLocation resourceLocation4 = gen.getItemModelForTrimMaterial(resourceLocation, string);
            String string2 = armorItem.getType().getName() + "_trim_" + string;
            ResourceLocation resourceLocation5 = new ResourceLocation(string2).withPrefix("trims/items/");
            if (armorItem.getMaterial() == ArmorMaterials.LEATHER) {
                gen.generateLayeredItem(resourceLocation4, resourceLocation2, resourceLocation3, resourceLocation5);
            } else {
                gen.generateLayeredItem(resourceLocation4, resourceLocation2, resourceLocation5);
            }
        }
    }

    private void handheld(ItemModelGenerators gen, ResourceLocation texture, Item item) {
        ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(texture), gen.output);
    }
}