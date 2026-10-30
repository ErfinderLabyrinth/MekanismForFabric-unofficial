package mekanism.client.render.obj;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.math.Transformation;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.SimpleModelState;
import mekanism.client.model.data.TransmitterModelData;
import mekanism.client.model.obj.BakedObjModel;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@NothingNullByDefault
public class TransmitterBakedModel implements BakedModel {
    private static final RenderMaterial STANDARD = RendererAccess.INSTANCE.getRenderer().materialFinder().find();
    private static final RenderMaterial NO_AO = RendererAccess.INSTANCE.getRenderer().materialFinder().ambientOcclusion(TriState.FALSE).find();

    private static final RenderMaterial TRANSLUCENT_MATERIAL =
            RendererAccess.INSTANCE.getRenderer()
                    .materialFinder()
                    .blendMode(BlendMode.TRANSLUCENT)
                    .find();

    private final BakedModel original;
    private final BlockModel owner;
    private final ModelBaker baker;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final ModelState modelTransform;
    private final ItemOverrides overrides;
    private final ResourceLocation modelLocation;
    private final LoadingCache<SidedConnection, List<BakedQuad>> internalPartsCache;
    @Nullable
    private final LoadingCache<SidedConnection, List<BakedQuad>> glassPartsCache;
    //TODO: Debate making transmitter models actually have cleanup code and have them also add listeners for opaque transmitters so that when the config
    // changes then these update accordingly
    private final LoadingCache<TransmitterDataKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
        @NotNull
        @Override
        public List<BakedQuad> load(@NotNull TransmitterDataKey key) {
            //Glass cache should never be null if we have renderGlass as true
            LoadingCache<SidedConnection, List<BakedQuad>> partsCache = key.renderGlass ? Objects.requireNonNull(glassPartsCache) : internalPartsCache;
            List<BakedQuad> quads = new ArrayList<>();
            for (Direction side : EnumUtils.DIRECTIONS) {
                ConnectionType connectionType = key.data.getConnectionType(side);
                TransmitterModelConfiguration.IconStatus iconStatus = TransmitterModelConfiguration.getIconStatus(key.data, side, connectionType);
                SidedConnection sidedConnection = new SidedConnection(side, connectionType, iconStatus);
                quads.addAll(partsCache.getUnchecked(sidedConnection));
            }
            return quads;
        }
    });

    public TransmitterBakedModel(BakedObjModel internal, @Nullable BakedObjModel glass, ModelBaker baker,
                                 Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation, BlockModel owner, BakedModel alreadyBaked) {
        //We define our baked variant to be how the item is. As we should always have model data when we have a state
        //TODO new VisibleModelConfiguration(owner, Arrays.stream(EnumUtils.DIRECTIONS).map(side -> getPartName(side, ConnectionType.NONE)).toList())
        original = internal.wrapper(Set.of(getPartName(Direction.DOWN, ConnectionType.NONE), getPartName(Direction.NORTH, ConnectionType.NONE), getPartName(Direction.EAST, ConnectionType.NONE), getPartName(Direction.SOUTH, ConnectionType.NONE), getPartName(Direction.WEST, ConnectionType.NONE), getPartName(Direction.UP, ConnectionType.NONE)), alreadyBaked);
        this.owner = owner;
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
        this.overrides = overrides;
        this.modelLocation = modelLocation;
        this.internalPartsCache = CacheBuilder.newBuilder().build(createPartCacheLoader(internal));
        this.glassPartsCache = glass == null ? null : CacheBuilder.newBuilder().build(createPartCacheLoader(glass));
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        TransmitterModelData data = null;
        BlockEntity blockEntity = blockView.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityTransmitter transmitter) {
            data =  transmitter.getRenderData();
        }

        QuadEmitter emitter = context.getEmitter();
        final RenderMaterial defaultMaterial = useAmbientOcclusion() ? STANDARD : NO_AO;

        for (BakedQuad quad:getQuads(state, null, randomSupplier.get(), false, data)) {
            emitter.fromVanilla(quad, TRANSLUCENT_MATERIAL, quad.getDirection());
            emitter.emit();
        }

        for (BakedQuad quad:getQuads(state, null, randomSupplier.get(), true, data)) {
            emitter.fromVanilla(quad, TRANSLUCENT_MATERIAL, quad.getDirection());
            emitter.emit();
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        TransmitterModelData data = null;
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTileEntity<?> hasTileEntity) {
            BlockEntity blockEntity = hasTileEntity.createDummyBlockEntity();
            if (blockEntity instanceof TileEntityTransmitter transmitter) {
                data = transmitter.getRenderData();
            }
        }

        QuadEmitter emitter = context.getEmitter();
        final RenderMaterial defaultMaterial = useAmbientOcclusion() ? STANDARD : NO_AO;

        for (BakedQuad quad:getQuads(null, null, randomSupplier.get(), false, data)) {
            emitter.fromVanilla(quad, TRANSLUCENT_MATERIAL, quad.getDirection());
            emitter.emit();
        }

        for (BakedQuad quad:getQuads(null, null, randomSupplier.get(), true, data)) {
            emitter.fromVanilla(quad, TRANSLUCENT_MATERIAL, quad.getDirection());
            emitter.emit();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return getQuads(state, side, rand, false, null);
    }

    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, boolean renderGlass, @Nullable TransmitterModelData data) {
        if (side != null) {
            return Collections.emptyList();
        }
        if (data != null) {
            if (renderGlass && (glassPartsCache == null || !data.getHasColor())) {
                //Skip rendering the glass if we don't actually have any glass, or we don't have a color for it
                return Collections.emptyList();
            }
            return cache.getUnchecked(new TransmitterDataKey(data, renderGlass));
        }
        //Fallback to our "default" model arrangement. The item variant uses this
        return original.getQuads(state, null, rand);
    }



    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return original.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return original.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return original.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return original.getOverrides();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }


    //    @Override
//    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
//        return glassPartsCache == null ? CUTOUT : FULL;
//    }
//
//    @Override
//    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
//        if (glassPartsCache == null) {
//            return List.of(Sheets.cutoutBlockSheet());
//        }
//        return List.of(Sheets.cutoutBlockSheet(), fabulous ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
//    }
//
//    @Override
//    public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
//        return Collections.singletonList(this);
//    }

    private static String getPartName(Direction side, ConnectionType connectionType) {
        return side.getSerializedName() + connectionType.name();
    }

    private CacheLoader<SidedConnection, List<BakedQuad>> createPartCacheLoader(BakedObjModel model) {
        return new CacheLoader<>() {
            @NotNull
            @Override
            public List<BakedQuad> load(@NotNull SidedConnection key) {
                Direction side = key.side();
                ConnectionType connectionType = key.connection();
                String part = getPartName(side, connectionType);
                if (!model.hasObject(part)) {
                    //Validate the model actually has the part (this should always be true but if for some reason it isn't short circuit)
                    return Collections.emptyList();
                }
                TransmitterModelConfiguration.IconStatus iconStatus = key.status();
                ModelState transform = modelTransform;
                if (connectionType == ConnectionType.NONE && iconStatus.getAngle() > 0) {
                    //If the part should be rotated, then we need to use a custom IModelTransform
                    Vector3f vecForDirection = Vec3.atLowerCornerOf(side.getNormal()).toVector3f();
                    vecForDirection.mul(-1);
                    Quaternionf quaternion = new Quaternionf().setAngleAxis(iconStatus.getAngle(), vecForDirection.x, vecForDirection.y, vecForDirection.z);
                    Transformation matrix = new Transformation(null, quaternion, null, null);
                    transform = new SimpleModelState(transform.getRotation().compose(matrix), transform.isUvLocked());
                }
                //Note: We don't actually care about the state, or the side anywhere and the model returns the proper values even if we don't provide a render type
                // We also just use a new random source as we don't have one in our current context
                return model.getQuads(Set.of(part));
            }
        };
    }

    private record SidedConnection(Direction side, ConnectionType connection, TransmitterModelConfiguration.IconStatus status) {
    }

    private static class TransmitterDataKey {

        private final TransmitterModelData data;
        private final boolean renderGlass;
        private final int hash;

        public TransmitterDataKey(TransmitterModelData data, boolean renderGlass) {
            this.data = data;
            this.renderGlass = renderGlass;
            this.hash = Objects.hash(this.data.getConnectionsMap(), this.renderGlass);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            //Note: We don't compare data directly as if we aren't rendering glass it not being colored is irrelevant
            // and if we are rendering glass, it will always be colored as we short circuit when it isn't colored
            return obj instanceof TransmitterDataKey other && renderGlass == other.renderGlass && data.getConnectionsMap().equals(other.data.getConnectionsMap());
        }
    }
}