package mekanism.common.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import mekanism.api.FluidStack;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.item.gear.ItemCanteen;
import mekanism.common.mixinhelper.ItemPredicateMekanismAddonAccessor;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.util.StorageUtils;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ItemPredicate.class)
public class ItemPredicateMekanismAddonMixin implements ItemPredicateMekanismAddonAccessor {
    @Unique boolean fullCanteen;
    @Unique boolean maxedModuleContainer;

    @Inject(method = "fromJson", at = @At(value = "RETURN", ordinal = 1))
    private static void addMekanismAddonData(JsonElement jsonElement, CallbackInfoReturnable<ItemPredicate> cir, @Local JsonObject jsonObject) {
        if (jsonObject.has("mekanism")) {
            JsonObject mekanismObject = jsonObject.getAsJsonObject("mekanism");
            boolean fullCanteen = false;
            boolean maxedModuleContainer = false;
            if (mekanismObject.has("fullCanteen")) {
                fullCanteen = GsonHelper.getAsBoolean(mekanismObject, "fullCanteen");
            }
            if (mekanismObject.has("maxedModuleContainer")) {
                maxedModuleContainer = GsonHelper.getAsBoolean(mekanismObject, "maxedModuleContainer");
            }

            ItemPredicateMekanismAddonAccessor mekanismPredicate = (ItemPredicateMekanismAddonAccessor) cir.getReturnValue();
            mekanismPredicate.setFullCanteen(fullCanteen);
            mekanismPredicate.setMaxedModuleContainer(maxedModuleContainer);
        }
    }

    @Inject(method = "serializeToJson", at = @At(value = "RETURN", ordinal = 1))
    private void serializeMekanismAddonData(CallbackInfoReturnable<JsonElement> cir, @Local JsonObject jsonObject) {
        if (fullCanteen || maxedModuleContainer) {
            JsonObject mekanismObject = new JsonObject();

            if (fullCanteen) {
                mekanismObject.add("fullCanteen", new JsonPrimitive(true));
            }
            if (maxedModuleContainer) {
                mekanismObject.add("maxedModuleContainer", new JsonPrimitive(true));
            }

            jsonObject.add("mekanism", mekanismObject);
        }
    }

    @Inject(method = "matches", at = @At(value = "RETURN"), cancellable = true)
    private void matchesMekanismAddonData(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (fullCanteen) {
                if (stack.getItem() instanceof ItemCanteen) {
                    FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
                    cir.setReturnValue(fluidStack.isFluidStackIdentical(MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(MekanismConfig.COMMON.gear.canteenMaxStorage)));
                }
                cir.setReturnValue(false);
            }
            if (maxedModuleContainer) {
                Set<ModuleData<?>> supportedModules = IModuleHelper.INSTANCE.getSupported(stack);

                Reference2IntMap<ModuleData<?>> installedCounts = ModuleHelper.get().loadAllCounts(stack);
                if (installedCounts.keySet().containsAll(supportedModules)) {
                    for (Reference2IntMap.Entry<ModuleData<?>> entry : installedCounts.reference2IntEntrySet()) {
                        if (entry.getIntValue() != entry.getKey().getMaxStackSize()) {
                            cir.setReturnValue(false);
                        }
                    }
                    cir.setReturnValue(true);
                }
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public void setFullCanteen(boolean fullCanteen) {
        this.fullCanteen = fullCanteen;
    }

    @Override
    public void setMaxedModuleContainer(boolean maxedModuleContainer) {
        this.maxedModuleContainer = maxedModuleContainer;
    }
}
