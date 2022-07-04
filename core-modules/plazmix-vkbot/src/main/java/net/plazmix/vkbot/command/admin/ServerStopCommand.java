package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class ServerStopCommand extends VkCommand {

    public ServerStopCommand() {
        super("serverstop", "stopserver", "стоп", "stop");

        setMinimalGroup(Group.ADMIN);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте - !стоп <имя сервера>");
            return;
        }

        AbstractServer abstractServer = PlazmixCore.getInstance().getBukkitServer(args[0]);

        if (abstractServer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный сервер не существует или не подключен к Core!");
            return;
        }
        vkBot.printMessage(message.getPeerId(), "Сервер " + abstractServer.getName() + " был отправлен нахуй!");
    }

}
