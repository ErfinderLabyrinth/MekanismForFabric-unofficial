package mekanism.common.lib.multiblock;

import mekanism.api.*;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.util.StackUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiblockCache<T extends MultiblockData> implements IContentsListener {
//        implements IMekanismInventory, IMekanismFluidHandler, IMekanismStrictEnergyHandler, IMekanismHeatHandler,
//      IGasTracker, IInfusionTracker, IPigmentTracker, ISlurryTracker

    private final List<IInventorySlot> inventorySlots = new ArrayList<>();
    private final List<IExtendedFluidTank> fluidTanks = new ArrayList<>();
    private final List<IGasTank> gasTanks = new ArrayList<>();
    private final List<IInfusionTank> infusionTanks = new ArrayList<>();
    private final List<IPigmentTank> pigmentTanks = new ArrayList<>();
    private final List<ISlurryTank> slurryTanks = new ArrayList<>();
    private final List<IEnergyContainer> energyContainers = new ArrayList<>();
    private final List<IHeatCapacitor> heatCapacitors = new ArrayList<>();

    public void apply(T data) {
        for (CacheSubstance<NBTSerializable<CompoundTag>> type : CacheSubstance.VALUES) {
            List<? extends NBTSerializable<CompoundTag>> containers = type.multiblockDataContainerList(data);
            if (containers != null) {
                List<? extends NBTSerializable<CompoundTag>> cacheContainers = type.multiblockCacheContainerList(this);
                for (int i = 0; i < cacheContainers.size(); i++) {
                    if (i < containers.size()) {
                        //Copy it via NBT to ensure that we set it using the "unsafe" method in case there is a problem with the types somehow
                        containers.get(i).deserializeNBT(cacheContainers.get(i).serializeNBT());
                    }
                }
            }
        }
    }

    public void sync(T data) {
        for (CacheSubstance<NBTSerializable<CompoundTag>> type : CacheSubstance.VALUES) {
            List<? extends NBTSerializable<CompoundTag>> containersToCopy = type.multiblockDataContainerList(data);
            if (containersToCopy != null) {
                List<? extends NBTSerializable<CompoundTag>> cacheContainers = type.multiblockCacheContainerList(this);
                if (cacheContainers.isEmpty()) {
                    type.prefab(this, containersToCopy.size());
                }
                for (int i = 0; i < containersToCopy.size(); i++) {
                    type.sync(cacheContainers.get(i), containersToCopy.get(i));
                }
            }
        }
    }

    public void load(CompoundTag nbtTags) {
        for (CacheSubstance<NBTSerializable<CompoundTag>> type : CacheSubstance.VALUES) {
            int stored = nbtTags.getInt(type.getTagKey() + "_stored");
            if (stored > 0) {
                type.prefab(this, stored);
                DataHandlerUtils.readContainers(type.multiblockCacheContainerList(this), nbtTags.getList(type.getTagKey(), Tag.TAG_COMPOUND));
            }
        }
    }

    public void save(CompoundTag nbtTags) {
        for (CacheSubstance<NBTSerializable<CompoundTag>> type : CacheSubstance.VALUES) {
            List<NBTSerializable<CompoundTag>> containers = type.multiblockCacheContainerList(this);
            if (!containers.isEmpty()) {
                //Note: We can skip putting stored at zero if containers is empty (in addition to skipping actually writing the containers)
                // because getInt will default to 0 for keys that aren't present
                nbtTags.putInt(type.getTagKey() + "_stored", containers.size());
                nbtTags.put(type.getTagKey(), DataHandlerUtils.writeContainers(containers, NBTSerializable::serializeNBT));
            }
        }
    }

    public void merge(MultiblockCache<T> mergeCache, RejectContents rejectContents) {
        // prefab enough containers for each substance type to support the merge cache
        for (CacheSubstance<NBTSerializable<CompoundTag>> type : CacheSubstance.VALUES) {
            type.preHandleMerge(this, mergeCache);
        }

        // Items
        StackUtils.merge(getInventorySlots(null), mergeCache.getInventorySlots(null), rejectContents.rejectedItems);
        // Fluid
        StorageUtils.mergeFluidTanks(getFluidTanks(null), mergeCache.getFluidTanks(null), rejectContents.rejectedFluids);
        // Gas
        StorageUtils.mergeTanks(getGasTanks(null), mergeCache.getGasTanks(null), rejectContents.rejectedGases);
        // Infusion
        StorageUtils.mergeTanks(getInfusionTanks(null), mergeCache.getInfusionTanks(null), rejectContents.rejectedInfuseTypes);
        // Pigment
        StorageUtils.mergeTanks(getPigmentTanks(null), mergeCache.getPigmentTanks(null), rejectContents.rejectedPigments);
        // Slurry
        StorageUtils.mergeTanks(getSlurryTanks(null), mergeCache.getSlurryTanks(null), rejectContents.rejectedSlurries);
        // Energy
        StorageUtils.mergeEnergyContainers(getEnergyContainers(null), mergeCache.getEnergyContainers(null));
        // Heat
        StorageUtils.mergeHeatCapacitors(getHeatCapacitors(null), mergeCache.getHeatCapacitors(null));
    }

    @Override
    public void onContentsChanged() {
    }

    @NotNull
//    @Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return inventorySlots;
    }

    @NotNull
//    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidTanks;
    }

    @NotNull
//    @Override
    public List<IGasTank> getGasTanks(@Nullable Direction side) {
        return gasTanks;
    }

    @NotNull
//    @Override
    public List<IInfusionTank> getInfusionTanks(@Nullable Direction side) {
        return infusionTanks;
    }

    @NotNull
//    @Override
    public List<IPigmentTank> getPigmentTanks(@Nullable Direction side) {
        return pigmentTanks;
    }

    @NotNull
//    @Override
    public List<ISlurryTank> getSlurryTanks(@Nullable Direction side) {
        return slurryTanks;
    }

    @NotNull
//    @Override
    public List<IEnergyContainer> getEnergyContainers(@Nullable Direction side) {
        return energyContainers;
    }

    @NotNull
//    @Override
    public List<IHeatCapacitor> getHeatCapacitors(Direction side) {
        return heatCapacitors;
    }


    public static class RejectContents {

        public final List<ItemStack> rejectedItems = new ArrayList<>();
        public final List<FluidStack> rejectedFluids = new ArrayList<>();
        public final List<GasStack> rejectedGases = new ArrayList<>();
        public final List<InfusionStack> rejectedInfuseTypes = new ArrayList<>();
        public final List<PigmentStack> rejectedPigments = new ArrayList<>();
        public final List<SlurryStack> rejectedSlurries = new ArrayList<>();
    }

    public abstract static class CacheSubstance<ELEMENT> {

        public static final CacheSubstance<IInventorySlot> ITEMS = new CacheSubstance<>(NBTConstants.ITEMS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.inventorySlots.add(BasicInventorySlot.at(cache, 0, 0));
            }

            @Override
            protected List<IInventorySlot> multiblockDataContainerList(MultiblockData inventory) {
                return inventory.inventorySlots;
            }

            @Override
            protected List<IInventorySlot> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.inventorySlots;
            }

            @Override
            public void sync(IInventorySlot cache, IInventorySlot data) {
                cache.setStack(data.getStack());
            }
        };

        public static final CacheSubstance<IExtendedFluidTank> FLUID = new CacheSubstance<>(NBTConstants.FLUID_TANKS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.fluidTanks.add(BasicFluidTank.create(Integer.MAX_VALUE, cache));
            }

            @Override
            protected List<IExtendedFluidTank> multiblockDataContainerList(MultiblockData fluidHandler) {
                return fluidHandler.fluidTanks;
            }

            @Override
            protected List<IExtendedFluidTank> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.fluidTanks;
            }

            @Override
            public void sync(IExtendedFluidTank cache, IExtendedFluidTank data) {
                cache.setStack(data.getFluid());
            }
        };

        public static final CacheSubstance<IGasTank> GAS = new CacheSubstance<>(NBTConstants.GAS_TANKS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.gasTanks.add(ChemicalTankBuilder.GAS.createAllValid(Long.MAX_VALUE, cache));
            }

            @Override
            protected List<IGasTank> multiblockDataContainerList(MultiblockData tracker) {
                return tracker.gasTanks;
            }

            @Override
            protected List<IGasTank> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.gasTanks;
            }

            @Override
            public void sync(IGasTank cache, IGasTank data) {
                cache.setStack(data.getStack());
            }
        };

        public static final CacheSubstance<IInfusionTank> INFUSION = new CacheSubstance<>(NBTConstants.INFUSION_TANKS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.infusionTanks.add(ChemicalTankBuilder.INFUSION.createAllValid(Long.MAX_VALUE, cache));
            }

            @Override
            protected List<IInfusionTank> multiblockDataContainerList(MultiblockData tracker) {
                return tracker.infusionTanks;
            }

            @Override
            protected List<IInfusionTank> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.infusionTanks;
            }

            @Override
            public void sync(IInfusionTank cache, IInfusionTank data) {
                cache.setStack(data.getStack());
            }
        };

        public static final CacheSubstance<IPigmentTank> PIGMENT = new CacheSubstance<>(NBTConstants.PIGMENT_TANKS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.pigmentTanks.add(ChemicalTankBuilder.PIGMENT.createAllValid(Long.MAX_VALUE, cache));
            }

            @Override
            protected List<IPigmentTank> multiblockDataContainerList(MultiblockData tracker) {
                return tracker.pigmentTanks;
            }

            @Override
            protected List<IPigmentTank> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.pigmentTanks;
            }

            @Override
            public void sync(IPigmentTank cache, IPigmentTank data) {
                cache.setStack(data.getStack());
            }
        };

        public static final CacheSubstance<ISlurryTank> SLURRY = new CacheSubstance<>(NBTConstants.SLURRY_TANKS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.slurryTanks.add(ChemicalTankBuilder.SLURRY.createAllValid(Long.MAX_VALUE, cache));
            }

            @Override
            protected List<ISlurryTank> multiblockDataContainerList(MultiblockData tracker) {
                return tracker.slurryTanks;
            }

            @Override
            protected List<ISlurryTank> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.slurryTanks;
            }

            @Override
            public void sync(ISlurryTank cache, ISlurryTank data) {
                cache.setStack(data.getStack());
            }
        };

        public static final CacheSubstance<IEnergyContainer> ENERGY = new CacheSubstance<>(NBTConstants.ENERGY_CONTAINERS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.energyContainers.add(BasicEnergyContainer.create(Long.MAX_VALUE, cache));
            }

            @Override
            protected List<IEnergyContainer> multiblockDataContainerList(MultiblockData handler) {
                return handler.energyContainers;
            }

            @Override
            protected List<IEnergyContainer> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.energyContainers;
            }

            @Override
            public void sync(IEnergyContainer cache, IEnergyContainer data) {
                cache.setEnergy(data.getEnergy());
            }
        };

        public static final CacheSubstance<IHeatCapacitor> HEAT = new CacheSubstance<>(NBTConstants.HEAT_CAPACITORS) {
            @Override
            protected void defaultPrefab(MultiblockCache<?> cache) {
                cache.heatCapacitors.add(BasicHeatCapacitor.create(HeatAPI.DEFAULT_HEAT_CAPACITY, null, cache));
            }

            @Override
            protected List<IHeatCapacitor> multiblockDataContainerList(MultiblockData handler) {
                return handler.heatCapacitors;
            }

            @Override
            protected List<IHeatCapacitor> multiblockCacheContainerList(MultiblockCache<?> handler) {
                return handler.heatCapacitors;
            }

            @Override
            public void sync(IHeatCapacitor cache, IHeatCapacitor data) {
                cache.setHeat(data.getHeat());
                if (cache instanceof BasicHeatCapacitor heatCapacitor) {
                    heatCapacitor.setHeatCapacity(data.getHeatCapacity(), false);
                }
            }
        };

        @SuppressWarnings({"unchecked"})
        public static final CacheSubstance<NBTSerializable<CompoundTag>>[] VALUES = new CacheSubstance[]{
              ITEMS,
              FLUID,
              GAS,
              INFUSION,
              PIGMENT,
              SLURRY,
              ENERGY,
              HEAT
        };

        private final String tagKey;

        public CacheSubstance(String tagKey) {
            this.tagKey = tagKey;
        }

        protected abstract void defaultPrefab(MultiblockCache<?> cache);

        protected abstract List<ELEMENT> multiblockCacheContainerList(MultiblockCache<?> handler);
        protected abstract List<ELEMENT> multiblockDataContainerList(MultiblockData handler);

        private void prefab(MultiblockCache<?> cache, int count) {
            for (int i = 0; i < count; i++) {
                defaultPrefab(cache);
            }
        }

//        public List<ELEMENT> getContainerList(Object holder) {
//            return containerList((HANDLER) holder);
//        }

        public abstract void sync(ELEMENT cache, ELEMENT data);

        public void preHandleMerge(MultiblockCache<?> cache, MultiblockCache<?> merge) {
            int diff = multiblockCacheContainerList(merge).size() - multiblockCacheContainerList(cache).size();
            if (diff > 0) {
                prefab(cache, diff);
            }
        }

        public String getTagKey() {
            return tagKey;
        }
    }
}