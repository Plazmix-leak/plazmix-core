package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class GroupCommand extends VkCommand {

    public GroupCommand() {
        super("группа", "group");

        setMinimalGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyChats(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length < 2) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте -  !группа <ник> <группа>");
            return;
        }

        String currentPlayerName = args[0];
        Group groupToSet = ValidateUtil.isNumber(args[1]) ? Group.getGroupByLevel(Integer.parseInt(args[1])) : Group.getGroupByName(args[1]);

        if (groupToSet == null) {
            groupToSet = Group.DEFAULT;
        }

        GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, groupToSet);
        vkBot.printMessage(message.getPeerId(), "Группа " + groupToSet.getName() + " была выдана игроку " + currentPlayerName);
    }
}
