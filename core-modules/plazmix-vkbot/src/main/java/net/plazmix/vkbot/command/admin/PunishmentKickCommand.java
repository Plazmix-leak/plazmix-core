package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Arrays;

public class PunishmentKickCommand extends VkCommand {

    public PunishmentKickCommand() {
        super("кик", "кикнуть", "kick");
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        String playerName = botUser.getPrimaryAccountName();

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);
        Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(playerName);

        if (args.length == 0) {

            if (corePlayer == null) {
                vkBot.printMessage(message.getPeerId(), "Аккаунт " + playerGroup.name() + " " + playerName + " не найден!");
                return;
            }

            if (!corePlayer.isOnline()) {
                vkBot.printMessage(message.getPeerId(), "Аккаунт " + playerGroup.name() + " " + playerName + " не в сети!");
                return;
            }

            corePlayer.disconnect("§cВы были кикнуты с сервера при помощи VK бота!");
            vkBot.printMessage(message.getPeerId(), "Аккаунт " + playerGroup.name() + " " + playerName + " был успешно кикнут с сервера!");

            return;
        }

        if (!playerGroup.isStaff()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, для выполнения данной команды требутеся статус " + Group.JR_MODER.getName() + " и выше!");
            return;
        }

        if (args.length < 2) {
            vkBot.printMessage(message.getPeerId(), "Ошибка в синтаксисе, используйте - !кик <ник> <причина>");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer == null) {
            Group targetGroup = GroupManager.INSTANCE.getPlayerGroup(args[0]);

            vkBot.printMessage(message.getPeerId(), "Аккаунт " + targetGroup.name() + " " + args[0] + " не в сети!");
            return;
        }

        if (targetPlayer.getGroup().getLevel() >= corePlayer.getGroup().getLevel()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не можете кикнуть данного игрока, так как он выше Вас по статусу!");
            return;
        }

        String kickReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)));

        vkBot.printMessage(message.getPeerId(), "✅ Вы успешно кикнули " + ChatColor.stripColor(targetPlayer.getDisplayName())
                + " с причиной: " + ChatColor.stripColor(kickReason));

        PunishmentManager.INSTANCE.kickPlayer(corePlayer, targetPlayer, kickReason);
    }
    
}
