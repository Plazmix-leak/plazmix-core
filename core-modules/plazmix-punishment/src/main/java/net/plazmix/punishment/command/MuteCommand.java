package net.plazmix.punishment.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MuteCommand extends CommandExecutor {

    public MuteCommand() {
        super("mute", "tempmute", "мут", "завались");

        setMinimalGroup(Group.JR_MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/mute <ник> <время> <причина>");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer.getGroup().getLevel() > corePlayer.getGroup().getLevel()) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не можете замутить данного игрока, так как он выше Вас по статусу!");
            return;
        }

        long muteTimeMillis = NumberUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);
        String kickReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        commandSender.sendMessage("§d§lPlazmix §8:: §fВы успешно замутили " + targetPlayer.getDisplayName()
                + " §7на §d" + NumberUtil.getTime(muteTimeMillis) + " с причиной: §e" + kickReason);

        PunishmentManager.INSTANCE.tempMutePlayer(corePlayer, targetPlayer, kickReason, muteTimeMillis);
    }
}
