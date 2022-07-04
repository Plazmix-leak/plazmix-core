package net.plazmix.reports.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.report.ReportManager;

import java.util.Arrays;

public class ReportSendCommand extends CommandExecutor {

    public ReportSendCommand() {
        super("report", "жалоба", "читак", "репорт");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/report <ник> <причина>");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);
        CorePlayer targetPlayer = PlazmixCore.getInstance().getPlayer(args[0]);

        if (targetPlayer == null) {
            commandSender.sendLangMessage("PLAYER_OFFLINE");
            return;
        }

        if (targetPlayer.getName().equalsIgnoreCase(corePlayer.getName())) {
            commandSender.sendMessage("§d§lЖалобы §8:: §cОшибка, Вы не можете жаловаться сами на себя!");
            return;
        }

        if (ReportManager.INSTANCE.hasReport(corePlayer.getName(), targetPlayer.getName())) {
            commandSender.sendMessage("§d§lЖалобы §8:: §cОшибка, Вы уже жаловались на данного игрока!");
            return;
        }

        String reportReason = Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 1, args.length));
        ReportManager.INSTANCE.createReport(targetPlayer, corePlayer, reportReason);

        corePlayer.sendMessage("§d§lЖалобы §8:: §fВы успешно отправили жалобу на игрока " + targetPlayer.getDisplayName());

        if (PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff()).isEmpty())
            corePlayer.sendMessage(" §cНа данный момент нет активного персонала на сервере, придется подождать, пока кто-то увидит!");
    }
}
