package mekanism.client;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class IncompleteRecipeScanner {

    private static final Component RECIPE_WARNING = MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, MekanismLang.MEKANISM/*, EnumColor.GRAY*/, MekanismLang.RECIPE_WARNING.translate());
    private static boolean foundIncompleteRecipes = false;

    public static void recipes(ServerPlayer player, boolean joined) {
        //player is logging in
        if (player != null) {
            if (foundIncompleteRecipes) {
                sendMessageToPlayer(player);
            }
            //skip running scan on player login, should have run at start or last reload
            return;
        }

        //run the scan
        foundIncompleteRecipes = MekanismRecipeType.checkIncompleteRecipes(player.getServer());

        //if broken, message any players online
        if (foundIncompleteRecipes) {
            List<ServerPlayer> players = player.getServer().getPlayerList().getPlayers();
            if (!players.isEmpty()) {
                players.forEach(IncompleteRecipeScanner::sendMessageToPlayer);
            }
        }
    }

    private static void sendMessageToPlayer(ServerPlayer player) {
        player.sendSystemMessage(RECIPE_WARNING);
    }

    public static void serverStarted(MinecraftServer server) {
        //run the scan. In theory there will be no players at this point, so shouldn't need to send message
        foundIncompleteRecipes = MekanismRecipeType.checkIncompleteRecipes(server);
    }
}