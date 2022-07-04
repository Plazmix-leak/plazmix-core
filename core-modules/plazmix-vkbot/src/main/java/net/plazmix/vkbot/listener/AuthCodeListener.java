package net.plazmix.vkbot.listener;

import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthSendCodeEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import net.plazmix.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;

import java.util.concurrent.TimeUnit;

public class AuthCodeListener implements EventListener {

    @EventHandler
    public void onSendCode(PlayerAuthSendCodeEvent playerAuthSendCodeEvent) {
        AuthPlayer authPlayer = playerAuthSendCodeEvent.getAuthPlayer();
        CorePlayer corePlayer = authPlayer.getHandle();

        new Message()
                .peerId(authPlayer.getVkId())
                .body("\uD83D\uDD12 Был совершен вход на аккаунт " + ChatColor.stripColor(corePlayer.getDisplayName()) + "\n\n" +
                        "\uD83D\uDCCA IP: " + corePlayer.getInetSocketAddress().getHostName() +
                        "\n\n Если это не Вы, то срочно восстановите свой аккаунт!" + "\n\n" +
                        "Для восстановления аккаунта напишите команду !восстановить")

                .keyboard(true, true)

                .button(KeyboardButtonColor.POSITIVE, 0, new TextButtonAction("!twofactoraccept " + authPlayer.getPlayerName(), "Разрешить вход"))
                .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("!twofactordeny " + authPlayer.getPlayerName(), "Запретить вход"))

                .message()
                .send(VkBot.INSTANCE);

        new CommonScheduler() {

            @Override
            public void run() {
                corePlayer.sendMessage("§d§lPlazmix §8:: §eВам было отправлено сообщение в VK (id" + authPlayer.getVkId() + ")");
                corePlayer.sendMessage("§d§lPlazmix §8:: §fЧтобы выполнить вход, подтвердите его через §9ВКонтакте§7");
                corePlayer.sendMessage("§d§lPlazmix §8:: §fДля этого нажмите кнопку \"§aРазрешить вход\"");
            }

        }.runLater(1, TimeUnit.SECONDS);
    }
}
