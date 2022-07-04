package net.plazmix.core.common.coloredprefix;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.game.BukkitGameServer;
import net.plazmix.core.connection.server.mode.ServerMode;

public class PrefixCommand extends CommandExecutor {

    public PrefixCommand() {
        super("prefix", "color", "префикс", "цвет");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (ServerMode.isGameArena(corePlayer.getBukkitServer().getName())) {
            corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, вы не можете использовать эту команду на игровых серверах!");
            return;
        }

        if ((corePlayer.getGroup().isUniversal() && !corePlayer.getGroup().equals(Group.BUILDER)) || corePlayer.getGroup().isAdmin() || corePlayer.getGroup().equals(Group.LUXURY)) {
            new ColoredPrefixMenu().openInventory(corePlayer);

        } else {

            commandSender.sendLangMessage("NO_PERM", "%group%", Group.LUXURY.getColouredName());
        }
    }

}
