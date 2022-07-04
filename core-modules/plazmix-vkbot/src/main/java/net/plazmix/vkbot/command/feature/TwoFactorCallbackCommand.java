package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class TwoFactorCallbackCommand extends VkCommand {

    private final boolean allowLogin;

    public TwoFactorCallbackCommand(boolean allowLogin) {
        super("twofactor" + (allowLogin ? "accept" : "deny"));
        this.allowLogin = allowLogin;

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (!AuthManager.INSTANCE.hasTwofactorSession(args[0])) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, сессия была преждевременно завершена!");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(args[0]);
        AuthManager.INSTANCE.removeTwofactorSession(args[0]);

        if (corePlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Упс... Аккаунт уже не в сети :(");
            return;
        }

        if (allowLogin) {
            corePlayer.sendMessage("§aВход был успешно подтвержден через VK!\n§aПриятной игры!");
            vkBot.printMessage(message.getPeerId(), "❗ Вход успешно выполнен, приятной игры!");

            AuthManager.INSTANCE.getAuthPlayer(corePlayer.getPlayerId()).complete();
            return;
        }

        corePlayer.disconnect("§cВаша сессия была завершена через VK");
        vkBot.printMessage(message.getPeerId(), "Сессия входа была завершена! Советуем вам сменить пароль от аккаунта. \n Используйте команду !восстановить для этого");
    }

}
