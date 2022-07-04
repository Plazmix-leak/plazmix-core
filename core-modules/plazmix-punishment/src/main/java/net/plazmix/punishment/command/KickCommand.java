package net.plazmix.punishment.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;

public class KickCommand extends CommandExecutor {

    public KickCommand() {
        super("kick", "кик");

        setMinimalGroup(Group.JR_MODER);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer playerSender = PlazmixCore.getInstance().getPlayer(commandSender.getName());
        Group playerGroup = playerSender.getGroup();

        if (args.length < 2) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/kick <ник> <причина>");

            return;
        }

        String targetPlayerName = (args[0]);

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(targetPlayerName);
        Group targetGroup = NetworkManager.INSTANCE.getPlayerGroup(targetPlayerName);

        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));

        if (corePlayer == null) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, игрок " + targetPlayerName + " оффлайн!");
            return;
        }

        if (targetGroup.getLevel() >= playerGroup.getLevel()) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, вы не можете кикнуть данного игрока!");
            return;
        }

        for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff())) {

            staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + commandSender.getDisplayName() + " §fкикнул игрока "
                    + corePlayer.getDisplayName() + " §7по причине: §f" + reason);
        }

        PunishmentManager.INSTANCE.kickPlayer(playerSender, corePlayer, reason);
    }
}
