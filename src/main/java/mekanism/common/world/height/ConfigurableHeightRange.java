package mekanism.common.world.height;

import mekanism.common.resource.ore.BaseOreConfig;

public record ConfigurableHeightRange(HeightShape shape, ConfigurableVerticalAnchor minInclusive, ConfigurableVerticalAnchor maxInclusive,
                                      int plateau) {

    public static ConfigurableHeightRange create(String veinType, BaseOreConfig baseConfig) {
//        CachedEnumValue<HeightShape> shape = CachedEnumValue.wrap(config, builder.comment("Distribution shape for placing " + veinType + "s.")
//              .defineEnum("shape", baseConfig.shape()));
//        ConfigurableVerticalAnchor minInclusive = ConfigurableVerticalAnchor.create(config, builder, "minInclusive",
//              "Minimum (inclusive) height anchor for " + veinType + "s.", baseConfig.min(), null);
        ConfigurableVerticalAnchor minInclusive = ConfigurableVerticalAnchor.create(baseConfig.min(), null);
        return new ConfigurableHeightRange(baseConfig.shape(), minInclusive, ConfigurableVerticalAnchor.create(baseConfig.max(), minInclusive), baseConfig.plateau()
//              ConfigurableVerticalAnchor.create(config, builder, "maxInclusive", "Maximum (inclusive) height anchor for " + veinType + "s.",
//                    baseConfig.max(), minInclusive),
//              CachedIntValue.wrap(config, builder.comment("Half length of short side of trapezoid, only used if shape is TRAPEZOID. A value of zero means the shape is a triangle.")
//                    .define("plateau", baseConfig.plateau(), o -> {
//                        if (o instanceof Integer value) {
//                            if (value == 0) {
//                                return true;
//                            }
//                            return value > 0 && shape.get() == HeightShape.TRAPEZOID;
//                        }
//                        return false;
//                    }))
        );
    }
}