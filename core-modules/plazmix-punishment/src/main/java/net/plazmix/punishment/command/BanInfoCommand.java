package net.plazmix.punishment.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.punishment.Punishment;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.player.CorePlayer;

public class BanInfoCommand extends CommandExecutor {

    public BanInfoCommand() {
        super("baninfo","банинфо");

        setMinimalGroup(Group.MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/baninfo <ник>");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (targetPlayer.getPlayerId() < 1) {
            commandSender.sendLangMessage("NO_PLAYER");
            return;
        }

        Punishment permamentBan = PunishmentManager.INSTANCE.getPlayerPunishment(targetPlayer.getName(), PunishmentType.PERMANENT_BAN);
        Punishment tempBan      = PunishmentManager.INSTANCE.getPlayerPunishment(targetPlayer.getName(), PunishmentType.TEMP_BAN);

        if (permamentBan != null || tempBan != null) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИнформация о игроке: " + targetPlayer.getDisplayName());

            if (PunishmentManager.INSTANCE.getPlayerPunishment(targetPlayer.getName(), PunishmentType.PERMANENT_BAN) != null) {
                commandSender.sendMessage(" §fВыдал блокировку: " + (permamentBan.getPunishmentOwner()));
                commandSender.sendMessage(" §fТип блокировки: §aПермаментная");

                CorePlayer punishmentPermanentBanOwner = PlazmixCore.getInstance().getOfflinePlayer(permamentBan.getPunishmentOwner());

                commandSender.sendMessage("§7· §fПричина блокировки: §c" + (punishmentPermanentBanOwner.getDisplayName()));

            } else {
                CorePlayer punishmentTempBanOwner = PlazmixCore.getInstance().getOfflinePlayer(tempBan.getPunishmentOwner());

                commandSender.sendMessage(" §fВыдал блокировку: " + (punishmentTempBanOwner.getDisplayName()));
                commandSender.sendMessage(" §fТип блокировки: §aВременная");
                commandSender.sendMessage(" §fПричина блокировки: §c" + (tempBan.getPunishmentReason()));

                commandSender.sendMessage(" §fИстекает через: §d" + NumberUtil.getTime((tempBan.getPunishmentTime() - System.currentTimeMillis())));
            }
        }
    }

}
