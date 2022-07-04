package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;

public class HubCommand extends CommandExecutor {

    public HubCommand() {
        super("hub", "lobby", "рги", "дщиин");

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (corePlayer.getBukkitServer().getName().toLowerCase().startsWith("hub")) {
            corePlayer.sendLangMessage("ALREADY_CONNECTION");
            return;
        }

        if (args.length == 0) {
            String playerServerName = corePlayer.getBukkitServer().getName();

            if (ServerMode.isGameArena(playerServerName)) {
                connect(corePlayer, ServerMode.getMode(playerServerName).getSubModes(ServerSubModeType.GAME_LOBBY)
                        .stream()
                        .map(ServerSubMode::getSubPrefix)
                        .findFirst()
                        .orElse("hub"));
            }
            else {
                connect(corePlayer, "hub");
            }

        } else {

            connect(corePlayer, args[0]);
        }
    }

    private void connect(CorePlayer corePlayer, String serverPrefix) {

        if (PlazmixCore.getInstance().getServersByPrefix(serverPrefix).isEmpty()) {
            corePlayer.sendLangMessage("HUB_NOT_FOUND");
        }

        ServerMode serverMode = ServerMode.getMode(serverPrefix);

        if (serverMode.equals(ServerMode.HUB)) {
            corePlayer.connectToAnyServer("hub");
            return;
        }

        if (serverMode.getSubModes(ServerSubModeType.GAME_LOBBY).isEmpty()) {
            corePlayer.sendLangMessage("LOBBY_NOT_FOUND");

        } else {

            BukkitServer lobbyServer = PlazmixCore.getInstance().getBestServer(serverMode.getSubModes(ServerSubModeType.GAME_LOBBY).stream().findFirst().get());

            if (lobbyServer != null) {
                corePlayer.connectToServer(lobbyServer);

            } else {

                corePlayer.sendLangMessage("LOBBY_NOT_FOUND");
            }
        }
    }

}
