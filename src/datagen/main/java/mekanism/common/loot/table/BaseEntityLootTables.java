package mekanism.common.loot.table;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import mekanism.api.providers.IEntityTypeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

public abstract class BaseEntityLootTables extends SimpleFabricLootTableProvider {

    private final Set<EntityType<?>> knownEntityTypes = new ReferenceOpenHashSet<>();

    protected BaseEntityLootTables(FabricDataOutput output) {
        super(output, LootContextParamSets.ENTITY);
    }

    protected void add(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer, @NotNull EntityType<?> type, @NotNull LootTable.Builder table) {
        //Overwrite the core register method to add to our list of known entity types
        //Note: This isn't the actual core method as that one takes a ResourceLocation, but all our things wil pass through this one
        biConsumer.accept(BuiltInRegistries.ENTITY_TYPE.getKey(type).withPrefix("entities/"), table);
        knownEntityTypes.add(type);
    }

    protected void add(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer, @NotNull IEntityTypeProvider typeProvider, @NotNull LootTable.Builder table) {
        add(biConsumer, typeProvider.getEntityType(), table);
    }
}