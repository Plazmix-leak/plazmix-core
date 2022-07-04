package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class OnlineCommand extends VkCommand {

    public OnlineCommand() {
        super("онлайн", "online");
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Общий онлайн сервера - " + NumberUtil.spaced(PlazmixCore.getInstance().getGlobalOnline()));

            return;
        }

        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();

        if (args[0].startsWith("@")) {
            String serverPrefix = args[0].substring(1);

            vkBot.printMessage(message.getPeerId(), "Онлайн серверов по префиксу " + serverPrefix.toUpperCase() + " - " + NumberUtil.spaced(PlazmixCore.getInstance().getOnlineByServerPrefix(serverPrefix)));
            return;
        }

        AbstractServer abstractServer = serverManager.getBukkit(args[0]);

        if (abstractServer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный сервер не существует или не подключен к Core!");
            return;
        }

        vkBot.printMessage(message.getPeerId(), "Онлайн сервера " + abstractServer.getName() + " - " + NumberUtil.spaced(abstractServer.getOnlineCount()));
    }
}
