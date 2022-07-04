package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Arrays;

public class ByeByeCommand extends VkCommand {

    public ByeByeCommand() {
        super("бб", "пошёлнахуй", "кик", "кикнуть", "kick", "bb");

        setMinimalGroup(Group.ADMIN);

        setOnlyChats(true);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не переслали сообщение необходимого пользователя!");
            return;
        }

        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не указали причину кика");
            return;
        }

        Message forwardedMessage = message.getForwardedMessages().get(0);
        vkBot.kick(message.getChatId(), forwardedMessage.getUserId());

        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 0, args.length));
        vkBot.printMessage(message.getPeerId(), "Пользователь [id" + forwardedMessage.getUserId() + "|id" + forwardedMessage.getUserId()
                + "] был кикнут из беседы \n\nПричина: " + reason + "\nКикнул: "
                + ChatColor.stripColor(PlazmixCore.getInstance().getOfflinePlayer(botUser.getPrimaryAccountName()).getDisplayName()));
    }
}
