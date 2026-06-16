package mekanism.common.config;

import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "storage")
public class StorageConfig extends BaseMekanismConfig {

    public long enrichmentChamber = 20_000;
    public long osmiumCompressor = 80_000;
    public long combiner = 40_000;
    public long crusher = 20_000;
    public long metallurgicInfuser = 20_000;
    public long purificationChamber = 80_000;
    public long energizedSmelter = 20_000;
    public long digitalMiner = 50_000;
    public long electricPump = 40_000;
    public long chargePad = 2_048_000;
    public long rotaryCondensentrator = 20_000;
    public long oxidationChamber = 80_000;
    public long chemicalInfuser = 80_000;
    public long chemicalInjectionChamber = 160_000;
    public long electrolyticSeparator = 160_000;
    public long precisionSawmill = 20_000;
    public long chemicalDissolutionChamber = 160_000;
    public long chemicalWasher = 80_000;
    public long chemicalCrystallizer = 160_000;
    public long seismicVibrator = 20_000;
    public long pressurizedReactionBase = 2_000;
    public long fluidicPlenisher = 40_000;
    public long laser = 2_000_000;
    public long laserAmplifier = 5_000_000_000L;
    public long laserTractorBeam = 5_000_000_000L;
    public long formulaicAssemblicator = 40_000;
    public long teleporter = 5_000_000;
    public long modificationStation = 40_000;
    public long isotopicCentrifuge = 80_000;
    public long nutritionalLiquifier = 40_000;
    public long antiprotonicNucleosynthesizer = 1_000_000_000;
    public long pigmentExtractor = 40_000;
    public long pigmentMixer = 80_000;
    public long paintingMachine = 40_000;
    public long spsPort = 1_000_000_000;
    public long dimensionalStabilizer = 40_000;

    StorageConfig() {
//        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
//        builder.comment("Machine Energy Storage Config. This config is synced from server to client.").push("storage");
//
//        enrichmentChamber = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "enrichmentChamber",
//              FloatingLong.createConst(20_000));
//        osmiumCompressor = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "osmiumCompressor",
//              FloatingLong.createConst(80_000));
//        combiner = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "combiner",
//              FloatingLong.createConst(40_000));
//        crusher = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "crusher",
//              FloatingLong.createConst(20_000));
//        metallurgicInfuser = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "metallurgicInfuser",
//              FloatingLong.createConst(20_000));
//        purificationChamber = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "purificationChamber",
//              FloatingLong.createConst(80_000));
//        energizedSmelter = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "energizedSmelter",
//              FloatingLong.createConst(20_000));
//        digitalMiner = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "digitalMiner",
//              FloatingLong.createConst(50_000));
//        electricPump = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "electricPump",
//              FloatingLong.createConst(40_000));
//        chargePad = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chargePad", FloatingLong.createConst(2_048_000));
//        rotaryCondensentrator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "rotaryCondensentrator",
//              FloatingLong.createConst(20_000));
//        oxidationChamber = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "oxidationChamber",
//              FloatingLong.createConst(80_000));
//        chemicalInfuser = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chemicalInfuser",
//              FloatingLong.createConst(80_000));
//        chemicalInjectionChamber = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chemicalInjectionChamber",
//              FloatingLong.createConst(160_000));
//        electrolyticSeparator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "electrolyticSeparator",
//              FloatingLong.createConst(160_000));
//        precisionSawmill = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "precisionSawmill",
//              FloatingLong.createConst(20_000));
//        chemicalDissolutionChamber = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chemicalDissolutionChamber",
//              FloatingLong.createConst(160_000));
//        chemicalWasher = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chemicalWasher",
//              FloatingLong.createConst(80_000));
//        chemicalCrystallizer = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "chemicalCrystallizer",
//              FloatingLong.createConst(160_000));
//        seismicVibrator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "seismicVibrator",
//              FloatingLong.createConst(20_000));
//        pressurizedReactionBase = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "pressurizedReactionBase",
//              FloatingLong.createConst(2_000));
//        fluidicPlenisher = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "fluidicPlenisher",
//              FloatingLong.createConst(40_000));
//        laser = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "laser",
//              FloatingLong.createConst(2_000_000));
//        laserAmplifier = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "laserAmplifier",
//              FloatingLong.createConst(5_000_000_000L));
//        laserTractorBeam = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "laserTractorBeam",
//              FloatingLong.createConst(5_000_000_000L));
//        formulaicAssemblicator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "formulaicAssemblicator",
//              FloatingLong.createConst(40_000));
//        teleporter = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "teleporter",
//              FloatingLong.createConst(5_000_000));
//        modificationStation = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "modificationStation",
//              FloatingLong.createConst(40_000));
//        isotopicCentrifuge = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "isotopicCentrifuge",
//              FloatingLong.createConst(80_000));
//        nutritionalLiquifier = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "nutritionalLiquifier",
//              FloatingLong.createConst(40_000));
//        antiprotonicNucleosynthesizer = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules). Also defines max process rate.", "antiprotonicNucleosynthesizer",
//              FloatingLong.createConst(1_000_000_000));
//        pigmentExtractor = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "pigmentExtractor",
//              FloatingLong.createConst(40_000));
//        pigmentMixer = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "pigmentMixer",
//              FloatingLong.createConst(80_000));
//        paintingMachine = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "paintingMachine",
//              FloatingLong.createConst(40_000));
//        spsPort = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules). Also defines max output rate.", "spsPort",
//              FloatingLong.createConst(1_000_000_000));
//        dimensionalStabilizer = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "dimensionalStabilizer",
//              FloatingLong.createConst(40_000));
//
//        builder.pop();
//        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-storage";
    }

    @Override
    public boolean addToContainer() {
        return false;
    }
}