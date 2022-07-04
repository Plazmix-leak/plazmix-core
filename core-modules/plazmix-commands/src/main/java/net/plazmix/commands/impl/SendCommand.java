package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

public class SendCommand extends CommandExecutor {

    public SendCommand() {
        super("send", "playersend", "sendserver", "сенд", "redirect");

        setMinimalGroup(Group.ADMIN);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 2) {
            commandSender.sendLangMessage("SEND_HELP");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);
        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(args[1]);

        if (!targetPlayer.isOnline()) {
            commandSender.sendLangMessage("PLAYER_OFFLINE");
            return;
        }

        if (bukkitServer == null) {
            commandSender.sendLangMessage("SERVER_NOT_FOUND");
            return;
        }

        if (bukkitServer.getName().startsWith("build")) {
            if (!targetPlayer.getGroup().isAdmin() || targetPlayer.getGroup() == Group.BUILDER || targetPlayer.getGroup() == Group.SR_BUILDER) {
                commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, вы не можете переместить " + targetPlayer.getDisplayName() + " §cна данный тип сервера!");
                return;
            }

            bukkitServer.dispatchCommand("op " + targetPlayer.getName());
        }

        targetPlayer.connectToServer(bukkitServer);

        commandSender.sendMessage("§d§lPlazmix §8:: " + targetPlayer.getDisplayName() + " §fбыл успешно телепортирован на сервер §e" + bukkitServer.getName());
        targetPlayer.sendMessage("§d§lPlazmix §8:: " + commandSender.getDisplayName() + " §fперенес Вас на сервер §e" + bukkitServer.getName());
    }

}
