package mekanism.common.lib.transmitter.acceptor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class AbstractAcceptorInfo {

    private final Level level;
    private final BlockPos pos;

    protected AbstractAcceptorInfo(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }
}