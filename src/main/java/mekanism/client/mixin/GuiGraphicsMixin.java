package mekanism.client.mixin;

import mekanism.api.providers.IBlockProvider;
import mekanism.client.render.item.MekaSuitBarDecorator;
import mekanism.client.render.item.TransmitterTypeDecorator;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Unique
    List<IBlockProvider> blockProvidersWithDecorations = List.of(MekanismBlocks.BASIC_PRESSURIZED_TUBE, MekanismBlocks.ADVANCED_PRESSURIZED_TUBE,
            MekanismBlocks.ELITE_PRESSURIZED_TUBE, MekanismBlocks.ULTIMATE_PRESSURIZED_TUBE, MekanismBlocks.BASIC_THERMODYNAMIC_CONDUCTOR,
            MekanismBlocks.ADVANCED_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ELITE_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ULTIMATE_THERMODYNAMIC_CONDUCTOR,
            MekanismBlocks.BASIC_UNIVERSAL_CABLE, MekanismBlocks.ADVANCED_UNIVERSAL_CABLE, MekanismBlocks.ELITE_UNIVERSAL_CABLE, MekanismBlocks.ULTIMATE_UNIVERSAL_CABLE);

    List<Item> blockItemsWithDecorations = blockProvidersWithDecorations.stream().map(IBlockProvider::asItem).collect(Collectors.toList());

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void renderCustom(Font font, ItemStack itemStack, int i, int j, String string, CallbackInfo ci) {
        if(itemStack.getItem() == MekanismItems.MEKASUIT_HELMET.asItem()) {
            MekaSuitBarDecorator.INSTANCE.render((GuiGraphics) (Object)this, font, itemStack, i, j);
        }else if(itemStack.getItem() == MekanismItems.MEKASUIT_BODYARMOR.asItem()) {
            MekaSuitBarDecorator.INSTANCE.render((GuiGraphics) (Object)this, font, itemStack, i, j);
        }else if (blockItemsWithDecorations.contains(itemStack.getItem())) {
            IBlockProvider provider = blockProvidersWithDecorations.get(blockItemsWithDecorations.indexOf(itemStack.getItem()));
            new TransmitterTypeDecorator(provider).render((GuiGraphics) (Object)this, font, itemStack, i, j);
        }
    }
}
