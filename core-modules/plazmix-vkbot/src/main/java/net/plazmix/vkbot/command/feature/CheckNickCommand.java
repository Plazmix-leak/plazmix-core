package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class CheckNickCommand extends VkCommand {

    public CheckNickCommand() {
        super("ник", "никнейм");

        setOnlyChats(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, Вы не переслали сообщение необходимого пользователя!");
            return;
        }

        Message forwardedMessage = message.getForwardedMessages().get(0);
        BotUser targetBotUser = BotUser.getVkUser(forwardedMessage.getUserId());

        if (!targetBotUser.hasPrimaryAccount()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, пользователь [id" + targetBotUser.getVkId() + "|id" + targetBotUser.getVkId() + "] не привязал игровой аккаунт к своему VK!");
            return;
        }

        String playerName = targetBotUser.getPrimaryAccountName();

        vkBot.printMessage(message.getPeerId(), "❗ Игровой ник пользователя [id" + targetBotUser.getVkId() + "|id" + targetBotUser.getVkId() + "] - " +
                ChatColor.stripColor(PlazmixCore.getInstance().getOfflinePlayer(playerName).getDisplayName()));
    }

}
