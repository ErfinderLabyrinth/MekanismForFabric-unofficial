package mekanism.common.item.gear;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ISpecialGear;
import mekanism.client.render.armor.ISpecialGearGetter;
import mekanism.common.Mekanism;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

public class ItemScubaMask extends ItemSpecialArmor implements ISpecialGearGetter {

    private static final ScubaMaskMaterial SCUBA_MASK_MATERIAL = new ScubaMaskMaterial();

    public ItemScubaMask(Properties properties) {
        super(SCUBA_MASK_MATERIAL, ArmorItem.Type.HELMET, properties.rarity(Rarity.RARE));
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.scubaMask();
    }

//    @Override
//    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
//        return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
//    }

    @NothingNullByDefault
    protected static class ScubaMaskMaterial extends BaseSpecialArmorMaterial {

        @Override
        public String getName() {
            return Mekanism.MODID + ":scuba_mask";
        }
    }
}