package mekanism.api;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.EmptyGas;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.EmptyInfuseType;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.EmptyPigment;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.EmptySlurry;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.gear.ModuleData;
import mekanism.api.robit.RobitSkin;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@NothingNullByDefault
public class MekanismAPI {

    private MekanismAPI() {
    }

    /**
     * The version of the api classes - may not always match the mod's version
     */
    public static final String API_VERSION = "10.4.0";
    /**
     * Mekanism's Mod ID
     */
    public static final String MEKANISM_MODID = "mekanism";
    /**
     * Mekanism debug mode
     */
    public static boolean debug = false;
    /**
     * Logger for use in Mekanism's API classes
     */
    public static final Logger logger = LogUtils.getLogger();

    private static <T> ResourceKey<Registry<T>> registryKey(@SuppressWarnings("unused") Class<T> compileTimeTypeValidator, String path) {
        return ResourceKey.createRegistryKey(new ResourceLocation(MEKANISM_MODID, path));
    }

    private static <T> ResourceKey<Registry<Codec<? extends T>>> codecRegistryKey(@SuppressWarnings("unused") Class<T> compileTimeTypeValidator, String path) {
        return ResourceKey.createRegistryKey(new ResourceLocation(MEKANISM_MODID, path));
    }

    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link Gas gases}.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<Registry<Gas>> GAS_REGISTRY_NAME = registryKey(Gas.class, "gas");
    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link InfuseType infuse types}.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<Registry<InfuseType>> INFUSE_TYPE_REGISTRY_NAME = registryKey(InfuseType.class, "infuse_type");
    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link Pigment pigments}.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<Registry<Pigment>> PIGMENT_REGISTRY_NAME = registryKey(Pigment.class, "pigment");
    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link Slurry sluries}.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<? extends Registry<Slurry>> SLURRY_REGISTRY_NAME = registryKey(Slurry.class, "slurry");
    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link ModuleData modules}.
     *
     * @since 10.4.0
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ResourceKey<Registry<ModuleData<?>>> MODULE_REGISTRY_NAME = registryKey((Class) ModuleData.class, "module");
    /**
     * Gets the {@link ResourceKey} representing the name of the Datapack Registry for {@link RobitSkin robit skins}.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<Registry<RobitSkin>> ROBIT_SKIN_REGISTRY_NAME = registryKey(RobitSkin.class, "robit_skin");
    /**
     * Gets the {@link ResourceKey} representing the name of the Registry for {@link RobitSkin robit skin} serializers.
     *
     * @since 10.4.0
     */
    public static final ResourceKey<Registry<Codec<? extends RobitSkin>>> ROBIT_SKIN_SERIALIZER_REGISTRY_NAME = codecRegistryKey(RobitSkin.class, "robit_skin_serializer");

    @Nullable
    private static Registry<Gas> GAS_REGISTRY;
    @Nullable
    private static Registry<InfuseType> INFUSE_TYPE_REGISTRY;
    @Nullable
    private static Registry<Pigment> PIGMENT_REGISTRY;
    @Nullable
    private static Registry<Slurry> SLURRY_REGISTRY;
    @Nullable
    private static Registry<ModuleData<?>> MODULE_REGISTRY;
    @Nullable
    private static Registry<Codec<? extends RobitSkin>> ROBIT_SKIN_SERIALIZER_REGISTRY;

    //Note: None of the empty variants support registry replacement
    //TODO: Potentially define these with ObjectHolder for purposes of fully defining them outside of the API
    // would have some minor issues with how the empty stacks are declared
    /**
     * Empty Gas instance.
     */
    public static final Gas EMPTY_GAS = new EmptyGas();
    /**
     * Empty Infuse Type instance.
     */
    public static final InfuseType EMPTY_INFUSE_TYPE = new EmptyInfuseType();
    /**
     * Empty Pigment instance.
     */
    public static final Pigment EMPTY_PIGMENT = new EmptyPigment();
    /**
     * Empty Slurry instance.
     */
    public static final Slurry EMPTY_SLURRY = new EmptySlurry();

    /**
     * Gets the Registry for {@link Gas}.
     *
     * @see #GAS_REGISTRY_NAME
     */
    public static Registry<Gas> gasRegistry() {
        if (GAS_REGISTRY == null) {
            GAS_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getGasRegistry();
        }
        return GAS_REGISTRY;
    }

    /**
     * Gets the Registry for {@link InfuseType}.
     *
     * @see #INFUSE_TYPE_REGISTRY_NAME
     */
    public static Registry<InfuseType> infuseTypeRegistry() {
        if (INFUSE_TYPE_REGISTRY == null) {
            INFUSE_TYPE_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getInfuseTypeRegistry();
        }
        return INFUSE_TYPE_REGISTRY;
    }

    /**
     * Gets the Registry for {@link Pigment}.
     *
     * @see #PIGMENT_REGISTRY_NAME
     */
    public static Registry<Pigment> pigmentRegistry() {
        if (PIGMENT_REGISTRY == null) {
            PIGMENT_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getPigmentRegistry();
        }
        return PIGMENT_REGISTRY;
    }

    /**
     * Gets the Registry for {@link Slurry}.
     *
     * @see #SLURRY_REGISTRY_NAME
     */
    public static Registry<Slurry> slurryRegistry() {
        if (SLURRY_REGISTRY == null) {
            SLURRY_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getSlurryRegistry();
        }
        return SLURRY_REGISTRY;
    }

    /**
     * Gets the Registry for {@link ModuleData}.
     *
     * @see #MODULE_REGISTRY_NAME
     */
    public static Registry<ModuleData<?>> moduleRegistry() {
        if (MODULE_REGISTRY == null) {
            MODULE_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getModuleRegistry();
        }
        return MODULE_REGISTRY;
    }

    /**
     * Gets the Registry for {@link RobitSkin} serializers.
     *
     * @see #ROBIT_SKIN_SERIALIZER_REGISTRY_NAME
     * @since 10.4.0
     */
    public static Registry<Codec<? extends RobitSkin>> robitSkinSerializerRegistry() {
        if (ROBIT_SKIN_SERIALIZER_REGISTRY == null) {
            ROBIT_SKIN_SERIALIZER_REGISTRY = IMekanismAccess.INSTANCE.getRegistries().getRobitSkinSerializerRegistry();
        }
        return ROBIT_SKIN_SERIALIZER_REGISTRY;
    }
}