package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

public class ThrowCommand extends CommandExecutor {

    public ThrowCommand() {
        super("throw");

        setMinimalGroup(Group.DEVELOPER);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/throw <server 1> <server 2>");
            return;
        }

        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(args[0]);

        if (bukkitServer == null) {
            commandSender.sendLangMessage("SERVER_NOT_FOUND");
            return;
        }

        BukkitServer targetServer = PlazmixCore.getInstance().getBukkitServer(args[1]);

        if (targetServer == null) {
            targetServer = PlazmixCore.getInstance().getPlayer(commandSender.getName()).getBukkitServer();
        }

        if (targetServer.getName().startsWith("build")) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, вы не можете перемещать всех на данный тип серверов.");
            return;
        }

        for (CorePlayer corePlayer : bukkitServer.getOnlinePlayers()) {
            corePlayer.connectToServer(targetServer);
        }

        commandSender.sendMessage("§d§lPlazmix §8:: §fВсе игроки с сервера §e" + bukkitServer.getName() + " §fбыли телепортированы на сервер: §e" + targetServer.getName());
    }

}
