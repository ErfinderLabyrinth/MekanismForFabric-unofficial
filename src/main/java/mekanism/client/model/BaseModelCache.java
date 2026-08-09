package mekanism.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.client.mixinhelper.CustomGeometryHolder;
import mekanism.client.mixinhelper.ModelManagerModelBakeryGetter;
import mekanism.client.model.obj.ObjModel;
import mekanism.client.model.obj.ObjParser;
import mekanism.client.render.lib.Quad;
import mekanism.client.render.lib.QuadUtils;
import mekanism.client.render.lib.Vertex;
import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BaseModelCache {

    private final Map<ResourceLocation, MekanismModelData> modelMap = new Object2ObjectOpenHashMap<>();

    private final String modid;

    protected BaseModelCache(String modid) {
        this.modid = modid;
    }

    private ResourceLocation rl(String path) {
        return new ResourceLocation(modid, path);
    }

    public void onBake(ModelManager modelManager, ModelBakery modelBakery, Map<ResourceLocation, BakedModel> bakedRegistry) {
        modelMap.values().forEach(m -> m.reload(modelBakery, bakedRegistry));
    }

    public void setup(ModelLoadingPlugin.Context context) {
        modelMap.values().forEach(mekanismModelData -> mekanismModelData.setup(context));
    }

    protected OBJModelData registerOBJ(String path) {
        return registerOBJ(rl(path));
    }

    protected OBJModelData registerOBJ(ResourceLocation rl) {
        return register(rl, OBJModelData::new);
    }

    protected JSONModelData registerJSON(String path) {
        return registerJSON(rl(path));
    }

    protected JSONModelData registerJSON(ResourceLocation rl) {
        return register(rl, JSONModelData::new);
    }

    protected JSONModelData registerJSONAndBake(ResourceLocation rl) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelBakery modelBakery = ((ModelManagerModelBakeryGetter)modelManager).getModelBakery();
        ModelBaker baker = modelBakery.new ModelBakerImpl(
              (modelLoc, material) -> material.sprite(),
              rl
        );
        //Register the model
        JSONModelData data = registerJSON(rl);
        //Manually run the JsonModelData#reload logic
        data.bakedModel = baker.bake(rl, BlockModelRotation.X0_Y0);
        if (modelBakery.getModel(rl) instanceof CustomGeometryHolder blockModel) {
            data.model = blockModel.getCustomGeometry();
        }
        return data;
    }

    protected <DATA extends MekanismModelData> DATA register(ResourceLocation rl, Function<ResourceLocation, DATA> creator) {
        DATA data = creator.apply(rl);
        modelMap.put(rl, data);
        return data;
    }

    public static BakedModel getBakedModel(ModelManager modelManager, Map<ResourceLocation, BakedModel> bakedRegistry, ResourceLocation rl) {
        BakedModel bakedModel = bakedRegistry.get(rl);
        if (bakedModel == null) {
            Mekanism.logger.error("Baked model doesn't exist: {}", rl.toString());
            return modelManager.getMissingModel();
        }
        return bakedModel;
    }

    public static class MekanismModelData {

        protected CustomGeometry model;

        protected final ResourceLocation rl;
        private final Map<Set<String>, BakedModel> bakedMap = new Object2ObjectOpenHashMap<>();

        protected MekanismModelData(ResourceLocation rl) {
            this.rl = rl;
        }

        protected void reload(ModelBakery modelBakery, Map<ResourceLocation, BakedModel> bakedRegistry) {
            bakedMap.clear();
        }

        protected void setup(ModelLoadingPlugin.Context context) {
        }

        public BakedModel bake(Set<String> config, BlockModel blockModel) {
            return bakedMap.computeIfAbsent(config, c -> {
                ModelBaker baker = ((ModelManagerModelBakeryGetter)Minecraft.getInstance().getModelManager()).getModelBakery().new ModelBakerImpl(
                      (modelLoc, material) -> material.sprite(),
                      rl
                );
                BakedModel bakedModel = null;
                if(blockModel != null) {
                    bakedModel = blockModel.bake(baker, Material::sprite, BlockModelRotation.X0_Y0, rl);
                }
                return model.bake(blockModel, c, baker, Material::sprite, BlockModelRotation.X0_Y0, ItemOverrides.EMPTY, rl, bakedModel);
            });
        }

        public CustomGeometry getModel() {
            return model;
        }
    }

    public static class OBJModelData extends MekanismModelData {
        private ObjModel objModel;

        protected OBJModelData(ResourceLocation rl) {
            super(rl);
        }

        @Override
        protected void reload(ModelBakery modelBakery, Map<ResourceLocation, BakedModel> bakedRegistry) {
            super.reload(modelBakery, bakedRegistry);
            ObjModel unbakedModel = null; //new ModelSettings(rl, true, useDiffuseLighting(), true, true, null));
            try {
                unbakedModel = ObjParser.load(Minecraft.getInstance().getResourceManager().open(rl), rl.withPath(rl.getPath().substring(0, rl.getPath().lastIndexOf('/'))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            objModel = unbakedModel;
            ObjModel finalUnbakedModel = unbakedModel;
            model = new CustomGeometry() {
                @Override
                public BakedModel bake(BlockModel blockModel, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
                    return finalUnbakedModel.bake(blockModel, spriteGetter).wrapper(parts, alreadyBaked);
                }

                @Override
                public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
                }
            };
        }

        public ObjModel getObjModel() {
            return objModel;
        }

        protected boolean useDiffuseLighting() {
            return true;
        }
    }

    public static class JSONModelData extends MekanismModelData {

        private BakedModel bakedModel;

        private JSONModelData(ResourceLocation rl) {
            super(rl);
        }

        @Override
        protected void reload(ModelBakery modelBakery, Map<ResourceLocation, BakedModel> bakedRegistry) {
            super.reload(modelBakery, bakedRegistry);
            bakedModel = BaseModelCache.getBakedModel(Minecraft.getInstance().getModelManager(), bakedRegistry, rl);
            UnbakedModel unbaked = modelBakery.getModel(rl);
            if (unbaked instanceof CustomGeometryHolder blockModel) {
                model = blockModel.getCustomGeometry();
            }
        }

        @Override
        protected void setup(ModelLoadingPlugin.Context context) {
            context.addModels(rl);
        }

        public void collectQuadVertices(List<Vertex[]> vertices, RandomSource random) {
            for (Quad quad : QuadUtils.unpack(getQuads(random))) {
                vertices.add(quad.getVertices());
            }
        }

        public List<BakedQuad> getQuads(RandomSource random) {
            //TODO: Decide if this should just redirect to the other get quads method (some impls might be different depending on if it gets data and render type vs not)
            return getBakedModel().getQuads(null, null, random);
        }

        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            return getBakedModel().getQuads(state, side, rand);
        }

        public BakedModel getBakedModel() {
            return bakedModel;
        }
    }
}
