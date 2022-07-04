package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteCommand extends VkCommand {

    public TabCompleteCommand() {
        super("tab", "таб");
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, вы не указали префикс поиска");
            return;
        }

        List<CorePlayer> corePlayerList = PlazmixCore.getInstance().getOnlinePlayers().stream()
                .filter(corePlayer -> corePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());

        if (corePlayerList.size() == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, игроки по префиксу " + args[0] + " не найдены");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (CorePlayer corePlayer : corePlayerList) {

            stringBuilder.append(corePlayer.getName())
                    .append(corePlayerList.get(corePlayerList.size() - 1).equals(corePlayer) ? " " : ", ");
        }

        vkBot.printMessage(message.getPeerId(), "По вашему запросу найдено " + corePlayerList.size() + " игроков:\n" + stringBuilder.toString());
    }
}
