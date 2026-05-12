package mekanism.api;

import java.util.List;
import java.util.function.Function;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

@NothingNullByDefault
public class DataHandlerUtils {

    private DataHandlerUtils() {
    }

    /**
     * Helper to read and load a list of containers from a {@link ListTag}
     */
    public static void readContainers(List<? extends NBTSerializable> containers, ListTag storedContainers) {
        readContents(containers, storedContainers, getTagByType(containers));
    }

    /**
     * Helper to read and load a list of containers to a {@link ListTag}
     */
    public static ListTag writeContainers(List<? extends NBTSerializable<CompoundTag>> containers) {
        return writeContainers(containers, NBTSerializable::serializeNBT);
    }

    /**
     * Helper to read and load a list of containers to a {@link ListTag}
     */
    public static <T> ListTag writeContainers(List<T> containers, Function<T, CompoundTag> mapper) {
        return writeContents(containers, mapper, getTagByType(containers));
    }

    /**
     * Helper to read and load a list of handler contents from a {@link ListTag}
     */
    public static void readContents(List<? extends NBTSerializable> contents, ListTag storedContents, String key) {
        int size = contents.size();
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            CompoundTag tagCompound = storedContents.getCompound(tagCount);
            byte id = tagCompound.getByte(key);
            if (id >= 0 && id < size) {
                contents.get(id).deserializeNBT(tagCompound);
            }
        }
    }

    /**
     * Helper to read and load a list of handler contents to a {@link ListTag}
     */
    public static <T> ListTag writeContents(List<T> contents, Function<T, CompoundTag> mapper, String key) {
        ListTag storedContents = new ListTag();
        for (int tank = 0; tank < contents.size(); tank++) {
            CompoundTag tagCompound = mapper.apply(contents.get(tank));
            if (!tagCompound.isEmpty()) {
                tagCompound.putByte(key, (byte) tank);
                storedContents.add(tagCompound);
            }
        }
        return storedContents;
    }

    // keep this only for backwards compat
    private static String getTagByType(List<?> containers) {
        if (containers.isEmpty()) {
            return NBTConstants.CONTAINER;
        }
        Object obj = containers.get(0);
        if (obj instanceof IChemicalTank || obj instanceof IExtendedFluidHandler) {
            return NBTConstants.TANK;
        } else if (obj instanceof IHeatCapacitor || obj instanceof IEnergyContainer) {
            return NBTConstants.CONTAINER;
        } else if (obj instanceof IInventorySlot) {
            return NBTConstants.SLOT;
        }
        return NBTConstants.CONTAINER;
    }

    /**
     * Helper to calculate what the maximum id is in a list of contents.
     */
    public static int getMaxId(ListTag storedContents, String key) {
        int maxId = -1;
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            byte id = storedContents.getCompound(tagCount).getByte(key);
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }
}