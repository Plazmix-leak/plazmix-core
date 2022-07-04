package net.plazmix.chat.command.post;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.chat.util.PostMessageUtil;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;

public class PostMessageCommand extends CommandExecutor {

    public PostMessageCommand() {
        super("msg", "m", "message", "pm", "postmessage", "pmsg", "смс", "sms", "pmessage", "лс", "сообщение");
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length < 2) {
            corePlayer.sendLangMessage("MESSAGE_CHAT_FORMAT");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);
        String message = ChatColor.stripColor(Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)));

        PostMessageUtil.sendMessage(corePlayer, targetPlayer, message);
    }
}
