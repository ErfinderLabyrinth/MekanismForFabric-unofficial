package mekanism.common.item.predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mekanism.api.FluidStack;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemCanteen;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.util.StorageUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class FullCanteenLootCondition implements LootItemCondition {

    public static final ResourceLocation ID = Mekanism.rl("full_canteen");
    public static final LootItemConditionType TYPE = new LootItemConditionType(new Serializer<LootItemCondition>() {
        @Override
        public void serialize(JsonObject jsonObject, LootItemCondition object, JsonSerializationContext jsonSerializationContext) {

        }

        @Override
        public LootItemCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }
    });
    public static final FullCanteenLootCondition INSTANCE = new FullCanteenLootCondition();

    private FullCanteenLootCondition() {
    }

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        ItemStack stack = lootContext.getParam(LootContextParams.TOOL);
        if (stack.getItem() instanceof ItemCanteen) {
            FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
            return fluidStack.isFluidStackIdentical(MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(MekanismConfig.gear.canteenMaxStorage));
        }
        return false;
    }
}