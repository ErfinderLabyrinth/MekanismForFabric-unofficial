package mekanism.common.registries;

import mekanism.common.Mekanism;
import mekanism.common.entity.EntityFlame;
import mekanism.common.entity.EntityRobit;
import mekanism.common.registration.impl.EntityTypeDeferredRegister;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MekanismEntityTypes {

    private MekanismEntityTypes() {
    }

    public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister(Mekanism.MODID);

    public static final EntityTypeRegistryObject<EntityFlame> FLAME = ENTITY_TYPES.register("flame", FabricEntityTypeBuilder.create().entityFactory(EntityFlame::new).dimensions(new EntityDimensions(0.5F, 0.5F, false)).fireImmune());
    public static final EntityTypeRegistryObject<EntityRobit> ROBIT = ENTITY_TYPES.register("robit", FabricEntityTypeBuilder.createMob().entityFactory(EntityRobit::new).dimensions(new EntityDimensions(0.6F, 0.65F, false)).fireImmune().disableSummon(), EntityRobit::getDefaultAttributes);

    public static void register() {
        ENTITY_TYPES.registerEntityAttributes();
    }
}
