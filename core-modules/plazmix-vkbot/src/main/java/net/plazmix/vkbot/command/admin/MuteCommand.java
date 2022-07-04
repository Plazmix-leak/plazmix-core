package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.punishment.Punishment;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Arrays;

public class MuteCommand extends VkCommand {

    public MuteCommand() {
        super("мут", "mute", "замутить");

        setMinimalGroup(Group.JR_MODER);
        setShouldLinkAccount(true);
    }

    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {

        String playerName = botUser.getPrimaryAccountName();
        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);
        Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(playerName);

        if (args.length < 3) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте !мут <ник> <причина> <время>");
            vkBot.printMessage(message.getPeerId(), " Пример: !мут Игрок Пиар сервера 10m");
            return;
        }
        if (!playerGroup.isStaff()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, вам нужен статус " + Group.JR_MODER.getName() + " и выше!");
            return;
        }
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не найден");
            return;
        }
        if (targetPlayer.getGroup().getLevel() >= corePlayer.getGroup().getLevel() && playerGroup.isAdmin()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, вы не можете замутить данного игрока, он выше или равен по правам :(");
            return;
        }

        String muteReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 2, args.length)));
        long muteTime = Long.parseLong(args[1]);

        vkBot.printMessage(message.getPeerId(), "✅ Вы успешно замутили " + ChatColor.stripColor(targetPlayer.getDisplayName()) + " с причиной: " + ChatColor.stripColor(muteReason));
        mutePlayer(corePlayer, targetPlayer, muteTime, muteReason);

        for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff()))
            staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §7замутил игрока " + targetPlayer.getDisplayName() + " §7через §9VK §7на " + muteTime + " §7по причине: " + muteReason);
    }

    public void mutePlayer(@NonNull CorePlayer ownerPlayer, @NonNull CorePlayer intruderPlayer, @NonNull long muteTime, @NonNull String muteReason) {
        PunishmentManager.INSTANCE.tempMutePlayer(ownerPlayer, intruderPlayer, muteReason, muteTime);
    }
}