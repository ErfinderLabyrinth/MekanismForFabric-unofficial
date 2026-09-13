package mekanism.client.gui.element.custom;

import com.google.common.collect.Streams;
import mekanism.api.FluidStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.text.TextComponentUtil;
import mekanism.client.gui.GuiUtils.TilingDirection;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.item.GuiDictionary.DictionaryTagType;
import mekanism.client.jei.interfaces.IJEIGhostTarget;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidTextureType;
import mekanism.common.Mekanism;
import mekanism.common.base.TagCache;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tags.TagUtils;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GuiDictionaryTarget extends GuiElement implements IJEIGhostTarget {

    private final Map<DictionaryTagType, List<String>> tags = new EnumMap<>(DictionaryTagType.class);
    private final Consumer<Set<DictionaryTagType>> tagSetter;
    @Nullable
    private Object target;

    public GuiDictionaryTarget(IGuiWrapper gui, int x, int y, Consumer<Set<DictionaryTagType>> tagSetter) {
        super(gui, x, y, 16, 16);
        this.tagSetter = tagSetter;
    }

    public boolean hasTarget() {
        return target != null;
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (target instanceof ItemStack stack) {
            gui().renderItem(guiGraphics, stack, relativeX, relativeY);
        } else if (target instanceof FluidStack stack) {
            MekanismRenderer.color(guiGraphics, stack);
            drawTiledSprite(guiGraphics, relativeX, relativeY, height, width, height, MekanismRenderer.getFluidTexture(stack, FluidTextureType.STILL), TilingDirection.DOWN_RIGHT);
            MekanismRenderer.resetColor(guiGraphics);
        } else if (target instanceof ChemicalStack<?> stack) {
            MekanismRenderer.color(guiGraphics, stack);
            drawTiledSprite(guiGraphics, relativeX, relativeY, height, width, height, MekanismRenderer.getChemicalTexture(stack.getType()), TilingDirection.DOWN_RIGHT);
            MekanismRenderer.resetColor(guiGraphics);
        }
    }

    @Override
    public void renderToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderToolTip(guiGraphics, mouseX, mouseY);
        if (target instanceof ItemStack stack) {
            gui().renderItemTooltip(guiGraphics, stack, mouseX, mouseY);
        } else if (target != null) {
            displayTooltips(guiGraphics, mouseX, mouseY, TextComponentUtil.build(target));
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (Screen.hasShiftDown()) {
            if (target != null) {
                setTargetSlot(null);
            }
        } else {
            ItemStack stack = gui().getCarriedItem();
            if (!stack.isEmpty()) {
                setTargetSlot(stack);
            }
        }
    }

    public List<String> getTags(DictionaryTagType type) {
        return tags.getOrDefault(type, Collections.emptyList());
    }

    public void setTargetSlot(@Nullable Object newTarget) {
        //Clear cached tags
        tags.clear();
        if (newTarget == null) {
            target = null;
        } else if (newTarget instanceof ItemStack itemStack) {
            if (itemStack.isEmpty()) {
                target = null;
            } else {
                ItemStack stack = itemStack.copyWithCount(1);
                target = stack;
                Item item = stack.getItem();
                tags.put(DictionaryTagType.ITEM, TagCache.getItemTags(stack));
                if (item instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    tags.put(DictionaryTagType.BLOCK, TagCache.getTagsAsStrings(TagUtils.tagEntries(BuiltInRegistries.BLOCK, block)));
                    if (block instanceof IHasTileEntity || block.defaultBlockState().hasBlockEntity()) {
                        tags.put(DictionaryTagType.BLOCK_ENTITY_TYPE, TagCache.getTileEntityTypeTags(block));
                    }
                }
                //Entity type tags
                if (item instanceof SpawnEggItem spawnEggItem) {
                    tags.put(DictionaryTagType.ENTITY_TYPE, TagCache.getTagsAsStrings(TagUtils.tagEntries(BuiltInRegistries.ENTITY_TYPE, spawnEggItem.getType(stack.getTag()))));
                }
                //Enchantment tags
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                if (!enchantments.isEmpty()) {
                    tags.put(DictionaryTagType.ENCHANTMENT, TagCache.getTagsAsStrings(enchantments.keySet().stream()
                          .flatMap(enchantment -> TagUtils.tagEntries(BuiltInRegistries.ENCHANTMENT, enchantment))
                          .distinct()
                    ));
                }
                //Get any potion tags
                Potion potion = PotionUtils.getPotion(itemStack);
                if (potion != Potions.EMPTY) {
                    tags.put(DictionaryTagType.POTION, TagCache.getTagsAsStrings(TagUtils.tagEntries(BuiltInRegistries.POTION, potion)));
                    tags.put(DictionaryTagType.MOB_EFFECT, TagCache.getTagsAsStrings(potion.getEffects().stream()
                          .flatMap(effect -> TagUtils.tagEntries(BuiltInRegistries.MOB_EFFECT, effect.getEffect()))
                          .distinct()
                    ));
                }
                //Get any attribute tags
                Set<Attribute> attributes = Arrays.stream(EnumUtils.EQUIPMENT_SLOT_TYPES)
                      .flatMap(slot -> itemStack.getAttributeModifiers(slot).keySet().stream())
                      .collect(Collectors.toSet());
                if (!attributes.isEmpty()) {
                    //Only add them though if it has any attributes at all
                    tags.put(DictionaryTagType.ATTRIBUTE, TagCache.getTagsAsStrings(attributes.stream()
                          .flatMap(attribute -> TagUtils.tagEntries(BuiltInRegistries.ATTRIBUTE, attribute))
                          .distinct()
                    ));
                }
                //Get tags of any contained fluids
                Storage<FluidVariant> fluidStorage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
                if (fluidStorage != null) {
                    tags.put(DictionaryTagType.FLUID, TagCache.getTagsAsStrings(Streams.stream(fluidStorage.nonEmptyIterator())
                            .filter(fluidInTank -> !fluidInTank.isResourceBlank() && fluidInTank.getAmount() != 0)
                            .flatMap(fluidInTank -> TagUtils.tagEntries(BuiltInRegistries.FLUID, fluidInTank.getResource().getFluid()))
                            .distinct()
                    ));
                }
                //Get tags of any contained chemicals
                addChemicalTags(DictionaryTagType.GAS, stack, Capabilities.GAS_HANDLER_ITEM);
                addChemicalTags(DictionaryTagType.INFUSE_TYPE, stack, Capabilities.INFUSION_HANDLER_ITEM);
                addChemicalTags(DictionaryTagType.PIGMENT, stack, Capabilities.PIGMENT_HANDLER_ITEM);
                addChemicalTags(DictionaryTagType.SLURRY, stack, Capabilities.SLURRY_HANDLER_ITEM);
                //TODO: Support other types of things?
            }
        } else if (newTarget instanceof FluidStack fluidStack) {
            if (fluidStack.isEmpty()) {
                target = null;
            } else {
                target = fluidStack.copy();
                tags.put(DictionaryTagType.FLUID, TagCache.getTagsAsStrings(TagUtils.tagEntries(BuiltInRegistries.FLUID, ((FluidStack) target).getFluid())));
            }
        } else if (newTarget instanceof ChemicalStack<?> chemicalStack) {
            if (chemicalStack.isEmpty()) {
                target = null;
            } else {
                target = chemicalStack.copy();
                List<String> chemicalTags = TagCache.getTagsAsStrings(((ChemicalStack<?>) target).getType().getTags());
                if (target instanceof GasStack) {
                    tags.put(DictionaryTagType.GAS, chemicalTags);
                } else if (target instanceof InfusionStack) {
                    tags.put(DictionaryTagType.INFUSE_TYPE, chemicalTags);
                } else if (target instanceof PigmentStack) {
                    tags.put(DictionaryTagType.PIGMENT, chemicalTags);
                } else if (target instanceof SlurryStack) {
                    tags.put(DictionaryTagType.SLURRY, chemicalTags);
                }
            }
        } else {
            Mekanism.logger.warn("Unable to get tags for unknown type: {}", newTarget);
            return;
        }
        //Update the list being viewed
        tagSetter.accept(tags.keySet());
        playClickSound(SoundEvents.UI_BUTTON_CLICK::value);
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<?, STACK>, HANDLER extends IChemicalHandler<?, STACK, TANK>> void addChemicalTags(DictionaryTagType tagType, ItemStack stack, ItemApiLookup<? extends Storage<CHEMICAL>, ContainerItemContext> itemApiLookup) {
        Storage<CHEMICAL> chemicalHandler = ContainerItemContext.withConstant(stack).find(itemApiLookup);
        if (chemicalHandler != null) {
            tags.put(tagType, TagCache.getTagsAsStrings(Streams.stream(chemicalHandler.nonEmptyIterator())
                            .flatMap(chemicalInTank -> chemicalInTank.getResource().getTags())
                            .distinct()
                    )
            );
        }
    }

    @Override
    public boolean hasPersistentData() {
        return true;
    }

    @Override
    public void syncFrom(GuiElement element) {
        super.syncFrom(element);
        GuiDictionaryTarget old = (GuiDictionaryTarget) element;
        target = old.target;
        tags.putAll(old.tags);
    }

    @Nullable
    @Override
    public IGhostIngredientConsumer getGhostHandler() {
        return new IGhostIngredientConsumer() {
            @Override
            public boolean supportsIngredient(Object ingredient) {
                if (ingredient instanceof ItemStack stack) {
                    return !stack.isEmpty();
                } else if (ingredient instanceof FluidStack stack) {
                    return !stack.isEmpty();
                } else if (ingredient instanceof ChemicalStack<?> stack) {
                    return !stack.isEmpty();
                }
                return false;
            }

            @Override
            public void accept(Object ingredient) {
                setTargetSlot(ingredient);
            }
        };
    }
}