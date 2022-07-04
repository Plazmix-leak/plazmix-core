package net.plazmix.vkbot.handler;

import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.utility.query.AsyncUtil;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.handler.CallbackApiHandler;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Arrays;

@RequiredArgsConstructor
public class BotCallbackApiHandler implements CallbackApiHandler {

    private final VkBot vkBot;

    @Override
    public void onMessage(Message message) {
        String payloadValue = message.getPayloadValue("payload");

        if (payloadValue == null) {
            payloadValue = message.getBody();
        }

        //проверяем наличие сессий перехвата сообщений
        BotUser botUser = BotUser.getVkUser(message.getPeerId());
        if (botUser.hasPrimaryAccount() && botUser.hasMessageHandlers()) {
            botUser.handleMessageHandlers(payloadValue);
            return;
        }

        //получаем аргументы команды
        String[] commandArray = payloadValue.trim().replaceFirst("!", "").split(" ");
        String[] commandArgs = Arrays.copyOfRange(commandArray, 1, commandArray.length);

        //получаем саму команду в боте, которая должна быть зарегана
        VkCommand cmd = vkBot.getCommand(commandArray[0].toLowerCase());

        //если команда не зарегана исполнять нам нечего
        if (cmd == null) {
            return;
        }

        //нуб-команды в чатах все равно не должны работать
        if (!payloadValue.startsWith("!") && message.isFromChat()) {
            return;
        }

        AsyncUtil.submitAsync(() -> cmd.dispatchCommand(message, commandArgs, vkBot));
    }

    @Override
    public void onChatUserInvite(int chatId, int inviteId, int invitedId) {

    }

    @Override
    public void onChatUserJoin(int chatId, int userId) {

    }

    @Override
    public void onChatUserKick(int chatId, int kickId, int kickedId) {

    }

    @Override
    public void onChatTitleChange(int chatId, int userId, String newTitle) {

    }

    @Override
    public void onWallPostNew(int groupId, int postId, int ownerId, String link, String message) {
        //String ownerName = null;
        //BotUser botUser = BotUser.getVkUser(ownerId);
        //
        //if (botUser.hasPrimaryAccount()) {
        //    ownerName = botUser.getPrimaryAccountName();
        //}


        for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {

            corePlayer.sendMessage("§d§lPlazmix §8:: §fВ группе §9ВКонтакте §fопубликован новый пост:");
            corePlayer.sendMessage(" ");
            corePlayer.sendMessage(" §7" + message.split("\n")[0]);
            corePlayer.sendMessage(" §7...");
            //corePlayer.sendMessage(" §fАвтор: " + (ownerName != null ? TynixCore.getInstance().getOfflinePlayer(ownerName).getDisplayName() : "§cНеизвестно"));

            corePlayer.sendMessage(ChatMessageType.CHAT, JsonChatMessage.create("§e▸ Читать полностью [Клик]")
                    .addHover(HoverEvent.Action.SHOW_TEXT, "§eНажмите, чтобы продолжить читать пост")
                    .addClick(ClickEvent.Action.OPEN_URL, link)
                    .build());
        }
    }

}
