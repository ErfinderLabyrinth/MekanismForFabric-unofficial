package mekanism.common.world.height;

import mekanism.common.resource.ore.OreAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

import javax.annotation.Nullable;

public record ConfigurableVerticalAnchor(AnchorType anchorType, int value) {

    public static ConfigurableVerticalAnchor create(/*String path, String comment, */OreAnchor defaultAnchor,
          @Nullable ConfigurableVerticalAnchor minAnchor) {
        //builder.comment(comment).push(path);
//        CachedEnumValue<AnchorType> type = CachedEnumValue.wrap(config, builder.comment("Type of anchor.",
//              "Absolute -> y = value",
//              "Above Bottom -> y = minY + value",
//              "Below Top -> y = depth - 1 + minY - value").defineEnum("type", defaultAnchor.type()));
//        ForgeConfigSpec.Builder valueBuilder = builder.comment("Value used for calculating y for the anchor based on the type.");
//        ConfigValue<Integer> value;
//        if (minAnchor == null) {
//            value = valueBuilder.define("value", defaultAnchor.value());
//        } else {
//            value = valueBuilder.define("value", defaultAnchor.value(), o -> {
//                if (o instanceof Integer v) {
//                    return minAnchor.anchorType.get() != type.get() || v >= minAnchor.value.getAsInt();
//                }
//                return false;
//            });
//        }
//        builder.pop();
        return new ConfigurableVerticalAnchor(defaultAnchor.type(), defaultAnchor.value());
    }

    public int resolveY(WorldGenerationContext context) {
        return anchorType.resolveY(context, value);
    }

    @Override
    public String toString() {
        return switch (anchorType) {
            case ABSOLUTE -> value + " absolute";
            case ABOVE_BOTTOM -> value + " above bottom";
            case BELOW_TOP -> value + " below top";
        };
    }
}