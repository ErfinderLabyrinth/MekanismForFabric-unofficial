package mekanism.additions.common;

import mekanism.additions.client.AdditionsClient;
import mekanism.additions.common.block.BlockObsidianTNT;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.entity.baby.EntityBabyStray;
import mekanism.additions.common.mixin.ParrotAccessor;
import mekanism.additions.common.registries.*;
import mekanism.additions.common.voice.VoiceServerManager;
import mekanism.additions.common.world.modifier.BabyEntitySpawnBiomeModifier;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.lib.Version;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class MekanismAdditions implements IModModule, ModInitializer {

    public static final String MODID = "mekanismadditions";

    public static MekanismAdditions instance;

    /**
     * MekanismTools version number
     */
    public Version versionNumber;

    /**
     * The VoiceServer manager for walkie-talkies
     */
    public static VoiceServerManager voiceManager;

    @Override
    public void onInitialize() {
        Mekanism.addModule(instance = this);
        MekanismAdditionsConfig.registerConfig();
        ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);

        AdditionsItems.register();
        AdditionsBlocks.register();
        AdditionsCreativeTabs.register();
        AdditionsEntityTypes.register();
        AdditionsSounds.register();
        BabyEntitySpawnBiomeModifier.register();

        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(FabricLoader.getInstance().getModContainer(MODID).get());

        //Ensure our tags are all initialized
        AdditionsTags.init();
        //Setup some stuff related to entities
        //Register spawn controls for the baby entities based on the vanilla spawn controls
        registerSpawnControls(AdditionsEntityTypes.BABY_CREEPER, AdditionsEntityTypes.BABY_ENDERMAN, AdditionsEntityTypes.BABY_SKELETON,
                AdditionsEntityTypes.BABY_WITHER_SKELETON);
        //Slightly different restrictions for the baby stray, as strays have a slightly different spawn restriction
        SpawnPlacements.register(AdditionsEntityTypes.BABY_STRAY.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBabyStray::spawnRestrictions);
        //Add parrot sound imitations for baby mobs
        //Note: There is no imitation sound for endermen
        ParrotAccessor.getMobSoundMap().put(AdditionsEntityTypes.BABY_CREEPER.get(), SoundEvents.PARROT_IMITATE_CREEPER);
        ParrotAccessor.getMobSoundMap().put(AdditionsEntityTypes.BABY_SKELETON.get(), SoundEvents.PARROT_IMITATE_SKELETON);
        ParrotAccessor.getMobSoundMap().put(AdditionsEntityTypes.BABY_STRAY.get(), SoundEvents.PARROT_IMITATE_STRAY);
        ParrotAccessor.getMobSoundMap().put(AdditionsEntityTypes.BABY_WITHER_SKELETON.get(), SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
        //Dispenser behavior
        DispenserBlock.registerBehavior(AdditionsBlocks.OBSIDIAN_TNT, new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
                BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                if (BlockObsidianTNT.createAndAddEntity(source.getLevel(), blockpos, null)) {
                    source.getLevel().gameEvent(null, GameEvent.ENTITY_PLACE, blockpos);
                    stack.shrink(1);
                    return stack;
                }
                //Otherwise, if something went very wrong, eject it as a normal item
                return super.execute(source, stack);
            }
        });

        FlammableBlockRegistry.getDefaultInstance().add(AdditionsBlocks.OBSIDIAN_TNT.getBlock(), 15, 75);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MekanismAdditions.MODID, path);
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Additions";
    }

    @Override
    public void resetClient() {
        AdditionsClient.reset();
    }

    @Override
    public void launchClient() {
        AdditionsClient.launch();
    }

    @SafeVarargs
    private static void registerSpawnControls(EntityTypeRegistryObject<? extends Monster>... entityTypeROs) {
        for (EntityTypeRegistryObject<? extends Monster> entityTypeRO : entityTypeROs) {
            SpawnPlacements.register(entityTypeRO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                  Monster::checkMonsterSpawnRules);
        }
    }

    private void serverStarting(MinecraftServer server) {
        if (MekanismAdditionsConfig.additions.voiceServerEnabled) {
            if (voiceManager == null) {
                voiceManager = new VoiceServerManager();
            }
            voiceManager.start(server);
        }
    }

    private void serverStopping(MinecraftServer server) {
        if (voiceManager != null) {
            voiceManager.stop();
            voiceManager = null;
        }
    }
}