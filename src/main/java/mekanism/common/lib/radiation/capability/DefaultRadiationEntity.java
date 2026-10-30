package mekanism.common.lib.radiation.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.NBTConstants;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.common.Mekanism;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.registries.MekanismDamageTypes;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class DefaultRadiationEntity implements IRadiationEntity {
    public static final Codec<DefaultRadiationEntity> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf(NBTConstants.RADIATION).forGetter(DefaultRadiationEntity::getRadiation)
            ).apply(instance, DefaultRadiationEntity::new));

    public static final AttachmentType<DefaultRadiationEntity> ATTACHMENT_TYPE = AttachmentRegistry.<DefaultRadiationEntity>builder()
            .copyOnDeath()
            .initializer(() -> new DefaultRadiationEntity())
            .persistent(CODEC)
            .buildAndRegister(new ResourceLocation(Mekanism.MODID, "radiation"));

    private double radiation = RadiationManager.BASELINE;

    public DefaultRadiationEntity() {

    }

    public DefaultRadiationEntity(double radiation) {
        set(radiation);
    }

    @Override
    public double getRadiation() {
        return radiation;
    }

    @Override
    public void radiate(double magnitude) {
        if (magnitude > 0) {
            radiation += magnitude;
        }
    }

    @Override
    public void update(@NotNull LivingEntity entity) {
        if (entity instanceof Player player && !MekanismUtils.isPlayingMode(player)) {
            return;
        }

        RandomSource rand = entity.level().getRandom();
        double minSeverity = MekanismConfig.COMMON.general.radiationNegativeEffectsMinSeverity;
        double severityScale = RadiationScale.getScaledDoseSeverity(radiation);
        double chance = minSeverity + rand.nextDouble() * (1 - minSeverity);

        if (severityScale > chance) {
            //Calculate effect strength based on radiation severity
            float strength = Math.max(1, (float) Math.log1p(radiation));
            //Hurt randomly
            if (rand.nextBoolean()) {
                if (entity instanceof ServerPlayer player) {
                    MinecraftServer server = entity.getServer();
                    int totemTimesUsed = -1;
                    if (server != null && server.isHardcore()) {//Only allow totems to count on hardcore
                        totemTimesUsed = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                    }
                    if (entity.hurt(MekanismDamageTypes.RADIATION.source(entity.level()), strength)) {
                        //If the damage actually went through fire the trigger
                        boolean hardcoreTotem = totemTimesUsed != -1 && totemTimesUsed < player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                        MekanismCriteriaTriggers.DAMAGE.trigger(player, MekanismDamageTypes.RADIATION, hardcoreTotem);
                    }
                } else {
                    entity.hurt(MekanismDamageTypes.RADIATION.source(entity.level()), strength);
                }
            }
            if (entity instanceof ServerPlayer player) {
                player.getFoodData().addExhaustion(strength);
            }
        }
    }

    @Override
    public void set(double magnitude) {
        radiation = Math.max(RadiationManager.BASELINE, magnitude);
    }

    @Override
    public void decay() {
        set(radiation * MekanismConfig.COMMON.general.radiationTargetDecayRate);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag ret = new CompoundTag();
        ret.putDouble(NBTConstants.RADIATION, radiation);
        return ret;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        set(nbt.getDouble(NBTConstants.RADIATION));
    }

    public static void register() {
    }
}
