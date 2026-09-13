package mekanism.common.block.attribute;

import mekanism.api.Upgrade;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record AttributeUpgradeSupport(@NotNull Set<Upgrade> supportedUpgrades) implements Attribute {
}
