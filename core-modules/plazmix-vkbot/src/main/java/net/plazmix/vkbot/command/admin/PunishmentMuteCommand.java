package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;
import net.plazmix.core.common.punishment.PunishmentManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PunishmentMuteCommand extends VkCommand {

    public PunishmentMuteCommand() {
        super("мут", "mute", "замутить", "завалиебало");

        setMinimalGroup(Group.JR_MODER);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length < 3) {
            vkBot.printMessage(message.getPeerId(), "Ошибка в синтаксисе, используйте - !мут <ник> <время> <причина>");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(botUser.getPrimaryAccountName());
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer.getGroup().getLevel() > corePlayer.getGroup().getLevel()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не можете замутить данного игрока, так как он выше Вас по статусу!");
            return;
        }

        if (targetPlayer.equals(corePlayer.getName())) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не можете замутить самого себя!");
            return;
        }

        long muteTimeMillis = NumberUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);
        String kickReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        vkBot.printMessage(message.getPeerId(), "✅ Вы успешно замутили " + ChatColor.stripColor(targetPlayer.getDisplayName())
                + " на " + NumberUtil.getTime(muteTimeMillis) + " с причиной: " + ChatColor.stripColor(kickReason));

        PunishmentManager.INSTANCE.tempMutePlayer(corePlayer, targetPlayer, kickReason, muteTimeMillis);
    }
}
