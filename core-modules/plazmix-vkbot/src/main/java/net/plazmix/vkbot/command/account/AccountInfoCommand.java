package net.plazmix.vkbot.command.account;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class AccountInfoCommand extends VkCommand {

    public AccountInfoCommand() {
        super("инфо", "информация", "аккаунт", "акк", "info");

        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            printPlayerInfo(botUser.getPrimaryAccountName(), message, vkBot);
            return;
        }

        Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(botUser.getPrimaryAccountName());

        if (!playerGroup.isStaff()) {
            vkBot.printMessage(message.getPeerId(), "❗ У Вас недостаточно прав для получения информации о другом игроке!");
            return;
        }

        printPlayerInfo(args[0], message, vkBot);
    }

    private void printPlayerInfo(@NonNull String playerName,
                                 @NonNull Message message,
                                 @NonNull VkBot vkBot) {

        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        if (playerId < 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Игрок " + playerName + " ранее не играл на нашем сервере");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);
        StringBuilder playerInfo = new StringBuilder();

        // Basic information.
        playerInfo.append("Основное:");
        playerInfo.append("\n \uD83D\uDD11 Уникальный ID: ").append(corePlayer.getPlayerId());
        playerInfo.append("\n \uD83C\uDFAE Никнейм: ").append(corePlayer.getName());
        playerInfo.append("\n \uD83D\uDC8E Статус: ").append(corePlayer.getGroup().getName());

        // Level information.
        int level       = corePlayer.getLevel();
        int exp         = corePlayer.getExperience();
        int maxExp      = corePlayer.getMaxExperience();
        int percent     = NumberUtil.getIntPercent(exp, maxExp);


        playerInfo.append("\n\nИгровой уровень:");
        playerInfo.append("\n \uD83D\uDD38 Уровень: " + NumberUtil.spaced(level) + " LVL");
        playerInfo.append("\n \uD83D\uDD38 Опыт: " + NumberUtil.spaced(exp) + " EXP из " + NumberUtil.spaced(maxExp) + " EXP");

        playerInfo.append("\n\n  Собрано " + percent + "% опыта до следующего уровня,");
        playerInfo.append("\n  До " + (level + 1) + " уровня необходимо еще " + NumberUtil.spaced(maxExp - exp) + " EXP (" + (100 - percent) + "%)");


        // Status information.
        playerInfo.append("\n\nДополнительно:");

        if (corePlayer.isOnline()) {

            playerInfo.append("\n ✅ Online (в сети)");
            playerInfo.append("\n \uD83E\uDDC0 Сервер: ").append(corePlayer.getBukkitServer() == null ? "<Не инициализировано>" : corePlayer.getBukkitServer().getName().toLowerCase());

        } else {

            playerInfo.append("\n \uD83D\uDED1 Offline (не в сети)");
            playerInfo.append("\n \uD83D\uDCAC Последний сервер: ").append(corePlayer.getPlayerOfflineData().getLastServerName().toLowerCase());
            playerInfo.append("\n \uD83D\uDCCC Последний вход: ").append(NumberUtil.getTime(System.currentTimeMillis() - corePlayer.getPlayerOfflineData().getLastOnline().getTime())).append(" назад");
        }

        new Message()
                .peerId(message.getPeerId())
                .forwardedMessages(message)

                .body(playerInfo.toString())
                .send(vkBot);
    }

}
