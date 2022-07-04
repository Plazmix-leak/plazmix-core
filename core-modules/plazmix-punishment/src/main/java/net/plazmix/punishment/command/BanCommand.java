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

public class BanCommand extends CommandExecutor {

    public BanCommand() {
        super("ban", "tempban", "бан", "съебал");

        setMinimalGroup(Group.MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/ban <ник> <время/-e> <причина>");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer.getGroup().getLevel() >= corePlayer.getGroup().getLevel()) {
            corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не можете кикнуть данного игрока, так как он выше Вас по статусу!");
            return;
        }

        String kickReason = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)));

        if (args[1].equalsIgnoreCase("-e") && corePlayer.getGroup().isAdmin()) {
            for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff())) {

                staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fзабанил игрока "
                        + targetPlayer.getDisplayName() + " §7по причине: §f" + kickReason);
            }

            corePlayer.sendMessage("§d§lPlazmix §8:: §fВы успешно забанили " + targetPlayer.getDisplayName()
                    + " §7навсегда с причиной: §e" + kickReason);

            PunishmentManager.INSTANCE.banPlayer(corePlayer, targetPlayer, kickReason);

            return;
        }

        long banTimeMillis = NumberUtil.parseTimeToMillis(args[1], TimeUnit.MILLISECONDS);

        for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff())) {

            staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fзабанил игрока "
                    + targetPlayer.getDisplayName() + " на §d" + NumberUtil.getTime(banTimeMillis) + " §7по причине: §f" + kickReason);
        }

        corePlayer.sendMessage("§d§lPlazmix §8:: §fВы успешно забанили " + targetPlayer.getDisplayName()
                + " на §d" + NumberUtil.getTime(banTimeMillis) + " §7с причиной: §e" + kickReason);

        PunishmentManager.INSTANCE.tempBanPlayer(corePlayer, targetPlayer, kickReason, banTimeMillis);
    }

}
