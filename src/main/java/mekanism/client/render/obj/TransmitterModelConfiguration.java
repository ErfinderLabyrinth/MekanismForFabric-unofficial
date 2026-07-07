package mekanism.client.render.obj;

import mekanism.client.model.data.TransmitterModelData;
import mekanism.client.model.data.TransmitterModelData.Diversion;
import mekanism.common.lib.transmitter.ConnectionType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class TransmitterModelConfiguration {
    public static IconStatus getIconStatus(TransmitterModelData modelData, Direction side, ConnectionType connectionType) {
        if (modelData instanceof Diversion || connectionType != ConnectionType.NONE) {
            return IconStatus.NO_SHOW;
        }
        //If we don't have a connection coming out of this side
        return switch (side) {
            case DOWN, UP -> getStatus(modelData, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            case NORTH, SOUTH -> getStatus(modelData, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
            case WEST, EAST -> getStatus(modelData, Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
        };
    }

    private static IconStatus getStatus(TransmitterModelData modelData, Direction a, Direction b, Direction c, Direction d) {
        boolean hasA = modelData.getConnectionType(a) != ConnectionType.NONE;
        boolean hasB = modelData.getConnectionType(b) != ConnectionType.NONE;
        boolean hasC = modelData.getConnectionType(c) != ConnectionType.NONE;
        boolean hasD = modelData.getConnectionType(d) != ConnectionType.NONE;
        //If we don't have a connection coming out of one side, but have one coming out of the perpendicular one
        if ((hasA || hasB) != (hasC || hasD)) {
            if (hasA && hasB) {
                return IconStatus.NO_ROTATION;
            } else if (hasC && hasD) {
                return IconStatus.ROTATE_270;
            }
        }
        return IconStatus.NO_SHOW;
    }

    public enum IconStatus {
        NO_ROTATION(0),
        ROTATE_270(270),
        NO_SHOW(0);

        private final float angle;

        IconStatus(float angle) {
            this.angle = angle * Mth.DEG_TO_RAD;
        }

        /**
         * Gets the angle in radians
         */
        public float getAngle() {
            return angle;
        }
    }
}