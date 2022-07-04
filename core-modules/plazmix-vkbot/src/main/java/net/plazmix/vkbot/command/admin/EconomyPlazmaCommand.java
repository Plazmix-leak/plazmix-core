package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class EconomyPlazmaCommand extends VkCommand {

    public EconomyPlazmaCommand() {
        super("plazma", "плазма", "плазмы");

        setMinimalGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте - !плазма <ник> <кол-во>");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);

        if (corePlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не существует!");
            return;
        }

        corePlayer.addPlazma(Integer.parseInt(args[1]));
        vkBot.printMessage(message.getPeerId(), "Игроку " + corePlayer.getName() + " было успешно добавлено " + Integer.parseInt(args[1]) + " плазмы");
    }

}
