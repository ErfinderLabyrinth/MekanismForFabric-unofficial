package mekanism.common.capabilities.proxy;

import mekanism.api.IConfigurable;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

@NothingNullByDefault
public class ProxyConfigurable {//Unused
    public interface ISidedConfigurable extends IConfigurable {

        InteractionResult onSneakRightClick(Player player, Direction side);

        @Override
        default InteractionResult onSneakRightClick(Player player) {
            return InteractionResult.PASS;
        }

        InteractionResult onRightClick(Player player, Direction side);

        @Override
        default InteractionResult onRightClick(Player player) {
            return InteractionResult.PASS;
        }
    }
}