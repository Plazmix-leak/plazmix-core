package net.plazmix.gameselector.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;
import net.plazmix.gameselector.inventory.GameArenaSelectInventory;
import net.plazmix.gameselector.inventory.GameModeSelectInventory;

import java.util.Collection;

public class GameSelectorCommand extends CommandExecutor {

    public GameSelectorCommand() {
        super("gameselector");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            if (corePlayer.getBukkitServer() == null) {
                return;
            }

            if (!ServerMode.isTyped(corePlayer.getBukkitServer(), ServerSubModeType.GAME_LOBBY)) {
                return;
            }

            // Открывать инвентарь выбора подтипа укаанного serverMode
            Collection<ServerSubMode> serverSubModeCollection = ServerMode.getMode(corePlayer.getBukkitServer()).getSubModes(ServerSubModeType.GAME_ARENA);
            new GameModeSelectInventory(serverSubModeCollection).openInventory(corePlayer);

        } else {

            // Открытие выбора сервера по указанному префиксу в команде
            ServerMode serverMode = ServerMode.getMode(args[0]);
            Collection<ServerSubMode> serverSubModeCollection = serverMode.getSubModes(ServerSubModeType.GAME_ARENA);

            ServerSubMode serverSubMode = serverSubModeCollection.stream()
                    .filter(subMode -> subMode.getSubPrefix().equals(args[0]))
                    .findFirst()
                    .orElse(null);

            if (serverSubMode != null) {
                new GameArenaSelectInventory(serverSubModeCollection, serverSubMode).openInventory(corePlayer);

            } else {

                new GameModeSelectInventory(serverSubModeCollection).openInventory(corePlayer);
            }
        }
    }

}
