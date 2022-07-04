package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

public class ServerCommand extends CommandExecutor {

    public ServerCommand() {
        super("server", "serv", "сервер", "connect");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
        setMinimalGroup(Group.QA);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            corePlayer.sendLangMessage("SERVER_HELP");
            return;
        }

        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(args[0]);

        if (bukkitServer == null) {
            corePlayer.sendLangMessage("SERVER_NOT_FOUND");
            return;
        }

        if (corePlayer.getBukkitServer().getName().equals(bukkitServer.getName())) {
            corePlayer.sendLangMessage("ALREADY_CONNECTION");
            return;
        }

        if (bukkitServer.getName().startsWith("build")) {
            if (!corePlayer.getGroup().isAdmin() || corePlayer.getGroup() == Group.BUILDER || corePlayer.getGroup() == Group.SR_BUILDER) {
                corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, на данный сервер может попасть только команда проекта!");
                return;
            }

            bukkitServer.dispatchCommand("op " + corePlayer.getName());
        }

        corePlayer.connectToServer(bukkitServer);
    }

}
