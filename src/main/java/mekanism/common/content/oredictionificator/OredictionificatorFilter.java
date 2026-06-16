package mekanism.common.content.oredictionificator;

import mekanism.api.NBTConstants;
import mekanism.common.content.filter.BaseFilter;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.tile.machine.TileEntityOredictionificator;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.NetworkUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntBinaryOperator;

public abstract class OredictionificatorFilter<TYPE, STACK, FILTER extends OredictionificatorFilter<TYPE, STACK, FILTER>> extends BaseFilter<FILTER> {

    @Nullable
    private TagKey<TYPE> filterLocation;
    @Nullable
    private Optional<HolderSet.Named<TYPE>> filterTag;
    @NotNull
    private TYPE selectedOutput = getFallbackElement();
    @Nullable
    private STACK cachedSelectedStack;
    private boolean isValid;

    protected OredictionificatorFilter() {
    }

    protected OredictionificatorFilter(OredictionificatorFilter<TYPE, STACK, FILTER> filter) {
        filterLocation = filter.filterLocation;
        filterTag = filter.filterTag;
        selectedOutput = filter.selectedOutput;
        cachedSelectedStack = filter.cachedSelectedStack;
        isValid = filter.isValid;
    }

    public void flushCachedTag() {
        //If the filter doesn't exist (because we loaded a tagSupplier that is no longer valid), then just set the filter to being empty
        filterTag = filterLocation == null ? null : getTagLookup().get(filterLocation);
        if (filterTag == null || !filterTag.isPresent()) {
            setSelectedOutput(getFallbackElement());
        } else if (!filterTag.get().contains(getRegistry().createIntrusiveHolder(selectedOutput))) {
            filterTag.get().stream().findFirst().ifPresentOrElse(holder -> setSelectedOutput(holder.value()), () -> setSelectedOutput(getFallbackElement()));
        }
        //Note: Even though the tagSupplier instance may have changed, we don't need to reset the cached
        // stack if the tagSupplier still contains the selected output as that means it is not empty and
        // the stack is still valid
    }

    @Override
    public boolean hasFilter() {
        return filterLocation != null && isValid;
    }

    public void checkValidity() {
        if (filterLocation != null && getTagLookup().get(filterLocation).isPresent()) {
            for (String filter : getValidValuesConfig().getOrDefault(filterLocation.location().getNamespace(), Collections.emptyList())) {
                if (filterLocation.location().getPath().startsWith(filter)) {
                    isValid = true;
                    return;
                }
            }
        }
        isValid = false;
    }

    @ComputerMethod(nameOverride = "getFilter", threadSafe = true)
    public String getFilterText() {
        return filterLocation == null ? "" : filterLocation.location().toString();
    }

    /**
     * This method should only be called if the filter is valid or if it isn't the validity should be rechecked afterwards
     */
    public final void setFilter(@Nullable ResourceLocation location) {
        filterLocation = location == null ? null : TagKey.create(getRegistry().key(), location);
        flushCachedTag();
        isValid = true;
    }

    @ComputerMethod(nameOverride = "setFilter")
    public void computerSetFilter(ResourceLocation tag) throws ComputerException {
        if (tag == null || !TileEntityOredictionificator.isValidTarget(tag)) {
            throw new ComputerException("Invalid tagSupplier");
        }
        setFilter(tag);
    }

    /**
     * Only publicly exposed for creating via ComputerCraft
     */
    public final void setSelectedOutput(@NotNull TYPE output) {
        this.selectedOutput = output;
        //Invalidate cached stack
        cachedSelectedStack = null;
    }

    public boolean filterMatches(ResourceLocation location) {
        return filterLocation != null && filterLocation.location().equals(location);
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.putString(NBTConstants.FILTER, getFilterText());
        if (selectedOutput != getFallbackElement()) {
            NBTUtils.writeRegistryEntry(nbtTags, NBTConstants.SELECTED, getRegistry(), selectedOutput);
        }
        return nbtTags;
    }

    @Override
    public void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        NBTUtils.setResourceLocationIfPresentElse(nbtTags, NBTConstants.FILTER, this::setFilter, () -> setFilter(null));
        NBTUtils.setResourceLocationIfPresent(nbtTags, NBTConstants.SELECTED, this::setSelectedOrFallback);
        //Recheck filter validity after reading from nbt
        checkValidity();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        //Realistically the filter location shouldn't be null except when the filter is first being created
        // but handle it being null just in case
        NetworkUtil.writeOptional(buffer, filterLocation, (buf, location) -> buf.writeResourceLocation(location.location()));
        buffer.writeResourceLocation(getRegistry().getKey(selectedOutput));
        buffer.writeBoolean(isValid);
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        super.read(buffer);
        setFilter(NetworkUtil.readOptional(buffer, FriendlyByteBuf::readResourceLocation));
        setSelectedOrFallback(buffer.readResourceLocation());
        isValid = buffer.readBoolean();
    }

    private void setSelectedOrFallback(@NotNull ResourceLocation resourceLocation) {
        TYPE output = getRegistry().get(resourceLocation);
        setSelectedOutput(output == null ? getFallbackElement() : output);
    }

    public STACK getResult() {
        //If we don't currently have a result stack cached, calculate what the result stack is
        if (cachedSelectedStack == null) {
            List<TYPE> matchingElements = matchingElements();
            if (matchingElements.isEmpty()) {
                cachedSelectedStack = getEmptyStack();
            } else {
                if (selectedOutput == getFallbackElement() || !matchingElements.contains(selectedOutput)) {
                    //Fallback to the first element if we don't have an output selected/it isn't in our possible outputs
                    selectedOutput = matchingElements.get(0);
                }
                cachedSelectedStack = createResultStack(selectedOutput);
            }
        }
        return cachedSelectedStack;
    }

    public final void next() {
        adjustSelected((index, size) -> {
            if (index < size - 1) {
                return index + 1;
            }
            return 0;
        });
    }

    public final void previous() {
        adjustSelected((index, size) -> {
            if (index == -1) {
                return 0;
            } else if (index > 0) {
                return index - 1;
            }
            return size - 1;
        });
    }

    private List<TYPE> matchingElements() {
        return filterTag == null || filterTag.isEmpty() ? Collections.emptyList() : filterTag.get().stream().map(Holder::value).toList();
    }

    private void adjustSelected(IntBinaryOperator calculateSelected) {
        List<TYPE> matchingElements = matchingElements();
        int size = matchingElements.size();
        //Check if there is more than one element as the selected output does not need to change if there is only one element
        if (size > 1) {
            int selected;
            if (selectedOutput == getFallbackElement()) {
                selected = size - 1;
            } else {
                selected = calculateSelected.applyAsInt(matchingElements.indexOf(selectedOutput), size);
            }
            setSelectedOutput(matchingElements.get(selected));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), filterLocation, selectedOutput);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        OredictionificatorFilter<?, ?, ?> other = (OredictionificatorFilter<?, ?, ?>) o;
        return Objects.equals(filterLocation, other.filterLocation) && selectedOutput == other.selectedOutput;
    }

    public abstract TYPE getResultElement();

    protected abstract Registry<TYPE> getRegistry();

    protected abstract HolderLookup.RegistryLookup<TYPE> getTagLookup();

    protected abstract TYPE getFallbackElement();

    protected abstract STACK getEmptyStack();

    protected abstract STACK createResultStack(TYPE type);

    protected abstract Map<String, List<String>> getValidValuesConfig();

    @Override
    @ComputerMethod(threadSafe = true)
    public abstract FILTER clone();
}
