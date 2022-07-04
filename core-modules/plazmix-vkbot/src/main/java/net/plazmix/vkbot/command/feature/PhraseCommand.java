package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PhraseCommand extends VkCommand {

    public PhraseCommand() {
        super("фраза", "цитата");

        setMinimalGroup(Group.STAR);
        setOnlyChats(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {

        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не переслали сообщение с фразой!");
            return;
        }

        Message forwardedMessage = message.getForwardedMessages().get(0);

        BotUser targetBotUser = BotUser.getVkUser(forwardedMessage.getUserId());
        if (!targetBotUser.hasPrimaryAccount()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, пользователь [id" + targetBotUser.getVkId() + "|id" + targetBotUser.getVkId() + "] не привязал игровой аккаунт к своему VK!");
            return;
        }

        BufferedImage bufferedImage = getPhraseImage(forwardedMessage.getUserId(), forwardedMessage);

        new Message()
                .peerId(message.getPeerId())
                .body("Цитата \"" + forwardedMessage.getBody() + "\" была успешно создана!")
                .photos(bufferedImage)
                .send(vkBot);
    }


    protected final int MAX_CHARSET_COUNT_IN_LINE = 50;

    protected BufferedImage getPhraseImage(int forwardPeerId, @NonNull Message forwardedMessage) {
        String playerName = "Ноунейм";
        BotUser botUser = BotUser.getVkUser(forwardPeerId);

        if (botUser.hasPrimaryAccount()) {
            playerName = botUser.getPrimaryAccountName();
        }

        BufferedImage bufferedImage = new BufferedImage(300, 150, BufferedImage.TYPE_USHORT_555_RGB);

        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawString(ChatColor.stripColor(PlazmixCore.getInstance().getOfflinePlayer(playerName).getDisplayName()) + ":", 25, 50);

        String textMessage = forwardedMessage.getBody();
        if (textMessage.length() > 35) {

            for (int i = 0 ; i < (textMessage.length() - i * MAX_CHARSET_COUNT_IN_LINE) % MAX_CHARSET_COUNT_IN_LINE; i++) {
                graphics.drawString(textMessage.substring(i * MAX_CHARSET_COUNT_IN_LINE), 25, 75);
            }

        } else {

            graphics.drawString(textMessage, 25, 75);
        }

        return bufferedImage;
    }

}
