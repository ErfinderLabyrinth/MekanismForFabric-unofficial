package mekanism.api.datagen.tag;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

/**
 * Helper classes for implementing tagSupplier providers for various chemical types.
 */
public abstract class ChemicalTagsProvider<CHEMICAL extends Chemical<CHEMICAL>> extends IntrinsicHolderTagsProvider<CHEMICAL> {

    protected ChemicalTagsProvider(PackOutput packOutput, ResourceKey<? extends Registry<CHEMICAL>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, registryKey, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()),
              chemical -> ResourceKey.create(registryKey, chemical.getRegistryName()));
    }

    public abstract static class GasTagsProvider extends ChemicalTagsProvider<Gas> {

        protected GasTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, MekanismAPI.GAS_REGISTRY_NAME, lookupProvider);
        }
    }

    public abstract static class InfuseTypeTagsProvider extends ChemicalTagsProvider<InfuseType> {

        protected InfuseTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, MekanismAPI.INFUSE_TYPE_REGISTRY_NAME, lookupProvider);
        }
    }

    public abstract static class PigmentTagsProvider extends ChemicalTagsProvider<Pigment> {

        protected PigmentTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, MekanismAPI.PIGMENT_REGISTRY_NAME, lookupProvider);
        }
    }

    public abstract static class SlurryTagsProvider extends ChemicalTagsProvider<Slurry> {

        protected SlurryTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, MekanismAPI.SLURRY_REGISTRY_NAME, lookupProvider);
        }
    }
}