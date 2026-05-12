package mekanism.common.item.predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import mekanism.api.JsonConstants;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.util.RegistryUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MaxedModuleContainerLootItemCondition<ITEM extends Item & IModuleContainerItem> implements LootItemCondition {

    public static final ResourceLocation ID = Mekanism.rl("maxed_module_container");
    public static final LootItemConditionType TYPE = new LootItemConditionType(new Serializer<MaxedModuleContainerLootItemCondition>() {
        @Override
        public void serialize(JsonObject jsonObject, MaxedModuleContainerLootItemCondition object, JsonSerializationContext jsonSerializationContext) {
            object.serializeToJson(jsonObject);
        }

        @Override
        public MaxedModuleContainerLootItemCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return fromJson(jsonObject);
        }
    });

    private final Set<ModuleData<?>> supportedModules;
    private final ITEM item;

    public MaxedModuleContainerLootItemCondition(ITEM item) {
        this.item = item;
        this.supportedModules = IModuleHelper.INSTANCE.getSupported(new ItemStack(item));
    }

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        ItemStack stack = lootContext.getParam(LootContextParams.TOOL);
        if (stack.getItem() == item) {
            Reference2IntMap<ModuleData<?>> installedCounts = ModuleHelper.get().loadAllCounts(stack);
            if (installedCounts.keySet().containsAll(supportedModules)) {
                for (Reference2IntMap.Entry<ModuleData<?>> entry : installedCounts.reference2IntEntrySet()) {
                    if (entry.getIntValue() != entry.getKey().getMaxStackSize()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @NotNull
    public void serializeToJson(JsonObject object) {
        object.addProperty(JsonConstants.ITEM, RegistryUtils.getName(item).toString());
    }

    public static MaxedModuleContainerLootItemCondition<?> fromJson(JsonObject json) {
        String itemName = GsonHelper.getAsString(json, JsonConstants.ITEM);
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemName));
        if (item instanceof IModuleContainerItem) {
            return new MaxedModuleContainerLootItemCondition<>((Item & IModuleContainerItem) item);
        }
        throw new JsonParseException("Specified item is not a module container item.");
    }
}