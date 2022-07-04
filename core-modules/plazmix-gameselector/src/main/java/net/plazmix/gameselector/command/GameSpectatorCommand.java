package net.plazmix.gameselector.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.gameselector.inventory.GameSpectatorInventory;

public class GameSpectatorCommand extends CommandExecutor {

    public GameSpectatorCommand() {
        super("gamespectator");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length > 0) {
            new GameSpectatorInventory(args[0]).openInventory(corePlayer);
        }
    }
}
