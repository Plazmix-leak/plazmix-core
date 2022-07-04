package net.plazmix.myserver.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.myserver.inventory.ServerManagementInventory;
import net.plazmix.myserver.type.MyServerCategory;
import net.plazmix.myserver.type.MyServerManager;
import net.plazmix.myserver.type.MyServerType;
import net.plazmix.myserver.type.PlayerMyServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class MyServerCommand extends CommandExecutor {

    public MyServerCommand() {
        super("myserver", "ms");

        setMinimalGroup(Group.LUXURY);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);
        PlayerMyServer playerMyServer = MyServerManager.INSTANCE.getPlayerServer(corePlayer);

        if (args.length == 0) {

            if (playerMyServer != null) {
                new ServerManagementInventory(playerMyServer).openInventory(corePlayer);

            } else {

                sendHelpMessage(commandSender);
            }
            return;
        }

        switch (args[0].toLowerCase()) {

            case "create": {
                if (playerMyServer != null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы уже создали сервер ранее!");
                    break;
                }

                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/myserver create <индекс/префикс сервера>");

                    break;
                }

                MyServerType myServerType;

                if (ValidateUtil.isNumber(args[1])) myServerType = MyServerType.of(Integer.parseInt(args[1]));
                else myServerType = MyServerType.of(args[1]);

                if (myServerType == null) {

                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, неверно указана категория сервера!");
                    commandSender.sendMessage("§d§lPlazmix §8:: §fЧтобы узнать, какие категории доступны, то пишите /myserver list");
                    break;
                }

                commandSender.sendMessage("§d§lMyServer §8:: §fНачалось создание сервера...");
                MyServerManager.INSTANCE.createServer(corePlayer, myServerType);
                break;
            }

            case "list": {
                commandSender.sendMessage("§d§lMyServer §8:: §fСписок доступных серверов:");

                for (MyServerCategory serverCategory : MyServerCategory.SERVER_CATEGORIES) {
                    commandSender.sendMessage("§e" + serverCategory.getName() + " (ID: " + serverCategory.getTypeIndex() + ")");

                    for (MyServerType serverType : serverCategory.getServerTypes()) {
                        commandSender.sendMessage(" §7" + serverType.getSubTypeIndex() + ". §6" + serverType.getSubName() + " §f(" + serverType.name().toLowerCase() + ")");

                        if (Arrays.stream(Objects.requireNonNull(serverType.getServersFolder().toFile().listFiles())).anyMatch(serverFolder -> !MyServerManager.INSTANCE.isAvailable(serverFolder.toPath()))) {
                            commandSender.sendMessage("  §8- §7Сейчас запущено §e" + MyServerManager.INSTANCE.getActiveServers(serverType).size() + " §7серверов");

                        } else {

                            commandSender.sendMessage("  §8- §cНет доступных серверов");
                        }
                    }
                }

                break;
            }

            case "stats": {
                commandSender.sendMessage("§d§lMyServer §8:: §fСтатистика серверов:");

                Collection<BukkitServer> connectedServers = PlazmixCore.getInstance().getServersByPrefix("ms-");
                Collection<PlayerMyServer> activeServers = MyServerManager.INSTANCE.getActiveServers();

                // Connected servers to Core.
                commandSender.sendMessage("§d§lMyServer §8:: §f[Соединение с игровым координатором]: §7(" + connectedServers.size() + ")");

                if (!connectedServers.isEmpty()) {
                    JsonChatMessage jsonChatMessage = JsonChatMessage.create().addText("  ");

                    int serverCounter = 1;
                    for (BukkitServer bukkitServer : connectedServers) {
                        JsonChatMessage serverJson = JsonChatMessage.create();

                        serverJson.addText(ChatColor.YELLOW + bukkitServer.getName());
                        serverJson.addHover(HoverEvent.Action.SHOW_TEXT,
                                "§7Название сервера: §e" + bukkitServer.getName() + "\n" +
                                        "§7Онлайн: §e" + bukkitServer.getOnlineCount() + "\n" +
                                        "§7Версия ядра: §b" + bukkitServer.getMinecraftVersionName());

                        jsonChatMessage.addComponents(serverJson.build());

                        if (serverCounter < activeServers.size())
                            jsonChatMessage.addText("§f, ");

                        serverCounter++;
                    }

                    commandSender.sendMessage(ChatMessageType.CHAT, jsonChatMessage.build());

                } else {

                    commandSender.sendMessage("  §cСписок серверов пуст!");
                }


                // Active system servers
                commandSender.sendMessage(" §f[Активные сервера]: §7(" + activeServers.size() + ")");

                if (!activeServers.isEmpty()) {
                    JsonChatMessage jsonChatMessage = JsonChatMessage.create().addText("  ");

                    int serverCounter = 1;
                    for (PlayerMyServer myServer : activeServers) {
                        JsonChatMessage serverJson = JsonChatMessage.create();

                        serverJson.addText((myServer.isRunning() ? ChatColor.GREEN : ChatColor.RED) + myServer.getServerName());
                        serverJson.addHover(HoverEvent.Action.SHOW_TEXT,
                                "§7Название: §e" + myServer.getServerName() + "\n" +
                                        "§7Создатель: " + myServer.getOwner().getDisplayName() + "\n" +
                                        "§7Тип: §e" + myServer.getServerType().name().toLowerCase() + "\n" +
                                        (myServer.isRunning() ? "§aСервер запущен и активен!" : "§cСервер отключен!"));

                        jsonChatMessage.addComponents(serverJson.build());

                        if (serverCounter < activeServers.size())
                            jsonChatMessage.addText("§f, ");

                        serverCounter++;
                    }

                    commandSender.sendMessage(ChatMessageType.CHAT, jsonChatMessage.build());

                } else {

                    commandSender.sendMessage("  §cСписок серверов пуст!");
                }

                break;
            }

            default: {
                sendHelpMessage(commandSender);

                break;
            }
        }

    }

    private void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage("§d§lMyServer §8:: §fСписок доступных команд:");
        commandSender.sendMessage(" §7Создать свой сервер - §e/myserver create <индекс/префикс категории>");
        commandSender.sendMessage(" §7Статистика серверов - §e/myserver stats");
        commandSender.sendMessage(" §7Список доступных серверов - §e/myserver list");
    }

}
