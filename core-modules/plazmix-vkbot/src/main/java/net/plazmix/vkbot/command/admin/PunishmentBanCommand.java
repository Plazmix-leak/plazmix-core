package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PunishmentBanCommand extends VkCommand {

    public PunishmentBanCommand() {
        super("ban", "бан", "забанить", "пошелнахуй");

        setMinimalGroup(Group.MODER);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length < 3) {
            vkBot.printMessage(message.getPeerId(), "Ошибка в синтаксисе, используйте - !ban <ник> <время/-e> <причина>");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(botUser.getPrimaryAccountName());
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer.getGroup().getLevel() > corePlayer.getGroup().getLevel()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не можете забанить данного игрока, так как он выше Вас по статусу!");
            return;
        }

        if (targetPlayer.getName().equalsIgnoreCase(corePlayer.getName())) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не можете забанить самого себя!");
            return;
        }

        String kickReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        if (args[1].equalsIgnoreCase("-e") && corePlayer.getGroup().isAdmin()) {
            vkBot.printMessage(message.getPeerId(), "✅ Вы успешно забанили " + ChatColor.stripColor(targetPlayer.getDisplayName())
                    + " навсегда с причиной: " + ChatColor.stripColor(kickReason));

            PunishmentManager.INSTANCE.banPlayer(corePlayer, targetPlayer, kickReason);

            return;
        }

        long banTimeMillis = NumberUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);

        vkBot.printMessage(message.getPeerId(), "✅ Вы успешно забанили " + ChatColor.stripColor(targetPlayer.getDisplayName())
                + " на " + NumberUtil.getTime(banTimeMillis) + " с причиной: " + ChatColor.stripColor(kickReason));

        PunishmentManager.INSTANCE.tempBanPlayer(corePlayer, targetPlayer, kickReason, banTimeMillis);
    }

}
