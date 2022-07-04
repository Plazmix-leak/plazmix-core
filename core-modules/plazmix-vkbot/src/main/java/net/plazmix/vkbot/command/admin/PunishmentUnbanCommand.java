package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class PunishmentUnbanCommand extends VkCommand {

    public PunishmentUnbanCommand() {
        super("разбан", "unban", "пардон", "pardon");

        setMinimalGroup(Group.MODER);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка в синтаксисе, используйте - !разбан <ник>");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (PunishmentManager.INSTANCE.hasPunishmentToPlayer(targetPlayer.getName(), PunishmentType.TEMP_BAN) ||
                PunishmentManager.INSTANCE.hasPunishmentToPlayer(targetPlayer.getName(), PunishmentType.PERMANENT_BAN)) {

            PunishmentManager.INSTANCE.unbanPlayer(targetPlayer.getName());
            vkBot.printMessage(message.getPeerId(), "✅ Вы успешно сняли блокировку с " + ChatColor.stripColor(targetPlayer.getDisplayName()));

            return;
        }

        vkBot.printMessage(message.getPeerId(), "Ошибка, данный пользователь не имеет текущих блокировок!");
    }

}
