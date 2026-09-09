package mekanism.additions.common.loot;

import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.common.loot.table.BaseEntityLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class AdditionsEntityLootTables extends BaseEntityLootTables {
    protected AdditionsEntityLootTables(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        //Copy of vanilla's creeper drops
        add(biConsumer, AdditionsEntityTypes.BABY_CREEPER, LootTable.lootTable()
              .withPool(
                    LootPool.lootPool()
                          .setRolls(ConstantValue.exactly(1))
                          .add(LootItem.lootTableItem(Items.GUNPOWDER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                          )
              ).withPool(LootPool.lootPool()
                    .add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS)))
              )
        );
        //Copy of vanilla's enderman drops
        add(biConsumer, AdditionsEntityTypes.BABY_ENDERMAN, LootTable.lootTable()
              .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENDER_PEARL)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                    )
              )
        );
        //Copy of vanilla's skeleton drops
        add(biConsumer, AdditionsEntityTypes.BABY_SKELETON, skeletonDrops());
        //Copy of vanilla's stray drops
        add(biConsumer, AdditionsEntityTypes.BABY_STRAY, skeletonDrops()
              .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.TIPPED_ARROW)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))
                                .setLimit(1)
                          ).apply(SetPotionFunction.setPotion(Potions.SLOWNESS))
                    ).when(LootItemKilledByPlayerCondition.killedByPlayer())
              )
        );
        //Copy of vanilla's wither skeleton drops
        add(biConsumer, AdditionsEntityTypes.BABY_WITHER_SKELETON, LootTable.lootTable()
              .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.COAL)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))
              ).withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.BONE)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))
              ).withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    //Double vanilla's skull drop chance due to being "younger and less brittle"
                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05F, 0.01F))
              )
        );
    }

    /**
     * Copy of vanilla's skeleton drops
     */
    private LootTable.Builder skeletonDrops() {
        return LootTable.lootTable()
              .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ARROW)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))
              ).withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.BONE)
                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                    )
              );
    }
}