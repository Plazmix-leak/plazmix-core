package net.plazmix.vkbot.command.admin;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.ModuleManager;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.core.protocol.Protocol;
import net.plazmix.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import net.plazmix.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

public class ServerInfoCommand extends VkCommand {

    public ServerInfoCommand() {
        super("сервера", "serverinfo", "core", "plazmix", "pcore", "кор");

        setMinimalGroup(Group.ADMIN);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();

        if (args.length == 0) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" [Сервера]:").append("\n");
            stringBuilder.append(" ▪ Bungee: ").append(serverManager.getBungeeServers().size()).append("\n");
            stringBuilder.append(" ▪ Bukkit: ").append(serverManager.getBukkitServers().size()).append("\n");

            stringBuilder.append(" [Игроки]:").append("\n");
            stringBuilder.append(" ▪ Подключено: ").append(PlazmixCore.getInstance().getGlobalOnline()).append("\n");
            stringBuilder.append(" ▪ Авторизировано: ").append(AuthManager.INSTANCE.getAuthPlayerMap().size()).append("\n");
            stringBuilder.append(" ▪ Забанено: ").append(PunishmentManager.INSTANCE.getPunishmentMap().values().stream()
                    .filter(punishment -> punishment.isPermanent() && punishment.getPunishmentType().equals(PunishmentType.PERMANENT_BAN)).count()).append("\n");

            ModuleManager moduleManager = PlazmixCore.getInstance().getModuleManager();

            stringBuilder.append(" [Модули]:").append("\n");
            stringBuilder.append(" ▪ Всего подключено: ").append(moduleManager.getModuleMap().size()).append("\n");
            stringBuilder.append(" ▪ Активно: ").append(moduleManager.getModuleMap().values().stream().filter(CoreModule::isEnabled).count()).append("\n");
            stringBuilder.append(" ▪ Команд зарегистрировано: ").append(PlazmixCore.getInstance().getCommandManager().getCommandMap().size()).append("\n");

            stringBuilder.append(" [Protocol]:").append("\n");
            stringBuilder.append(" ▪ TO_CLIENT: ").append(Protocol.PLAY.TO_CLIENT.getIdToPackets().size()).append("\n");
            stringBuilder.append(" ▪ TO_SERVER: ").append(Protocol.PLAY.TO_SERVER.getIdToPackets().size()).append("\n");
            stringBuilder.append(" ▪ Всего: ").append(Protocol.PLAY.TO_SERVER.getIdToPackets().size() + Protocol.PLAY.TO_CLIENT.getIdToPackets().size()).append("\n");

            stringBuilder.append(" [System]:").append("\n");
            stringBuilder.append(" ▪ Система была запущена ").append(NumberUtil.getTime(PlazmixCore.getInstance().getSessionMillis())).append(" назад");

            vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
            return;
        }

        AbstractServer abstractServer = serverManager.getBukkit(args[0]);

        if (abstractServer == null) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, данный сервер не существует или не подключен к Core!");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("❗ Информация о сервере:").append("\n");
        stringBuilder.append("* Название: " + abstractServer.getName()).append(":").append("\n");
        stringBuilder.append("* MOTD: " + abstractServer.getMotd()).append(":").append("\n");
        stringBuilder.append("* Директория: ").append(abstractServer.isBungee() ? "Bungee" : "Bukkit").append("\n");
        stringBuilder.append("* Версия ядра: " + abstractServer.getMinecraftVersionName()).append(":").append("\n");
        stringBuilder.append("* Количество игроков: ").append(abstractServer.getOnlineCount()).append("\n");

        new Message()
                .peerId(message.getPeerId())
                .body(stringBuilder.toString())

                .keyboard(true, true)
                .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("!рестарт " + abstractServer.getName(), "Перезагрузить"))
                .message()

                .send(vkBot);
    }
}
