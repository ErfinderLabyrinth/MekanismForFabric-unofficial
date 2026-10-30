package mekanism.tools.common;

import mekanism.common.advancements.BaseAdvancementProvider;
import mekanism.common.advancements.MekanismAdvancements;
import mekanism.tools.common.advancements.ToolsAdvancements;
import mekanism.tools.common.item.ItemMekanismArmor;
import mekanism.tools.common.item.ItemMekanismAxe;
import mekanism.tools.common.item.ItemMekanismHoe;
import mekanism.tools.common.item.ItemMekanismPaxel;
import mekanism.tools.common.item.ItemMekanismPickaxe;
import mekanism.tools.common.item.ItemMekanismShield;
import mekanism.tools.common.item.ItemMekanismShovel;
import mekanism.tools.common.item.ItemMekanismSword;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ToolsAdvancementProvider extends BaseAdvancementProvider {

    public ToolsAdvancementProvider(FabricDataOutput output) {
        super(output, MekanismTools.MODID);
    }

    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer) {
        Advancement root = createPlaceHolder(MekanismAdvancements.ROOT.name());
        Advancement materials = createPlaceHolder(MekanismAdvancements.MATERIALS.name());
        Advancement paxel = advancement(ToolsAdvancements.PAXEL)
              .parent(root)
              .display(ToolsItems.DIAMOND_PAXEL, FrameType.TASK, true)
              .orCriteria("any_paxel", getItems(item -> item instanceof ItemMekanismPaxel))
              .save(consumer);
        Advancement alternateArmor = advancement(ToolsAdvancements.ALTERNATE_ARMOR)
              .parent(materials)
              .display(ToolsItems.OSMIUM_CHESTPLATE, FrameType.TASK, false)
              .orCriteria("armor", getItems(item -> item instanceof ItemMekanismArmor))
              .save(consumer);
        Advancement alternateTools = advancement(ToolsAdvancements.ALTERNATE_TOOLS)
              .parent(materials)
              .display(ToolsItems.OSMIUM_PICKAXE, FrameType.TASK, false)
              .orCriteria("tools", getItems(item -> item instanceof ItemMekanismAxe || item instanceof ItemMekanismHoe || item instanceof ItemMekanismPickaxe ||
                                                    item instanceof ItemMekanismShovel || item instanceof ItemMekanismSword))
              .save(consumer);
        Advancement notEnoughShielding = advancement(ToolsAdvancements.NOT_ENOUGH_SHIELDING)
              .parent(materials)
              .display(ToolsItems.OSMIUM_SHIELD, FrameType.TASK, false)
              .orCriteria("shields", getItems(item -> item instanceof ItemMekanismShield))
              .save(consumer);

        Advancement betterThanNetherite = advancement(ToolsAdvancements.BETTER_THAN_NETHERITE)
              .parent(alternateArmor)
              .display(ToolsItems.REFINED_OBSIDIAN_CHESTPLATE, FrameType.GOAL, false)
              .orCriteria("armor", ToolsItems.REFINED_OBSIDIAN_HELMET,
                    ToolsItems.REFINED_OBSIDIAN_CHESTPLATE,
                    ToolsItems.REFINED_OBSIDIAN_LEGGINGS,
                    ToolsItems.REFINED_OBSIDIAN_BOOTS
              ).save(consumer);
        Advancement lovedByPiglins = advancement(ToolsAdvancements.LOVED_BY_PIGLINS)
              .parent(alternateArmor)
              .display(ToolsItems.REFINED_GLOWSTONE_CHESTPLATE, FrameType.GOAL, false)
              .orCriteria("armor", ToolsItems.REFINED_GLOWSTONE_HELMET,
                    ToolsItems.REFINED_GLOWSTONE_CHESTPLATE,
                    ToolsItems.REFINED_GLOWSTONE_LEGGINGS,
                    ToolsItems.REFINED_GLOWSTONE_BOOTS
              ).save(consumer);
    }

    private ItemLike[] getItems(Predicate<Item> matcher) {
        return getItems(ToolsItems.ITEMS.getAllItems(), matcher);
    }
}