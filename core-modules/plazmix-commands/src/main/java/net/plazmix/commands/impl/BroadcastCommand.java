package net.plazmix.commands.impl;

import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import org.apache.logging.log4j.core.Core;

@Log4j2
public class BroadcastCommand extends CommandExecutor {

    public BroadcastCommand() {
        super( "broadcast", "броадкаст", "bc");

        setMinimalGroup(Group.COSMO);

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendLangMessage("BROADCAST_HELP");
            return;
        }

        if (corePlayer.getGroup().getLevel() <= Group.SPECIAL.getLevel()) {
            if (corePlayer.getCoins() < 2500) {
                corePlayer.sendLangMessage("NO_ENOUGH_MONEY");
                return;
            }

            corePlayer.takeCoins(2500);
        }

        alert(ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args)), commandSender.getName());
    }

    public static void alert(@NonNull String message, String playername) {
        CorePlayer player = PlazmixCore.getInstance().getPlayer(playername);

        message = ("§c§lBROADCAST §8➥ §f") + message;

        for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {
            corePlayer.sendMessage(message);
            corePlayer.sendMessage(" §7Было отправлено игроком: " + player.getDisplayName());
        }

        log.info(message);
    }

}
