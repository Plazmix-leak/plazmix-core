package net.plazmix.chat.command.post;

import lombok.NonNull;
import net.plazmix.chat.util.PostMessageUtil;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;

public class IgnoreAllCommand extends CommandExecutor {

    public IgnoreAllCommand() {
        super("ignoreall", "ignore-all");

        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);
        PostMessageUtil.ignoreAll(corePlayer);
    }

}
