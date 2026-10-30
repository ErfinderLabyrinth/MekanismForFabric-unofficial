package mekanism.common.content.oredictionificator;

import mekanism.common.config.MekanismConfig;
import mekanism.common.content.filter.FilterType;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

//TODO - V11: Rewrite/refactor usages of this to better handle tags for fluids and chemicals by allowing them to make use of the super OredictionificatorFilter class
public class OredictionificatorItemFilter extends OredictionificatorFilter<Item, ItemStack, OredictionificatorItemFilter> {

    public OredictionificatorItemFilter() {
    }

    public OredictionificatorItemFilter(OredictionificatorItemFilter filter) {
        super(filter);
    }

    @Override
    @ComputerMethod(nameOverride = "getSelectedOutput")
    public Item getResultElement() {
        return getResult().getItem();
    }

    @Override
    protected Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    protected HolderLookup.RegistryLookup<Item> getTagLookup() {
        return getRegistry().asTagAddingLookup();
    }

    @Override
    protected Item getFallbackElement() {
        return Items.AIR;
    }

    @Override
    protected ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack createResultStack(Item item) {
        return new ItemStack(item);
    }

    @Override
    protected Map<String, List<String>> getValidValuesConfig() {
        return MekanismConfig.COMMON.general.validOredictionificatorFilters;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.OREDICTIONIFICATOR_ITEM_FILTER;
    }

    @Override
    public OredictionificatorItemFilter clone() {
        return new OredictionificatorItemFilter(this);
    }

    @ComputerMethod(nameOverride = "setSelectedOutput", threadSafe = true)
    void computerSetSelectedOutput(@NotNull Item item) {
        setSelectedOutput(item);
    }
}
