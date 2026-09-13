package mekanism.common.network.to_server;

import mekanism.api.Coord4D;
import mekanism.api.FluidStack;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.radiation.IRadiationManager;
import mekanism.api.tier.BaseTier;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.advancements.triggers.UseGaugeDropperTrigger.UseDropperAction;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager;
import mekanism.common.item.ItemGaugeDropper;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PacketDropperUse implements IMekanismPacket {
    public static final PacketType<PacketDropperUse> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "dropper_use"), PacketDropperUse::decode);

    private final BlockPos pos;
    private final DropperAction action;
    private final TankType tankType;
    private final int tankId;

    public PacketDropperUse(BlockPos pos, DropperAction action, TankType tankType, int tankId) {
        this.pos = pos;
        this.action = action;
        this.tankType = tankType;
        this.tankId = tankId;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (tankId < 0 || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ContainerItemContext containerItemContext = ContainerItemContext.ofPlayerCursor(player, player.containerMenu);
        ItemStack stack = player.containerMenu.getCarried();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemGaugeDropper) {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), pos);
            if (tile != null) {
                if (tile instanceof TileEntityMultiblock<?> multiblock) {
                    MultiblockData structure = multiblock.getMultiblock();
                    if (structure.isFormed()) {
                        handleTankType(structure, serverPlayer, containerItemContext, new Coord4D(structure.getBounds().getCenter(), player.level()));
                    }
                } else {
                    if (action == DropperAction.DUMP_TANK && !player.isCreative()) {
                        //If the dropper is being used to dump the tank and the player is not in creative
                        // check if the block the tank is in is a tiered block and if it is, and it is creative
                        // don't allow clearing the tank
                        if (Attribute.getBaseTier(tile.getBlockType()) == BaseTier.CREATIVE) {
                            return;
                        }
                    }
                    handleTankType(tile, serverPlayer, containerItemContext, tile.getTileCoord());
                }
            }
        }
    }

    private void handleTankType(MultiblockData data, ServerPlayer player, ContainerItemContext containerItemContext, Coord4D coord) {
        if (tankType == TankType.FLUID_TANK) {
            IExtendedFluidTank fluidTank = data.fluidTanks.get(tankId);
            if (fluidTank != null) {
                handleFluidTank(player, containerItemContext, fluidTank);
            }
        } else if (tankType == TankType.GAS_TANK) {
            handleChemicalTanks(player, containerItemContext, data.getGasTanks(), coord, Capabilities.GAS_HANDLER_ITEM);
        } else if (tankType == TankType.INFUSION_TANK) {
            handleChemicalTanks(player, containerItemContext, data.getInfusionTanks(), coord, Capabilities.INFUSION_HANDLER_ITEM);
        } else if (tankType == TankType.PIGMENT_TANK) {
            handleChemicalTanks(player, containerItemContext, data.getPigmentTanks(), coord, Capabilities.PIGMENT_HANDLER_ITEM);
        } else if (tankType == TankType.SLURRY_TANK) {
            handleChemicalTanks(player, containerItemContext, data.getSlurryTanks(), coord, Capabilities.SLURRY_HANDLER_ITEM);
        }
    }

    private void handleTankType(TileEntityMekanism tile,
          ServerPlayer player, ContainerItemContext containerItemContext, Coord4D coord) {
        if (tankType == TankType.FLUID_TANK) {
            IExtendedFluidTank fluidTank = tile.getFluidManager().canHandle() ? tile.getFluidManager().getHolder().get(tankId) : null;
            if (fluidTank != null) {
                handleFluidTank(player, containerItemContext, fluidTank);
            }
        } else if (tankType == TankType.GAS_TANK) {
            handleChemicalTanks(player, containerItemContext, tile.getGasManager(), coord, Capabilities.GAS_HANDLER_ITEM);
        } else if (tankType == TankType.INFUSION_TANK) {
            handleChemicalTanks(player, containerItemContext, tile.getInfusionManager(), coord, Capabilities.INFUSION_HANDLER_ITEM);
        } else if (tankType == TankType.PIGMENT_TANK) {
            handleChemicalTanks(player, containerItemContext, tile.getPigmentManager(), coord, Capabilities.PIGMENT_HANDLER_ITEM);
        } else if (tankType == TankType.SLURRY_TANK) {
            handleChemicalTanks(player, containerItemContext, tile.getSlurryManager(), coord, Capabilities.SLURRY_HANDLER_ITEM);
        }
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> void handleChemicalTanks(
            ServerPlayer player, ContainerItemContext containerItemContext, ChemicalHandlerManager<CHEMICAL, STACK, TANK, ?, ?> manager, Coord4D coord, ItemApiLookup<? extends Storage<CHEMICAL>, ContainerItemContext> lookup) {
        if (manager.canHandle()) {
            handleChemicalTanks(player, containerItemContext, manager.getHolder().getAll(), coord, lookup);
        }
    }


    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> void handleChemicalTanks(
          ServerPlayer player, ContainerItemContext containerItemContext, List<TANK> tanks, Coord4D coord, ItemApiLookup<? extends Storage<CHEMICAL>, ContainerItemContext> lookup) {
        //This method is a workaround for Eclipse's compiler showing an error/warning if we try to just assign the tanks
        // to a variable in handleTankType and then have the size check and call to handleChemicalTank happen there
        if (tankId < tanks.size()) {
            handleChemicalTank(player, containerItemContext, tanks.get(tankId), coord, lookup);
        }
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void handleChemicalTank(ServerPlayer player, ContainerItemContext containerItemContext,
                                                                                                                 IChemicalTank<CHEMICAL, STACK> tank, Coord4D coord, ItemApiLookup<? extends Storage<CHEMICAL>, ContainerItemContext> lookup) {
        if (action == DropperAction.DUMP_TANK) {
            //Dump the tank
            if (!tank.isEmpty()) {
                if (tank instanceof IGasTank gasTank) {
                    //If the tank is a gas tank and has radioactive substances in it make sure we properly emit the radiation to the environment
                    IRadiationManager.INSTANCE.dumpRadiation(coord, gasTank.getStack(), player.server);
                }
                tank.setEmpty();
                MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.DUMP);
            }
        } else {
            //Optional<IChemicalHandler<CHEMICAL, STACK>> cap = stack.getItem() instanceof IChemicalHandler<?,?> ? Optional.of((IChemicalHandler<CHEMICAL, STACK>) stack.getItem()) : Optional.empty();
            Storage<CHEMICAL> handler = containerItemContext.find(lookup);
            if (handler != null) {
                if (handler instanceof IMekanismChemicalHandler<?, ?, ?> chemicalHandler) {
                    IChemicalTank<CHEMICAL, STACK> itemTank = (IChemicalTank<CHEMICAL, STACK>) chemicalHandler.getTanks().get(0);
                    //It is a chemical tank
                    if (itemTank != null) {
                        //Validate something didn't go terribly wrong, and we actually do have the tank we expect to have
                        if (action == DropperAction.FILL_DROPPER) {
                            //Insert chemical into dropper
                            transferBetweenTanks(tank, itemTank, player);
                            MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.FILL);
                        } else if (action == DropperAction.DRAIN_DROPPER) {
                            //Extract chemical from dropper
                            transferBetweenTanks(itemTank, tank, player);
                            MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.DRAIN);
                        }
                    }
                }
            }
        }
    }

    private void handleFluidTank(ServerPlayer player, ContainerItemContext containerItemContext, IExtendedFluidTank fluidTank) {
        if (action == DropperAction.DUMP_TANK) {
            //Dump the tank
            fluidTank.setEmpty();
            MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.DUMP);
            return;
        }
        Storage<FluidVariant> storage = containerItemContext.find(FluidStorage.ITEM);
        if (storage != null) {
            IExtendedFluidTank itemFluidTank = (IExtendedFluidTank) storage.iterator().next();
            if (itemFluidTank != null) {
                if (action == DropperAction.FILL_DROPPER) {
                    //Insert fluid into dropper
                    transferBetweenTanks(fluidTank, itemFluidTank, player);
                    MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.FILL);
                } else if (action == DropperAction.DRAIN_DROPPER) {
                    //Extract fluid from dropper
                    transferBetweenTanks(itemFluidTank, fluidTank, player);
                    MekanismCriteriaTriggers.USE_GAUGE_DROPPER.trigger(player, UseDropperAction.DRAIN);
                }
            }
        }
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void transferBetweenTanks(IChemicalTank<CHEMICAL, STACK> drainTank,
          IChemicalTank<CHEMICAL, STACK> fillTank, Player player) {
        if (!drainTank.isEmpty() && fillTank.getNeeded() > 0) {
            STACK chemicalInDrainTank = drainTank.getStack();
            long inserted;
            try(Transaction t=Transaction.openOuter()) {
                inserted = fillTank.insert(chemicalInDrainTank.getType(), chemicalInDrainTank.getAmount(), t);
            }
            long amount = chemicalInDrainTank.getAmount();
            if (inserted > 0) {
                //We are able to fit at least some of the chemical from our drain tank into the fill tank
                CHEMICAL resource = chemicalInDrainTank.getType();
                long extracted;
                try(Transaction t=Transaction.openOuter()) {
                    extracted = drainTank.extract(resource, inserted, t);
                    t.commit();
                }
                if (extracted != 0) {
                    //If we were able to actually extract it from our tank, then insert it into the tank
                    try(Transaction t=Transaction.openOuter()) {
                        MekanismUtils.logMismatchedStackSize(fillTank.insert(resource, extracted, t), 0);
                        t.commit();
                    }
                    player.containerMenu.synchronizeCarriedToRemote();
                }
            }
        }
    }

    private static void transferBetweenTanks(IExtendedFluidTank drainTank, IExtendedFluidTank fillTank, Player player) {
        if (!drainTank.isEmpty() && fillTank.getNeeded() > 0) {
            FluidStack fluidInDrainTank = drainTank.getFluid();
            long inserted;
            try(Transaction t=Transaction.openOuter()) {
                inserted = fillTank.insert(fluidInDrainTank.variant(), fluidInDrainTank.amount(), t);
            }
            long amount = fluidInDrainTank.amount();
            if (inserted > 0) {
                //We are able to fit at least some of the fluid from our drain tank into the fill tank
                long extracted;
                try(Transaction t=Transaction.openOuter()) {
                    extracted = drainTank.extract(fluidInDrainTank.variant(), inserted, t);
                    t.commit();
                }
                if (extracted > 0) {
                    //If we were able to actually extract it from our tank, then insert it into the tank
                    try(Transaction t=Transaction.openOuter()) {
                        MekanismUtils.logMismatchedStackSize(fillTank.insert(fluidInDrainTank.variant(), extracted, t), extracted);
                    }
                    player.containerMenu.synchronizeCarriedToRemote();
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(action);
        buffer.writeEnum(tankType);
        buffer.writeVarInt(tankId);
    }

    public static PacketDropperUse decode(FriendlyByteBuf buffer) {
        return new PacketDropperUse(buffer.readBlockPos(), buffer.readEnum(DropperAction.class), buffer.readEnum(TankType.class), buffer.readVarInt());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum DropperAction {
        FILL_DROPPER,
        DRAIN_DROPPER,
        DUMP_TANK
    }

    public enum TankType {
        GAS_TANK,
        FLUID_TANK,
        INFUSION_TANK,
        PIGMENT_TANK,
        SLURRY_TANK
    }
}