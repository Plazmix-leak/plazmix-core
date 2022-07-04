package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.Collection;

public class OnlineStaffCommand extends VkCommand {

    public OnlineStaffCommand() {
        super("стафф", "персонал", "staff");

        setMinimalGroup(Group.DEFAULT);

        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Список онлайн администрации:\n");

        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.JR_MODER), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.MODER), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.SR_MODER), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.STAFF), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.ADMIN), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.DEVELOPER), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.SR_DEVELOPER), stringBuilder);
        buildStaffString(PlazmixCore.getInstance().getOfflinePlayersByGroup(Group.OWNER), stringBuilder);

        if (stringBuilder.toString().length() <= 31) {
            stringBuilder.append("\nОнлайн персонал не найден");
        }

        vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
    }

    private void buildStaffString(Collection<CorePlayer> playersCollection, StringBuilder stringBuilder) {
        playersCollection.forEach(corePlayer -> {
            if (corePlayer.isOnline()) {
                stringBuilder.append("\n \uD83D\uDC8E Статус: " + corePlayer.getGroup().name() + " "  + corePlayer.getName() + "");
                stringBuilder.append("\n \uD83E\uDDC0 Сервер: " + (corePlayer.getBukkitServer() == null ? "<Не инициализировано>" : corePlayer.getBukkitServer().getName()));
            }
        });
    }
}
