package mekanism.common.config;

import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "usage")
public class UsageConfig extends BaseMekanismConfig {

//    private static final String TELEPORTER_CATEGORY = "teleporter";

    public long enrichmentChamber = 50;
    public long osmiumCompressor = 100;
    public long combiner = 50;
    public long crusher = 50;
    public long metallurgicInfuser = 50;
    public long purificationChamber = 200;
    public long energizedSmelter = 50;
    public long digitalMiner = 1_000;
    public long electricPump = 100;
    public long chargePad = 1_024_000;
    public long rotaryCondensentrator = 50;
    public long oxidationChamber = 200;
    public long chemicalInfuser = 200;
    public long chemicalInjectionChamber = 400;
    public long precisionSawmill = 50;
    public long chemicalDissolutionChamber = 400;
    public long chemicalWasher = 200;
    public long chemicalCrystallizer = 400;
    public long seismicVibrator = 50;
    public long pressurizedReactionBase = 5;
    public long fluidicPlenisher = 100;
    public long laser = 10_000;
    public long formulaicAssemblicator = 100;
    public long modificationStation = 100;
    public long isotopicCentrifuge = 200;
    public long nutritionalLiquifier = 200;
    public long antiprotonicNucleosynthesizer = 100_000;
    public long pigmentExtractor = 200;
    public long pigmentMixer = 200;
    public long paintingMachine = 100;
    public long dimensionalStabilizer = 5_000;

    public long teleporterBase = 1_000;
    public long teleporterDistance = 10;
    public long teleporterDimensionPenalty = 10_000;

    UsageConfig() {
//        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
//        builder.comment("Machine Energy Usage Config. This config is synced from server to client.").push("usage");
//
//        enrichmentChamber = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "enrichmentChamber",
//              FloatingLong.createConst(50));
//        osmiumCompressor = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "osmiumCompressor",
//              FloatingLong.createConst(100));
//        combiner = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "combiner", FloatingLong.createConst(50));
//        crusher = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "crusher", FloatingLong.createConst(50));
//        metallurgicInfuser = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "metallurgicInfuser",
//              FloatingLong.createConst(50));
//        purificationChamber = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "purificationChamber",
//              FloatingLong.createConst(200));
//        energizedSmelter = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "energizedSmelter",
//              FloatingLong.createConst(50));
//        digitalMiner = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "digitalMiner",
//              FloatingLong.createConst(1_000));
//        electricPump = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "electricPump",
//              FloatingLong.createConst(100));
//        chargePad = CachedFloatingLongValue.define(this, builder, "Energy that can be transferred at once per charge operation (Joules).", "chargePad",
//              FloatingLong.createConst(1_024_000));
//        rotaryCondensentrator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "rotaryCondensentrator",
//              FloatingLong.createConst(50));
//        oxidationChamber = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "oxidationChamber",
//              FloatingLong.createConst(200));
//        chemicalInfuser = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "chemicalInfuser",
//              FloatingLong.createConst(200));
//        chemicalInjectionChamber = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "chemicalInjectionChamber",
//              FloatingLong.createConst(400));
//        precisionSawmill = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "precisionSawmill",
//              FloatingLong.createConst(50));
//        chemicalDissolutionChamber = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "chemicalDissolutionChamber",
//              FloatingLong.createConst(400));
//        chemicalWasher = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "chemicalWasher",
//              FloatingLong.createConst(200));
//        chemicalCrystallizer = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "chemicalCrystallizer",
//              FloatingLong.createConst(400));
//        seismicVibrator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "seismicVibrator",
//              FloatingLong.createConst(50));
//        pressurizedReactionBase = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "pressurizedReactionBase",
//              FloatingLong.createConst(5));
//        fluidicPlenisher = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "fluidicPlenisher",
//              FloatingLong.createConst(100));
//        laser = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "laser", FloatingLong.createConst(10_000));
//        formulaicAssemblicator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "formulaicAssemblicator",
//              FloatingLong.createConst(100));
//        modificationStation = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "modificationStation",
//              FloatingLong.createConst(100));
//        isotopicCentrifuge = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "isotopicCentrifuge",
//              FloatingLong.createConst(200));
//        nutritionalLiquifier = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "nutritionalLiquifier",
//              FloatingLong.createConst(200));
//        antiprotonicNucleosynthesizer = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "antiprotonicNucleosynthesizer",
//              FloatingLong.createConst(100_000));
//        pigmentExtractor = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "pigmentExtractor",
//              FloatingLong.createConst(200));
//        pigmentMixer = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "pigmentMixer",
//              FloatingLong.createConst(200));
//        paintingMachine = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "paintingMachine",
//              FloatingLong.createConst(100));
//        dimensionalStabilizer = CachedFloatingLongValue.define(this, builder, "Energy per chunk per tick (Joules).", "dimensionalStabilizer",
//              FloatingLong.createConst(5_000));
//
//        builder.comment("Teleporter").push(TELEPORTER_CATEGORY);
//
//        teleporterBase = CachedFloatingLongValue.define(this, builder, "Base Joules cost for a teleportation.", "teleporterBase", FloatingLong.createConst(1_000));
//        teleporterDistance = CachedFloatingLongValue.define(this, builder, "Joules per unit of distance travelled during teleportation - sqrt(xDiff^2 + yDiff^2 + zDiff^2).",
//              "teleporterDistance", FloatingLong.createConst(10));
//        teleporterDimensionPenalty = CachedFloatingLongValue.define(this, builder, "Flat additional cost for interdimensional teleportation. Distance is still taken into account minimizing energy cost based on dimension scales.",
//              "teleporterDimensionPenalty", FloatingLong.createConst(10_000));
//
//        builder.pop();
//
//        builder.pop();
//        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-usage";
    }

    @Override
    public boolean addToContainer() {
        return false;
    }
}