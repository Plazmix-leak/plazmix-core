package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;

public class OnlineCommand extends CommandExecutor {

    public OnlineCommand() {
        super("online", "list");
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendLangMessage("GLOBAL_ONLINE", "%online%", NumberUtil.spaced(PlazmixCore.getInstance().getGlobalOnline()));

            return;
        }

        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();

        if (args[0].startsWith("@")) {
            String serverPrefix = args[0].substring(1);

            commandSender.sendLangMessage("ONLINE_BY_PREFIX",
                    "%prefix%",serverPrefix.toUpperCase(), "%online%", NumberUtil.spaced(PlazmixCore.getInstance().getGlobalOnline()));

            return;
        }

        AbstractServer abstractServer = serverManager.getBukkit(args[0]);

        if (abstractServer == null) {
            commandSender.sendLangMessage("SERVER_NOT_FOUND");
            return;
        }

        commandSender.sendLangMessage("ONLINE_BY_SERVER",
                "%prefix%", abstractServer.getName(), "%online%", NumberUtil.spaced(abstractServer.getOnlineCount()));
    }

}
