package net.plazmix.chat.command.post;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.chat.util.PostMessageUtil;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;

public class ReplyCommand extends CommandExecutor {

    public ReplyCommand() {
        super("reply", "r", "ответ", "ответить");
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length == 0) {
            commandSender.sendLangMessage("REPLY_CHAT_FORMAT");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);
        String message = ChatColor.stripColor(Joiner.on(" ").join(args));

        PostMessageUtil.replyMessage(corePlayer, message);
    }

}
