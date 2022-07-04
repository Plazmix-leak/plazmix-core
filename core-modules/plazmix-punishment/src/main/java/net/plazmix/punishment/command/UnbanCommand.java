package net.plazmix.punishment.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;

public class UnbanCommand extends CommandExecutor {

    public UnbanCommand() {
        super("unban", "разбан");

        setMinimalGroup(Group.MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/unban <ник>");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);
        CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

            corePlayer.sendMessage("§d§lPlazmix §8:: §fВы успешно разбанили " + targetPlayer.getDisplayName());

            PunishmentManager.INSTANCE.unbanPlayer(targetPlayer.getName());
    }

}
