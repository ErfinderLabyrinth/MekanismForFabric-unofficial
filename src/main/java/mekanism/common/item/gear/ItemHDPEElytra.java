package mekanism.common.item.gear;

import mekanism.common.registries.MekanismItems;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemHDPEElytra extends ElytraItem implements FabricElytraItem {

    public ItemHDPEElytra(Properties properties) {
        super(properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == MekanismItems.HDPE_SHEET.asItem();
    }
}