package net.plazmix.streams.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.streams.inventory.StreamsInventory;

public class StreamsCommand extends CommandExecutor {

    public StreamsCommand() {
        super("streams", "streamlist", "streamslist", "liststream", "liststreams");

        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new StreamsInventory().openInventory((CorePlayer) commandSender);
    }

}
