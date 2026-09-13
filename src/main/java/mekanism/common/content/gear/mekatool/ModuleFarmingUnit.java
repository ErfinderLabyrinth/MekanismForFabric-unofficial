package mekanism.common.content.gear.mekatool;

import com.google.common.collect.BiMap;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.network.to_client.PacketLightningRender;
import mekanism.common.network.to_client.PacketLightningRender.LightningPreset;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@ParametersAreNotNullByDefault
public class ModuleFarmingUnit implements ICustomModule<ModuleFarmingUnit> {

    private IModuleConfigItem<FarmingRadius> farmingRadius;

    @Override
    public void init(IModule<ModuleFarmingUnit> module, ModuleConfigItemCreator configItemCreator) {
        farmingRadius = configItemCreator.createConfigItem("farming_radius", MekanismLang.MODULE_FARMING_RADIUS,
              new ModuleEnumData<>(FarmingRadius.LOW, module.getInstalledCount() + 1));
    }

    @NotNull
    @Override
    public InteractionResult onItemUse(IModule<ModuleFarmingUnit> module, UseOnContext context) {
        //Start with doing common logic to the module before we get onto specific logic for the different ways the module can be used
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) {
            //Skip if we don't have a player, or they are sneaking
            return InteractionResult.PASS;
        }
        int diameter = farmingRadius.get().getRadius();
        if (diameter == 0) {
            //If we don't have any blocks we are going to want to do, then skip it
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        EnergyStorage energyStorage = ContainerItemContext.ofPlayerHand(player, context.getHand()).find(EnergyStorage.ITEM);
        if (energyStorage == null) {
            return InteractionResult.FAIL;
        }
        //Lazily lookup the state so we only have to query it once
        BlockState lazyClickedState = context.getLevel().getBlockState(context.getClickedPos());
        return MekanismUtils.performActions(
              //First try to use the disassembler as an axe
              useAxeAOE(context, lazyClickedState, energyStorage, diameter, ((AxeItem)Items.DIAMOND_AXE)::getStripped, SoundEvents.AXE_STRIP, -1),
              () -> useAxeAOE(context, lazyClickedState, energyStorage, diameter, WeatheringCopper::getPrevious, SoundEvents.AXE_SCRAPE, LevelEvent.PARTICLES_SCRAPE),
              () -> useAxeAOE(context, lazyClickedState, energyStorage, diameter, blockState -> {
                  return Optional.ofNullable((Block)((BiMap) HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(blockState.getBlock()))
                                  .map(block -> block.withPropertiesOf(blockState));
              }, SoundEvents.AXE_WAX_OFF, LevelEvent.PARTICLES_WAX_OFF),
              //Then as a shovel
              () -> flattenAOE(context, lazyClickedState, energyStorage, diameter),
              () -> dowseCampfire(context, lazyClickedState, energyStorage),
              //Finally, as a hoe
              () -> tillAOE(context, lazyClickedState, energyStorage, diameter)
        );
    }

//    @Override
//    public boolean canPerformAction(IModule<ModuleFarmingUnit> module, ToolAction action) {
//        if (action == ToolActions.AXE_STRIP || action == ToolActions.AXE_SCRAPE || action == ToolActions.AXE_WAX_OFF) {
//            return module.hasEnoughEnergy(MekanismConfig.gear.mekaToolEnergyUsageAxe);
//        } else if (action == ToolActions.SHOVEL_FLATTEN) {
//            return module.hasEnoughEnergy(MekanismConfig.gear.mekaToolEnergyUsageShovel);
//        } else if (action == ToolActions.HOE_TILL) {
//            return module.hasEnoughEnergy(MekanismConfig.gear.mekaToolEnergyUsageHoe);
//        }
//        //Note: In general when we get here there will be no tool actions known unless mods add more default tool actions
//        // This is because we special case the known vanilla types above and the dig variants are already handled by the Meka-Tool itself before
//        // it even checks the installed modules
//        return ToolActions.DEFAULT_AXE_ACTIONS.contains(action) || ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(action) || ToolActions.DEFAULT_HOE_ACTIONS.contains(action);
//    }

    @NothingNullByDefault
    public enum FarmingRadius implements IHasTextComponent {
        OFF(0),
        LOW(1),
        MED(3),
        HIGH(5),
        ULTRA(7);

        private final int radius;
        private final Component label;

        FarmingRadius(int radius) {
            this.radius = radius;
            this.label = TextComponentUtil.getString(Integer.toString(radius));
        }

        @Override
        public Component getTextComponent() {
            return label;
        }

        public int getRadius() {
            return radius;
        }
    }

    private InteractionResult dowseCampfire(UseOnContext context, BlockState clickedState, EnergyStorage energyStorage) {
        long energy = energyStorage.getAmount();
        long energyUsage = MekanismConfig.COMMON.gear.mekaToolEnergyUsageShovel;
        if (energy < energyUsage) {
            //Fail if we don't have enough energy or using the item failed
            return InteractionResult.FAIL;
        }
        if (clickedState.getBlock() instanceof CampfireBlock && clickedState.getValue(CampfireBlock.LIT)) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            if (!world.isClientSide()) {
                world.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
            }
            CampfireBlock.dowse(context.getPlayer(), world, pos, clickedState);
            if (!world.isClientSide()) {
                world.setBlock(pos, clickedState.setValue(CampfireBlock.LIT, Boolean.FALSE), Block.UPDATE_ALL_IMMEDIATE);
                try(Transaction t=Transaction.openOuter()) {
                    energyStorage.extract(energyUsage, t);
                    t.commit();
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult tillAOE(UseOnContext context, BlockState clickedState, EnergyStorage energyStorage, int diameter) {
        return useAOE(context, clickedState, energyStorage, diameter, ModuleFarmingUnit::getHoeInteractionBlockstate, SoundEvents.HOE_TILL, -1,
              MekanismConfig.COMMON.gear.mekaToolEnergyUsageHoe, new HoeToolAOEData());
    }

    private static Optional<BlockState> getHoeInteractionBlockstate(BlockState old) {
        Block block = old.getBlock();
        if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.DIRT_PATH) {
            return Optional.of(Blocks.FARMLAND.defaultBlockState());
        }else if(block == Blocks.COARSE_DIRT || block == Blocks.ROOTED_DIRT) {
            return Optional.of(Blocks.DIRT.defaultBlockState());
        }
        return Optional.empty();
    }

    private InteractionResult flattenAOE(UseOnContext context, BlockState clickedState, EnergyStorage energyStorage, int diameter) {
        Direction sideHit = context.getClickedFace();
        if (sideHit == Direction.DOWN) {
            //Don't allow flattening a block from underneath
            return InteractionResult.PASS;
        }
        return useAOE(context, clickedState, energyStorage, diameter, blockstate -> Optional.ofNullable(ShovelItem.FLATTENABLES.get(blockstate.getBlock())), SoundEvents.SHOVEL_FLATTEN, -1,
              MekanismConfig.COMMON.gear.mekaToolEnergyUsageShovel, new ShovelToolAOEData());
    }

    private InteractionResult useAxeAOE(UseOnContext context, BlockState clickedState, EnergyStorage energyStorage, int diameter, Function<BlockState, Optional<BlockState>> interaction,
          SoundEvent sound, int particle) {
        return useAOE(context, clickedState, energyStorage, diameter, interaction, sound, particle, MekanismConfig.COMMON.gear.mekaToolEnergyUsageAxe,
              new AxeToolAOEData());
    }

    private InteractionResult useAOE(UseOnContext context, BlockState clickedState, EnergyStorage energyStorage, int diameter, Function<BlockState, Optional<BlockState>> interaction,
          SoundEvent sound, int particle, long energyUsage, IToolAOEData toolAOEData) {
        long energy = energyStorage.getAmount();
        if (energy < energyUsage) {
            //Fail if we don't have enough energy or using the item failed
            return InteractionResult.FAIL;
        }
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!toolAOEData.isValid(world, pos, clickedState)) {
            //Skip modifying the blocks if there is something we think is invalid about the position in the world in general
            return InteractionResult.PASS;
        }
        BlockState oldState = world.getBlockState(pos);
        Optional<BlockState> modifiedStateOptional = interaction.apply(oldState);
        if (modifiedStateOptional.isEmpty()) {
            //Skip modifying the blocks if the one we clicked cannot be modified
            return InteractionResult.PASS;
        } else if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockState modifiedState = modifiedStateOptional.get();

        //Process the block we interacted with initially and play the sound
        world.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
        world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (oldState.getBlock() == Blocks.ROOTED_DIRT) { // Special case
            Block.popResourceFromFace(world, pos, context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
        }

        if (particle != -1) {
            world.levelEvent(null, particle, pos, 0);
        }
        Direction side = context.getClickedFace();
        toolAOEData.persistData(world, pos, clickedState, side);
        //Note: We don't need to copy this as we add to it in a non modifying way
        long energyUsed = energyUsage;
        for (BlockPos newPos : toolAOEData.getTargetPositions(pos, side, (diameter - 1) / 2)) {
            if (pos.equals(newPos)) {
                //Skip the source position as we manually handled it before the loop
                continue;
            }
            long nextEnergyUsed = energyUsed + energyUsage;
            if (nextEnergyUsed > energy) {
                break;
            }
            //Check to make that the result we would get from modifying the other block is the same as the one we got on the initial block we interacted with
            // Also make sure that it is properly valid
            BlockState state = world.getBlockState(newPos);
            //Create a new used context based on the original one to try and pass the proper information to the conversion
            UseOnContext adjustedContext = new UseOnContext(world, context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(
                  context.getClickLocation().add(newPos.getX() - pos.getX(), newPos.getY() - pos.getY(), newPos.getZ() - pos.getZ()),
                  context.getClickedFace(), newPos, context.isInside()));
            Optional<BlockState> newState = interaction.apply(state);
            if (toolAOEData.isValid(world, newPos, state) && newState.isPresent() && modifiedState == newState.get()) {
                //Some of the below methods don't behave properly when the BlockPos is mutable, so now that we are onto ones where it may actually
                // matter we make sure to get an immutable instance of newPos
                newPos = newPos.immutable();
                //Update energy cost
                energyUsed = nextEnergyUsed;
                //Run it without simulation in case there are any side effects
                //state.getToolModifiedState(adjustedContext, action, false);
                //Replace the block. Note it just directly sets it (in the same way the normal tools do).
                world.setBlock(newPos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                world.playSound(null, newPos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (particle != -1) {
                    world.levelEvent(null, particle, newPos, 0);
                }
                Mekanism.packetHandler().sendToAllTracking(new PacketLightningRender(LightningPreset.TOOL_AOE, Objects.hash(pos, newPos),
                      toolAOEData.getLightningPos(pos), toolAOEData.getLightningPos(newPos), 10), world, pos);
            }
        }
        try(Transaction t=Transaction.openOuter()) {
            energyStorage.extract(energyUsed, t);
            t.commit();
        }
        return InteractionResult.CONSUME;
    }

    private interface IToolAOEData {

        boolean isValid(Level level, BlockPos pos, BlockState state);

        default void persistData(Level level, BlockPos pos, BlockState state, Direction side) {
        }

        Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius);

        Vec3 getLightningPos(BlockPos pos);
    }

    private abstract static class FlatToolAOEData implements IToolAOEData {

        @Override
        public Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius) {
            return BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius));
        }

        @Override
        public Vec3 getLightningPos(BlockPos pos) {
            return Vec3.upFromBottomCenterOf(pos, 0.94);
        }
    }

    private static class HoeToolAOEData extends FlatToolAOEData {

        @Override
        public boolean isValid(Level level, BlockPos pos, BlockState state) {
            //Always return that we are valid if we could find a conversion, we unfortunately are no longer able
            // to allow conversions when there is plants on top of it as then the tool modified state won't return
            // anything
            return true;
        }
    }

    private static class ShovelToolAOEData extends FlatToolAOEData {

        @Override
        public boolean isValid(Level level, BlockPos pos, BlockState state) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            //Allow flattening a block when the above block is air
            if (aboveState.isAir()) {
                return true;
            }
            //Or it is a replaceable plant that is also not solid (such as tall grass)
            //Note: This may not be the most optimal way of checking this, but it gives a decent enough estimate of it
            if (aboveState.is(MekanismTags.Blocks.FARMING_OVERRIDE) || aboveState.canBeReplaced() && aboveState.getBlock() instanceof CropBlock) {
                return aboveState.getFluidState().isEmpty() && !aboveState.isSolidRender(level, abovePos);
            }
            return false;
        }
    }

    private static class AxeToolAOEData implements IToolAOEData {

        @Nullable
        private Axis axis;
        private boolean isSet;
        private Vec3 offset = Vec3.ZERO;

        @Override
        public boolean isValid(Level level, BlockPos blockPos, BlockState state) {
            return !isSet || axis == getAxis(state);
        }

        @Override
        public void persistData(Level level, BlockPos pos, BlockState state, Direction side) {
            axis = getAxis(state);
            isSet = true;
            offset = Vec3.atLowerCornerOf(side.getNormal()).scale(0.5);
        }

        @Override
        public Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius) {
            Vec3i adjustment = switch (side) {
                case EAST, WEST -> new Vec3i(0, radius, radius);
                case UP, DOWN -> new Vec3i(radius, 0, radius);
                case SOUTH, NORTH -> new Vec3i(radius, radius, 0);
            };
            AABB box = new AABB(pos.subtract(adjustment), pos.offset(adjustment));
            return BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ));
        }

        @Nullable
        private Axis getAxis(BlockState state) {
            return state.hasProperty(RotatedPillarBlock.AXIS) ? state.getValue(RotatedPillarBlock.AXIS) : null;
        }

        @Override
        public Vec3 getLightningPos(BlockPos pos) {
            return Vec3.atCenterOf(pos).add(offset);
        }
    }
}