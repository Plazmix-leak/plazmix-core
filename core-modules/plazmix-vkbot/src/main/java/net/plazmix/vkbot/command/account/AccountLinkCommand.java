package net.plazmix.vkbot.command.account;

import lombok.NonNull;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AccountLinkCommand extends VkCommand {

    public AccountLinkCommand() {
        super("привязать", "привязка", "привезать", "аккаунт");

        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(@NonNull BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length < 2) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте - !привязать <ник> <пароль>");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        //для даунов убираем еще скобочки
        String playerName = trimForNoobs(args[0]);
        String password = trimForNoobs(args[1]);

        if (botUser.hasPrimaryAccount()) {
            vkBot.printMessage(message.getPeerId(), "❗ К Вашему VK уже привязан аккаунт");
            return;
        }

        if (botUser.hasLinkedAccount(playerName)) {
            vkBot.printMessage(message.getPeerId(), "❗ К данному VK уже привязан аккаунт с ником " + playerName);
            return;
        }

        //ищем другого владельца данного аккаунта
        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(playerName);

        if (!AuthManager.INSTANCE.hasPlayerAccount(NetworkManager.INSTANCE.getPlayerId(playerName))) {
            vkBot.printMessage(message.getPeerId(), "❗ Данный игрок не зарегистрирован на сервере! \n \nВНИМАНИЕ! Для привязки аккаунта Вы должны зайти на сервер как минимум 1 раз после выхода обновления");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        //надо проверить что нам вернулся не пустой аккаунт, а реальный игрок
        if (authPlayer.hasVKUser()) {
            vkBot.printMessage(message.getPeerId(), "❗ Аккаунт с ником " + playerName + " уже привязан к другому VK");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        //если хеши паролей не совпадают - отдельное
        if (!authPlayer.equalsPassword(password)) {

            vkBot.printMessage(message.getPeerId(), "❗ Вы ошиблись при вводе пароля от аккаунта " + playerName);
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        authPlayer.setVkId(botUser.getVkId());

        botUser.addLinkedAccount(playerName);
        vkBot.deleteMessages(message.getMessageId());

        vkBot.printMessage(message.getPeerId(), "✒ Вы успешно привязали аккаунт " + playerName + " к вашему профилю VKонтакте!");
    }
}
