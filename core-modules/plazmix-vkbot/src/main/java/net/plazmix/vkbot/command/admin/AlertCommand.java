package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.sender.ConsoleCommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AlertCommand extends VkCommand {

    public AlertCommand() {
        super("alert", "объявить", "объявление");

        setMinimalGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте !alert <сообщение>");
            return;
        }

        String alertMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));
        PlazmixCore.getInstance().getCommandManager().dispatchCommand(ConsoleCommandSender.getInstance(), "alert " + alertMessage);

        vkBot.printMessage(message.getPeerId(), "❗ На весь сервер было отправлено следующее сообщение:\n" +
                " Plazmix » " + ChatColor.stripColor(alertMessage));
    }

}
