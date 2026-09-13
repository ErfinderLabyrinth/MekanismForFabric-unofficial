package mekanism.additions.common.registries;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.additions.common.entity.baby.*;
import mekanism.common.registration.impl.EntityTypeDeferredRegister;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;

public class AdditionsEntityTypes {

    private AdditionsEntityTypes() {
    }

    public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister(MekanismAdditions.MODID);

    public static final EntityTypeRegistryObject<EntityBabyCreeper> BABY_CREEPER = ENTITY_TYPES.register("baby_creeper", FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MONSTER).entityFactory(EntityBabyCreeper::new).dimensions(new EntityDimensions(0.6F, 1.7F, false)), Creeper::createAttributes);
    public static final EntityTypeRegistryObject<EntityBabyEnderman> BABY_ENDERMAN = ENTITY_TYPES.register("baby_enderman", FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MONSTER).entityFactory(EntityBabyEnderman::new).dimensions(new EntityDimensions(0.6F, 2.9F, false)), EnderMan::createAttributes);
    public static final EntityTypeRegistryObject<EntityBabySkeleton> BABY_SKELETON = ENTITY_TYPES.register("baby_skeleton", FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MONSTER).entityFactory(EntityBabySkeleton::new).dimensions(new EntityDimensions(0.6F, 1.99F, false)), AbstractSkeleton::createAttributes);
    public static final EntityTypeRegistryObject<EntityBabyStray> BABY_STRAY = ENTITY_TYPES.register("baby_stray", FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MONSTER).entityFactory(EntityBabyStray::new).spawnGroup(MobCategory.MONSTER).dimensions(new EntityDimensions(0.6F, 1.99F, false)), AbstractSkeleton::createAttributes);
    public static final EntityTypeRegistryObject<EntityBabyWitherSkeleton> BABY_WITHER_SKELETON = ENTITY_TYPES.register("baby_wither_skeleton", FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MONSTER).entityFactory(EntityBabyWitherSkeleton::new).fireImmune().dimensions(new EntityDimensions(0.7F, 2.4F, false)), AbstractSkeleton::createAttributes);
    public static final EntityTypeRegistryObject<EntityBalloon> BALLOON = ENTITY_TYPES.register("balloon", FabricEntityTypeBuilder.create().entityFactory(EntityBalloon::new).dimensions(new EntityDimensions(0.4F, 0.45F, false)));
    public static final EntityTypeRegistryObject<EntityObsidianTNT> OBSIDIAN_TNT = ENTITY_TYPES.register("obsidian_tnt", FabricEntityTypeBuilder.create().entityFactory(EntityObsidianTNT::new).fireImmune().dimensions(new EntityDimensions(0.98F, 0.98F, false)));

    public static void register() {
        ENTITY_TYPES.registerEntityAttributes();
    }
}