package mekanism.common.capabilities;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.merged.IMergedHandler;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.item.ItemEnergized;
import mekanism.common.lib.radiation.capability.DefaultRadiationEntity;
import mekanism.common.storage.item.ItemStorageHandler;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class Capabilities {

    private Capabilities() {
    }

//    public static final Capability<IGasHandler> GAS_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ItemApiLookup<Storage<Gas>, ContainerItemContext> GAS_HANDLER_ITEM = ItemApiLookup.get(new ResourceLocation(Mekanism.MODID, "gas_handler"), Storage.asClass(), ContainerItemContext.class);
    public static final BlockApiLookup<Storage<Gas>, Direction> GAS_HANDLER_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "gas_handler"), Storage.asClass(), Direction.class);

//    public static final Capability<IInfusionHandler> INFUSION_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ItemApiLookup<Storage<InfuseType>, ContainerItemContext> INFUSION_HANDLER_ITEM = ItemApiLookup.get(new ResourceLocation(Mekanism.MODID, "infusion_handler"), Storage.asClass(), ContainerItemContext.class);
    public static final BlockApiLookup<Storage<InfuseType>, Direction> INFUSION_HANDLER_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "infusion_handler"), Storage.asClass(), Direction.class);

//    public static final Capability<IPigmentHandler> PIGMENT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ItemApiLookup<Storage<Pigment>, ContainerItemContext> PIGMENT_HANDLER_ITEM = ItemApiLookup.get(new ResourceLocation(Mekanism.MODID, "pigment_handler"), Storage.asClass(), ContainerItemContext.class);
    public static final BlockApiLookup<Storage<Pigment>, Direction> PIGMENT_HANDLER_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "pigment_handler"), Storage.asClass(), Direction.class);

//    public static final Capability<ISlurryHandler> SLURRY_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ItemApiLookup<Storage<Slurry>, ContainerItemContext> SLURRY_HANDLER_ITEM = ItemApiLookup.get(new ResourceLocation(Mekanism.MODID, "slurry_handler"), Storage.asClass(), ContainerItemContext.class);
    public static final BlockApiLookup<Storage<Slurry>, Direction> SLURRY_HANDLER_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "slurry_handler"), Storage.asClass(), Direction.class);

//    public static final Capability<IHeatHandler> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final BlockApiLookup<IHeatHandler, Direction> HEAT_HANDLER_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "heat_handler"), IHeatHandler.class, Direction.class);

//    public static final Capability<IStrictEnergyHandler> STRICT_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
    @Deprecated
    public static final ItemApiLookup<IStrictEnergyHandler, ContainerItemContext> STRICT_ENERGY_ITEM = ItemApiLookup.get(new ResourceLocation(Mekanism.MODID, "strict_energy"), IStrictEnergyHandler.class, ContainerItemContext.class);
    @Deprecated
    public static final BlockApiLookup<IStrictEnergyHandler, Direction> STRICT_ENERGY_BLOCK = BlockApiLookup.get(new ResourceLocation(Mekanism.MODID, "strict_energy"), IStrictEnergyHandler.class, Direction.class);

//    public static final Capability<IConfigurable> CONFIGURABLE = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<IAlloyInteraction> ALLOY_INTERACTION = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<IConfigCardAccess> CONFIG_CARD = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<IEvaporationSolar> EVAPORATION_SOLAR = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<ILaserReceptor> LASER_RECEPTOR = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<ILaserDissipation> LASER_DISSIPATION = CapabilityManager.get(new CapabilityToken<>() {});
//
//    public static final Capability<IRadiationShielding> RADIATION_SHIELDING = CapabilityManager.get(new CapabilityToken<>() {});

    public static final AttachmentType<? extends IRadiationEntity> RADIATION_ENTITY = DefaultRadiationEntity.ATTACHMENT_TYPE;

//    public static final Capability<IOwnerObject> OWNER_OBJECT = CapabilityManager.get(new CapabilityToken<>() {});
//    public static final Capability<ISecurityObject> SECURITY_OBJECT = CapabilityManager.get(new CapabilityToken<>() {});

    static {
        GAS_HANDLER_ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
                return itemStorageHandler.getGasStorage(context);
            }
            return null;
        });

        GAS_HANDLER_BLOCK.registerFallback((level, blockPos, blockState, blockEntity, direction) -> {
            if (blockEntity instanceof IMergedHandler mergedHandler) return mergedHandler.getGasHandler();
            if (blockEntity instanceof IGasHandler gasHandler) return gasHandler;
            return null;
        });

        INFUSION_HANDLER_ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
                return itemStorageHandler.getInfusionStorage(context);
            }
            return null;
        });

        INFUSION_HANDLER_BLOCK.registerFallback((level, blockPos, blockState, blockEntity, direction) -> {
            if (blockEntity instanceof IMergedHandler mergedHandler) return mergedHandler.getInfusionHandler();
            if (blockEntity instanceof IInfusionHandler infusionHandler) return infusionHandler;
            return null;
        });

        PIGMENT_HANDLER_ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
                return itemStorageHandler.getPigmentStorage(context);
            }
            return null;
        });

        PIGMENT_HANDLER_BLOCK.registerFallback((level, blockPos, blockState, blockEntity, direction) -> {
            if (blockEntity instanceof IMergedHandler mergedHandler) return mergedHandler.getPigmentHandler();
            if (blockEntity instanceof IPigmentHandler pigmentHandler) return pigmentHandler;
            return null;
        });

        SLURRY_HANDLER_ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
                return itemStorageHandler.getSlurryStorage(context);
            }
            return null;
        });

        SLURRY_HANDLER_BLOCK.registerFallback((level, blockPos, blockState, blockEntity, direction) -> {
            if (blockEntity instanceof IMergedHandler mergedHandler) return mergedHandler.getSlurryHandler();
            if (blockEntity instanceof ISlurryHandler slurryHandler) return slurryHandler;
            return null;
        });
    }

    public static void register() {
        DefaultRadiationEntity.register();
    }
}