package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.common.group.Group;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AdminRecoveryCommand extends VkCommand {

    public AdminRecoveryCommand() {
        super("сменитьпароль", "changepassword", "recovery", "восстановить");

        setMinimalGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте !восстановить <ник>");
            return;
        }

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(args[0]);

        if (authPlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не зарегестрирован");
            return;
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        authPlayer.setNewPassword(newPassword);

        AuthManager.INSTANCE.removeSession(authPlayer.getHandle());

        if (authPlayer.hasVKUser()) {
            BotUser targetBotUser = BotUser.getVkUser(authPlayer.getVkId());

            targetBotUser.removeLinkedAccount();
        }

        vkBot.printAndDeleteMessage(message.getPeerId(), "Новый пароль игрока " + args[0] + " - " + newPassword);
    }
}
