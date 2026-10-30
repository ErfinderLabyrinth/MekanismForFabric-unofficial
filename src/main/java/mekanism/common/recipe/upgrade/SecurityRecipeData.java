package mekanism.common.recipe.upgrade;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.security.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NothingNullByDefault
public class SecurityRecipeData implements RecipeUpgradeData<SecurityRecipeData> {

    private final UUID owner;
    private final SecurityMode mode;

    SecurityRecipeData(UUID owner, SecurityMode mode) {
        this.owner = owner;
        this.mode = mode;
    }

    @Nullable
    @Override
    public SecurityRecipeData merge(SecurityRecipeData other) {
        if (owner.equals(other.owner)) {
            //Pick the most restrictive security mode
            return ISecurityUtils.INSTANCE.moreRestrictive(mode, other.mode) ? other : this;
        }
        //If the owners don't match fail
        return null;
    }

    @Override
    public ItemStack applyToStack(ItemStack stack) {
        IOwnerObject ownerObject;
        if (stack.getItem() instanceof IItemOwnerObjectGetter ownerObjectGetter && (ownerObject = ownerObjectGetter.getOwnerObject(stack)) != null) {
            ownerObject.setOwnerUUID(owner);
            if (stack.getItem() instanceof ISecurityObject securityObject) {
                securityObject.setSecurityMode(mode);
            }
        }
        return stack;
    }
}