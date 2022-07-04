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

@Log4j2
public class AlertCommand extends CommandExecutor {

    public AlertCommand() {
        super( "alert", "объявить", "объявление", "notice");

        setMinimalGroup(Group.ADMIN);

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendLangMessage("ALERT_HELP");
            return;
        }

        alert(ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args)));
    }

    public static void alert(@NonNull String message) {
        message = ("§d§lPlazmix §8➥ §f") + message;

        for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {
            corePlayer.sendMessage(message);
        }

        log.info(message);
    }

}
