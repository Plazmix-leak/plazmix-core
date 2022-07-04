package net.plazmix.vkbot.command.account;

import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AccountRecoveryCommand extends VkCommand {

    public AccountRecoveryCommand() {
        super("восстановить");
        
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(@NonNull BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(botUser.getPrimaryAccountName());
        CorePlayer corePlayer = authPlayer.getHandle();

        if (corePlayer != null && corePlayer.isOnline()) {
            corePlayer.disconnect("§eВаши игровые данные были обновлены" +
                    "\n§eПожалуйста, перезайдите на сервер!");
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        authPlayer.setNewPassword(newPassword);

        vkBot.printAndDeleteMessage(message.getPeerId(), "❗ Восстановление прошло успешно. Ваш новый пароль: " + newPassword);
    }
}
