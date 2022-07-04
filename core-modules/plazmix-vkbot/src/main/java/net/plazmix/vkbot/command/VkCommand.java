package net.plazmix.vkbot.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.user.BotUser;

import java.util.List;

@Getter
public abstract class VkCommand {

    private final String[] aliases;

    public VkCommand(String... aliases) {
        Preconditions.checkArgument(aliases.length > 0, "You must specify command name as a first argument in constructor");

        this.aliases = aliases;
    }

    /**
     * Будет ли команда доступна только тем, у кого
     * установлен основной аккаунт в боте
     */
    @Setter
    private boolean shouldLinkAccount = false;

    /**
     * Доступна ли команда для использования только в ЛС
     * основной группы
     */
    @Setter
    private boolean onlyPrivateMessages = false;

    /**
     * Команда доступна только в беседах
     */
    @Setter
    private boolean onlyChats = false;

    /**
     * Минимальный статус доступа к команде
     */
    @Setter
    private Group minimalGroup = Group.ABOBA;


    public void dispatchCommand(@NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        try {
            BotUser botUser = BotUser.getVkUser(message.isFromChat() ? message.getUserId() : message.getPeerId());

            if (isOnlyPrivateMessages() && message.isFromChat()) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, для использования данной команды Вам необходимо перейти в личные сообщение бота: https://vk.me/plzmbot");

                return;
            }

            if (isShouldLinkAccount() && !botUser.hasPrimaryAccount()) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, Вы не имеете привязанного аккаунта к VK!\n" +
                        "\n\uD83D\uDCE2Введите !привязать <ваш ник> <пароль>, чтобы привязать аккаунт!");
                return;
            }

            if (botUser.hasPrimaryAccount()) {
                Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(botUser.getPrimaryAccountName());

                if (playerGroup.getLevel() < getMinimalGroup().getLevel()) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, для выполнения данной команды необходим статус " + minimalGroup.getName() + " и выше!");
                    return;
                }
            }

            execute(botUser, message, args, vkBot);
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Message getFirstForwardedMessage(@NonNull Message message) {
        List<Message> forwarded = message.getForwardedMessages();

        return forwarded.isEmpty() ? null : forwarded.get(0);
    }

    protected abstract void execute(
            BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot
    );

    protected static String trimForNoobs(@NonNull String commandLine) {
        return commandLine.replace("<", "").replace(">", "");
    }

}
