package net.plazmix.rewards.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.rewards.inventory.RewardsSelectTypeInventory;

public final class RewardsCommand extends CommandExecutor {

    public RewardsCommand() {
        super("passrewards", "dailyrewards", "rewards");

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new RewardsSelectTypeInventory().openInventory((CorePlayer) commandSender);
    }
}
