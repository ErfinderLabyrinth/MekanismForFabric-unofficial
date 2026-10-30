package mekanism.common.item.loot;

import mekanism.api.MekanismAPI;
import mekanism.common.item.loot.PersonalStorageContentsLootFunction.PersonalStorageLootFunctionSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class MekanismLootFunctions {
    public static final Registry<LootItemFunctionType> REGISTER = BuiltInRegistries.LOOT_FUNCTION_TYPE;

    public static final LootItemFunctionType PERSONAL_STORAGE_LOOT_FUNC = Registry.register(REGISTER, new ResourceLocation(MekanismAPI.MEKANISM_MODID, "personal_storage_contents"), new LootItemFunctionType(new PersonalStorageLootFunctionSerializer()));

    public static void register() {

    }
}