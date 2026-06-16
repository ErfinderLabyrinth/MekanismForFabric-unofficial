package mekanism.common.registration.impl;

import mekanism.common.Mekanism;
import mekanism.common.registration.WrappedDeferredRegister;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityTypeDeferredRegister extends WrappedDeferredRegister<EntityType<?>> {

    private Map<EntityTypeRegistryObject<? extends LivingEntity>, Supplier<Builder>> livingEntityAttributes = new HashMap<>();

    String modid;
    public EntityTypeDeferredRegister(String modid) {
        super(BuiltInRegistries.ENTITY_TYPE);
        this.modid = modid;
    }

    public <ENTITY extends LivingEntity> EntityTypeRegistryObject<ENTITY> register(String name, EntityType.Builder<ENTITY> builder, Supplier<Builder> attributes) {
        EntityTypeRegistryObject<ENTITY> entityTypeRO = register(name, builder);
        livingEntityAttributes.put(entityTypeRO, attributes);
        return entityTypeRO;
    }

    public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> register(String name, EntityType.Builder<ENTITY> builder) {
        return register(new ResourceLocation(modid, name), () -> builder.build(name), EntityTypeRegistryObject::new);
    }

    @Override
    public void register() {
        super.register();
        registerEntityAttributes();
    }

    public void registerEntityAttributes() {
        if (livingEntityAttributes == null) {
            Mekanism.logger.error("GlobalEntityTypeAttributes have already been set. This should not happen.");
        } else {
            //Register our living entity attributes
            for (Map.Entry<EntityTypeRegistryObject<? extends LivingEntity>, Supplier<Builder>> entry : livingEntityAttributes.entrySet()) {
                FabricDefaultAttributeRegistry.register(
                        entry.getKey().get(),
                        entry.getValue().get().build()
                );
            }
            //And set the map to null to allow it to be garbage collected
            livingEntityAttributes = null;
        }
    }
}