package mekanism.client.render;

import mekanism.client.render.armor.*;
import mekanism.client.render.item.block.RenderEnergyCubeItem;
import mekanism.client.render.item.block.RenderFluidTankItem;
import mekanism.client.render.item.gear.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class RenderPropertiesProvider {

    private RenderPropertiesProvider() {
    }

    public static MekRenderProperties energyCube() {
        return new MekRenderProperties(RenderEnergyCubeItem.RENDERER);
    }

    public static MekRenderProperties fluidTank() {
        return new MekRenderProperties(RenderFluidTankItem.RENDERER);
    }

    public static MekCustomArmorRenderProperties armoredJetpack() {
        return new MekCustomArmorRenderProperties(RenderJetpack.ARMORED_RENDERER, JetpackArmor.ARMORED_JETPACK);
    }

    public static MekCustomArmorRenderProperties jetpack() {
        return new MekCustomArmorRenderProperties(RenderJetpack.RENDERER, JetpackArmor.JETPACK);
    }

    public static MekRenderProperties disassembler() {
        return new MekRenderProperties(RenderAtomicDisassembler.RENDERER);
    }

    public static MekRenderProperties flamethrower() {
        return new MekRenderProperties(RenderFlameThrower.RENDERER);
    }

    public static MekCustomArmorRenderProperties armoredFreeRunners() {
        return new MekCustomArmorRenderProperties(RenderFreeRunners.ARMORED_RENDERER, FreeRunnerArmor.ARMORED_FREE_RUNNERS);
    }

    public static MekCustomArmorRenderProperties freeRunners() {
        return new MekCustomArmorRenderProperties(RenderFreeRunners.RENDERER, FreeRunnerArmor.FREE_RUNNERS);
    }

    public static MekCustomArmorRenderProperties scubaMask() {
        return new MekCustomArmorRenderProperties(RenderScubaMask.RENDERER, ScubaMaskArmor.SCUBA_MASK);
    }

    public static MekCustomArmorRenderProperties scubaTank() {
        return new MekCustomArmorRenderProperties(RenderScubaTank.RENDERER, ScubaTankArmor.SCUBA_TANK);
    }

    public static ISpecialGear mekaSuit() {
        return MEKA_SUIT;
    }

    private static final ISpecialGear MEKA_SUIT = type -> switch (type) {
        case HELMET -> MekaSuitArmor.HELMET;
        case CHESTPLATE -> MekaSuitArmor.BODYARMOR;
        case LEGGINGS -> MekaSuitArmor.PANTS;
        case BOOTS -> MekaSuitArmor.BOOTS;
    };

//    public static IClientBlockExtensions particles() {
//        return PARTICLE_HANDLER;
//    }
//
//    public static IClientBlockExtensions boundingParticles() {
//        return new IClientBlockExtensions() {
//            @Override
//            public boolean addHitEffects(BlockState state, Level world, HitResult target, ParticleEngine manager) {
//                if (target.getType() == Type.BLOCK && target instanceof BlockHitResult blockTarget) {
//                    BlockPos pos = blockTarget.getBlockPos();
//                    BlockPos mainPos = BlockBounding.getMainBlockPos(world, pos);
//                    if (mainPos != null) {
//                        BlockState mainState = world.getBlockState(mainPos);
//                        if (!mainState.isAir()) {
//                            //Copy of ParticleManager#addBlockHitEffects except using the block state for the main position
//                            AABB axisalignedbb = state.getShape(world, pos).bounds();
//                            double x = pos.getX() + world.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2) + 0.1 + axisalignedbb.minX;
//                            double y = pos.getY() + world.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2) + 0.1 + axisalignedbb.minY;
//                            double z = pos.getZ() + world.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2) + 0.1 + axisalignedbb.minZ;
//                            Direction side = blockTarget.getDirection();
//                            switch (side) {
//                                case DOWN -> y = pos.getY() + axisalignedbb.minY - 0.1;
//                                case UP -> y = pos.getY() + axisalignedbb.maxY + 0.1;
//                                case NORTH -> z = pos.getZ() + axisalignedbb.minZ - 0.1;
//                                case SOUTH -> z = pos.getZ() + axisalignedbb.maxZ + 0.1;
//                                case WEST -> x = pos.getX() + axisalignedbb.minX - 0.1;
//                                case EAST -> x = pos.getX() + axisalignedbb.maxX + 0.1;
//                            }
//                            manager.add(new TerrainParticle((ClientLevel) world, x, y, z, 0, 0, 0, mainState)
//                                  .updateSprite(mainState, mainPos).setPower(0.2F).scale(0.6F));
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        };
//    }

    public static class MekRenderProperties {
        private BlockEntityWithoutLevelRenderer renderer;
        public MekRenderProperties(BlockEntityWithoutLevelRenderer renderer) {
            this.renderer = renderer;
        }

        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    }

    public interface MekRenderPropertiesGetter {
        MekRenderProperties getRenderProperties();
    }

    public static class MekCustomArmorRenderProperties extends MekRenderProperties implements ISpecialGear {
        ICustomArmor gearModel;

        public MekCustomArmorRenderProperties(BlockEntityWithoutLevelRenderer renderer, ICustomArmor gearModel) {
            super(renderer);
            this.gearModel = gearModel;
        }

        @NotNull
        @Override
        public ICustomArmor getGearModel(ArmorItem.Type type) {
            return gearModel;
        }
    }

//    private static final IClientBlockExtensions PARTICLE_HANDLER = new IClientBlockExtensions() {
//        @Override
//        public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
//            //Copy of ParticleManager#addBlockDestroyEffects, but removes the minimum number of particles each voxel shape produces
//            state.getShape(Level, pos).forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
//                double xDif = Math.min(1, maxX - minX);
//                double yDif = Math.min(1, maxY - minY);
//                double zDif = Math.min(1, maxZ - minZ);
//                //Don't force the counts to be at least two
//                int xCount = Mth.ceil(xDif / 0.25);
//                int yCount = Mth.ceil(yDif / 0.25);
//                int zCount = Mth.ceil(zDif / 0.25);
//                if (xCount > 0 && yCount > 0 && zCount > 0) {
//                    for (int x = 0; x < xCount; x++) {
//                        for (int y = 0; y < yCount; y++) {
//                            for (int z = 0; z < zCount; z++) {
//                                double d4 = (x + 0.5) / xCount;
//                                double d5 = (y + 0.5) / yCount;
//                                double d6 = (z + 0.5) / zCount;
//                                double d7 = d4 * xDif + minX;
//                                double d8 = d5 * yDif + minY;
//                                double d9 = d6 * zDif + minZ;
//                                manager.add(new TerrainParticle((ClientLevel) Level, pos.getX() + d7, pos.getY() + d8,
//                                      pos.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5, state).updateSprite(state, pos));
//                            }
//                        }
//                    }
//                }
//            });
//            return true;
//        }
//    };
}