package net.plazmix.economy.command;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;

public class BalanceCommand extends CommandExecutor {

    public BalanceCommand() {
        super("balance", "bal", "money");

        setOnlyAuthorized(true);
        setCanUseLoginServer(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        corePlayer.sendLangMessage("MONEY_MESSAGE", "%money%", ChatColor.GREEN + NumberUtil.formatting(corePlayer.getCoins(), "монета", "монеты", "монет"),
                "%plazma%", ChatColor.LIGHT_PURPLE + NumberUtil.formatting(corePlayer.getPlazma(), "плазма", "плазмы", "плазмы"));
    }

}
