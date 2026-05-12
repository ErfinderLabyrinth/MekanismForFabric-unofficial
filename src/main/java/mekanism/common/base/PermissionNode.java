package mekanism.common.base;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PermissionNode {
    String modid;
    String nodeName;
    int permissionLevel;

    public PermissionNode(String modid, String nodeName, int permissionLevel) {
        this.modid = modid;
        this.nodeName = nodeName;
        this.permissionLevel = permissionLevel;
    }


    public String getNodeName() {
        return nodeName;
    }

    public boolean test(Player player) {
        return Permissions.check(player, getPermission(), permissionLevel);
    }

    public CompletableFuture<Boolean> test(UUID uuid) {
        return Permissions.check(uuid, getPermission(), permissionLevel == 0);
    }

    public String getPermission() {
        return modid + "." + nodeName;
    }
}
