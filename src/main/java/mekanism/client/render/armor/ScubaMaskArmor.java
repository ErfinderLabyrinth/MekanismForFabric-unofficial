package mekanism.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.model.ModelScubaMask;
import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ScubaMaskArmor implements ArmorRenderer, ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = new ResourceLocation(Mekanism.MODID, "scuba_mask_armor");
    public static final ScubaMaskArmor SCUBA_MASK = new ScubaMaskArmor();

    private ModelScubaMask model;

    private ScubaMaskArmor() {
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        model = new ModelScubaMask(Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void render(PoseStack matrix, MultiBufferSource renderer, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> baseModel) {
        if (!baseModel.head.visible) {
            //If the head model shouldn't show don't bother displaying it
            return;
        }
        if (baseModel.young) {
            matrix.pushPose();
            if (baseModel.scaleHead) {
                float f = 1.5F / baseModel.babyHeadScale;
                matrix.scale(f, f, f);
            }
            matrix.translate(0.0D, baseModel.babyYHeadOffset / 16.0F, baseModel.babyZHeadOffset / 16.0F);
            renderMask(baseModel, matrix, renderer, light, OverlayTexture.NO_OVERLAY, stack.hasFoil());
            matrix.popPose();
        } else {
            renderMask(baseModel, matrix, renderer, light, OverlayTexture.NO_OVERLAY, stack.hasFoil());
        }
    }

    private void renderMask(HumanoidModel<? extends LivingEntity> baseModel, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light,
          int overlayLight, boolean hasEffect) {
        matrix.pushPose();
        baseModel.head.translateAndRotate(matrix);
        matrix.translate(0, 0, 0.01);
        model.render(matrix, renderer, light, overlayLight, hasEffect);
        matrix.popPose();
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}