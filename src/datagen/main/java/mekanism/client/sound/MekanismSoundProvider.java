package mekanism.client.sound;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import mekanism.common.Mekanism;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class MekanismSoundProvider extends BaseSoundProvider {

    public MekanismSoundProvider(PackOutput output) {
        super(output, Mekanism.MODID);
    }

    @Override
    public void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        addTileSoundEvents(creator);
        addItemSoundEvents(creator);
        addHolidaySoundEvents(creator);
        addGuiSoundEvents(creator);
    }

    private void addTileSoundEvents(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        String basePath = "tile/";
        addSoundEventWithSubtitle(creator, MekanismSounds.CHARGEPAD, basePath + "chargepad");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_CRYSTALLIZER, basePath + "chemical_crystallizer");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_DISSOLUTION_CHAMBER, basePath + "chemical_dissolution_chamber");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_INFUSER, basePath + "chemical_infuser");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_INJECTION_CHAMBER, basePath + "chemical_injection_chamber");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_OXIDIZER, basePath + "chemical_oxidizer");
        addSoundEventWithSubtitle(creator, MekanismSounds.CHEMICAL_WASHER, basePath + "chemical_washer");
        addSoundEventWithSubtitle(creator, MekanismSounds.COMBINER, basePath + "combiner");
        addSoundEventWithSubtitle(creator, MekanismSounds.OSMIUM_COMPRESSOR, basePath + "compressor");
        addSoundEventWithSubtitle(creator, MekanismSounds.CRUSHER, basePath + "crusher");
        addSoundEventWithSubtitle(creator, MekanismSounds.ELECTROLYTIC_SEPARATOR, basePath + "electrolytic_separator");
        addSoundEventWithSubtitle(creator, MekanismSounds.ENRICHMENT_CHAMBER, basePath + "enrichment_chamber");
        addSoundEventWithSubtitle(creator, MekanismSounds.LASER, basePath + "laser");
        addSoundEventWithSubtitle(creator, MekanismSounds.LOGISTICAL_SORTER, basePath + "logistical_sorter");
        addSoundEventWithSubtitle(creator, MekanismSounds.METALLURGIC_INFUSER, basePath + "metallurgic_infuser");
        addSoundEventWithSubtitle(creator, MekanismSounds.PRECISION_SAWMILL, basePath + "precision_sawmill");
        addSoundEventWithSubtitle(creator, MekanismSounds.PRESSURIZED_REACTION_CHAMBER, basePath + "pressurized_reaction_chamber");
        addSoundEventWithSubtitle(creator, MekanismSounds.PURIFICATION_CHAMBER, basePath + "purification_chamber");
        addSoundEventWithSubtitle(creator, MekanismSounds.RESISTIVE_HEATER, basePath + "resistive_heater");
        addSoundEventWithSubtitle(creator, MekanismSounds.ROTARY_CONDENSENTRATOR, basePath + "rotary_condensentrator");
        addSoundEventWithSubtitle(creator, MekanismSounds.ENERGIZED_SMELTER, basePath + "energized_smelter");
        addSoundEventWithSubtitle(creator, MekanismSounds.ISOTOPIC_CENTRIFUGE, basePath + "isotopic_centrifuge");
        addSoundEventWithSubtitle(creator, MekanismSounds.NUTRITIONAL_LIQUIFIER, basePath + "nutritional_liquifier");
        addSoundEventWithSubtitle(creator, MekanismSounds.INDUSTRIAL_ALARM, basePath + "industrial_alarm", sound -> sound.withAttenuationDistance(128));
        addSoundEventWithSubtitle(creator, MekanismSounds.ANTIPROTONIC_NUCLEOSYNTHESIZER, basePath + "antiprotonic_nucleosynthesizer");
        addSoundEventWithSubtitle(creator, MekanismSounds.PAINTING_MACHINE, basePath + "painting_machine");
        addSoundEventWithSubtitle(creator, MekanismSounds.PIGMENT_EXTRACTOR, basePath + "pigment_extractor");
        addSoundEventWithSubtitle(creator, MekanismSounds.PIGMENT_MIXER, basePath + "pigment_mixer");
        addSoundEventWithSubtitle(creator, MekanismSounds.SPS, basePath + "sps");
    }

    private void addItemSoundEvents(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        String basePath = "item/";
        addSoundEventWithSubtitle(creator, MekanismSounds.HYDRAULIC, basePath + "hydraulic");
        addSoundEventWithSubtitle(creator, MekanismSounds.FLAMETHROWER_IDLE, basePath + "flamethrower_idle");
        addSoundEventWithSubtitle(creator, MekanismSounds.FLAMETHROWER_ACTIVE, basePath + "flamethrower_active");
        addSoundEventWithSubtitle(creator, MekanismSounds.SCUBA_MASK, basePath + "scuba_mask");
        addSoundEventWithSubtitle(creator, MekanismSounds.JETPACK, basePath + "jetpack");
        addSoundEventWithSubtitle(creator, MekanismSounds.GRAVITATIONAL_MODULATION_UNIT, basePath + "gravitational_modulation_unit");

        addSoundEventWithSubtitle(creator, MekanismSounds.GEIGER_SLOW, basePath + "geiger_slow");
        addSoundEventWithSubtitle(creator, MekanismSounds.GEIGER_MEDIUM, basePath + "geiger_medium");
        addSoundEventWithSubtitle(creator, MekanismSounds.GEIGER_ELEVATED, basePath + "geiger_elevated");
        addSoundEventWithSubtitle(creator, MekanismSounds.GEIGER_FAST, basePath + "geiger_fast");
    }

    private void addHolidaySoundEvents(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        String basePath = "holiday/";
        //Use the non holiday subtitles
        addSoundEvent(creator, MekanismSounds.CHRISTMAS1, basePath + "nutcracker1", MekanismSounds.ENRICHMENT_CHAMBER);
        addSoundEvent(creator, MekanismSounds.CHRISTMAS2, basePath + "nutcracker2", MekanismSounds.METALLURGIC_INFUSER);
        addSoundEvent(creator, MekanismSounds.CHRISTMAS3, basePath + "nutcracker3", MekanismSounds.PURIFICATION_CHAMBER);
        addSoundEvent(creator, MekanismSounds.CHRISTMAS4, basePath + "nutcracker4", MekanismSounds.ENERGIZED_SMELTER);
        addSoundEvent(creator, MekanismSounds.CHRISTMAS5, basePath + "nutcracker5", MekanismSounds.CRUSHER);
    }

    private void addGuiSoundEvents(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        String basePath = "gui/";
        //Manually call this to skip applying a subtitle
        addSoundEvent(creator, MekanismSounds.BEEP, basePath + "beep", UnaryOperator.identity(), sound -> sound.withPitch(0.8F));
    }
}