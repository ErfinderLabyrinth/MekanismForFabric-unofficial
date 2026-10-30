package mekanism.client.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class BaseBlockModelProvider extends FabricModelProvider {
    FabricDataOutput output;
    String modid;

    public BaseBlockModelProvider(FabricDataOutput output, String modid) {
        super(output);
        this.output = output;
        this.modid = modid;
    }

    @NotNull
    @Override
    public String getName() {
        return "Block model provider: " + modid;
    }

    public ResourceLocation sideBottomTop(BlockModelGenerators generators, String name, ResourceLocation parent, ResourceLocation texture) {
        ModelTemplate template = new ModelTemplate(Optional.of(parent), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM, TextureSlot.TOP);
        return template.create(new ResourceLocation(modid, name), new TextureMapping().put(TextureSlot.SIDE, texture).put(TextureSlot.BOTTOM, texture).put(TextureSlot.TOP, texture), generators.modelOutput);
    }

    public boolean textureExists(ResourceLocation texture) {
        return output.getModContainer().findPath("assets/" + texture.getNamespace() + "/textures/" + texture.getPath() + ".png").isPresent();
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {}
}