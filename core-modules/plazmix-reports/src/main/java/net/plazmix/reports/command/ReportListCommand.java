package net.plazmix.reports.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.reports.inventory.ReportInventory;

public class ReportListCommand extends CommandExecutor {

    public ReportListCommand() {
        super("reports", "reportlist", "жалобы", "rs");

        setMinimalGroup(Group.MODER);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new ReportInventory().openInventory((CorePlayer)commandSender);
    }

}
