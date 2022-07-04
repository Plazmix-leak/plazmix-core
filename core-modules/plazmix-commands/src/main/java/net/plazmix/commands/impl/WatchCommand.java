package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.game.GameServerInfo;
import net.plazmix.core.connection.server.impl.BukkitServer;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.TimeUnit;

public class WatchCommand
        extends CommandExecutor {

    public WatchCommand() {
        super("watch", "спек", "спектатор", "spectate", "spec");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
        setMinimalGroup(Group.MODER);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendLangMessage("WATCH_HELP");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (!targetPlayer.isOnline()) {
            commandSender.sendLangMessage("PLAYER_OFFLINE");
            return;
        }

        if (targetPlayer.getName().equalsIgnoreCase(commandSender.getName())) {
            commandSender.sendLangMessage("WATCH_ERROR_SPECTATE_YOURSELF");
            return;
        }

        BukkitServer targetServer = targetPlayer.getBukkitServer();
        if (targetServer.getName().equals(corePlayer.getBukkitServer().getName())) {
            return;
        }

        GameServerInfo serverInfo = GameServerInfo.of(targetServer);
        if (serverInfo == null || serverInfo.isAvailable()) {
            commandSender.sendLangMessage("WATCH_ERROR_GAME_ARENA_NOT_STARTED");
            return;
        }

        corePlayer.connectToServer(targetServer);

        for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff()))
            staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + commandSender.getDisplayName() + " §7отправился следить за " + targetPlayer.getDisplayName());

    }
}