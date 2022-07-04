package net.plazmix.chat.command.post;

import lombok.NonNull;
import net.plazmix.chat.util.PostMessageUtil;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;

public class IgnoreCommand extends CommandExecutor {

    public IgnoreCommand() {
        super("ignore", "игнор", "игнорировать");

        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendLangMessage("IGNORE_CHAT_FORMAT");

            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);
        PostMessageUtil.ignore(corePlayer, targetPlayer);
    }

}
