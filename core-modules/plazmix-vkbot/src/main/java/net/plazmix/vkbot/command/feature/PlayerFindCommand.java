package net.plazmix.vkbot.command.feature;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class PlayerFindCommand extends VkCommand {

    public PlayerFindCommand() {
        super("find", "search", "найти");
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, пишите - /find <игрок>");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(args[0]);

        if (corePlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не в сети!");
            return;
        }

        vkBot.printMessage(message.getPeerId(), ChatColor.stripColor(corePlayer.getDisplayName()) + " находится на сервере " + corePlayer.getBukkitServer().getName());
    }

}
