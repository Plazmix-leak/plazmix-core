package net.plazmix.vkbot.command.account;

import lombok.NonNull;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AccountUnLinkCommand extends VkCommand {

    public AccountUnLinkCommand() {
        super("отвязать", "отвязка", "отвезать", "отвяжи", "отвежи");

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте - !отвязать <ник>");

            return;
        }

        //для даунов убираем еще скобочки
        String playerName = trimForNoobs(args[0]);

        if (!botUser.hasLinkedAccount(playerName)) {
            vkBot.printMessage(message.getPeerId(), "❗ К Вашему VK не привязан аккаунт с ником " + playerName);
            return;
        }

        botUser.removeLinkedAccount();

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(botUser.getPrimaryAccountName());
        authPlayer.setVkId(-1);
        authPlayer.updateVkId();

        vkBot.printMessage(message.getPeerId(), "❗ Вы отвязали аккаунт " + playerName + " от своего VK." +
                "\nТеперь он в опасности, поскольку больше не защищен двухфакторной авторизацией." +
                "\n\n\uD83D\uDD25Напоминаем, что у нас запрещены передачи аккаунтов а также коммерческая деятельность, связанная с ними");
    }
}
