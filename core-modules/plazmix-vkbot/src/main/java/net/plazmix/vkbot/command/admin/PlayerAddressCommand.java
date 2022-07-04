package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class PlayerAddressCommand extends VkCommand {

    public PlayerAddressCommand() {
        super("ip", "ип", "адрес", "address");

        setMinimalGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используйте !адрес <ник>");
            return;
        }

        int playerId = NetworkManager.INSTANCE.getPlayerId(args[0]);
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(playerId);

        if (corePlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не в сети!");
            return;
        }

        vkBot.printAndDeleteMessage(message.getPeerId(),
                "❗ Информация об аккаунте: " + corePlayer.getGroup().name() + " " + corePlayer.getName() +
                "\n IP: " + corePlayer.getInetSocketAddress().getHostString() +
                "\n UUID: " + corePlayer.getUniqueId());
    }
}