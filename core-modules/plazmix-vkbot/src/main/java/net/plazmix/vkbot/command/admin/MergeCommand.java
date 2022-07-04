package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class MergeCommand extends VkCommand {

    public MergeCommand() {
        super("перенос", "merge");

        setMinimalGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length < 2) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте -  !перенос <ник> <новый ник>");

            return;
        }

        String currentPlayerName = args[0];
        String targetPlayerName = args[1];

        Group groupToMerge = GroupManager.INSTANCE.getPlayerGroup(currentPlayerName);
        Group secondGroupToMerge = GroupManager.INSTANCE.getPlayerGroup(targetPlayerName);

        GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, secondGroupToMerge);
        GroupManager.INSTANCE.setGroupToPlayer(targetPlayerName, groupToMerge);

        vkBot.printMessage(message.getPeerId(), "Донат привилегия успешно перенесена с аккаунта " + currentPlayerName + " на аккаунт " + targetPlayerName);
    }
}
