package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.ModuleManager;
import net.plazmix.core.api.utility.DateUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.language.LanguageManager;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.common.punishment.PunishmentType;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SLanguagesReloadPacket;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.Protocol;

import java.net.InetSocketAddress;
import java.util.Collection;

public class CoreCommand extends CommandExecutor {

    public CoreCommand() {
        super("core", "pcore", "plazmixcore");

        setMinimalGroup(Group.ADMIN);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();

        if (args.length == 0) {
            commandSender.sendMessage("§d§lCore §8:: §fИнформация о подключениях:");

            commandSender.sendMessage(" §f[Сервера]:");
            commandSender.sendMessage(" §8▪ §7Bungee: §b" + serverManager.getBungeeServers().size());
            commandSender.sendMessage(" §8▪ §7Bukkit: §b" + serverManager.getBukkitServers().size());

            commandSender.sendMessage(" §f[Игроки]:");
            commandSender.sendMessage(" §8▪ §7Подключено: §b" + PlazmixCore.getInstance().getGlobalOnline());

            commandSender.sendMessage(" §8▪ §7Авторизировано: §b" + AuthManager.INSTANCE.getAuthPlayerMap().size());
            commandSender.sendMessage(" §8▪ §7Забанено: §b" + PunishmentManager.INSTANCE.getPunishmentMap().values().stream()
                    .filter(punishment -> punishment.isPermanent() && punishment.getPunishmentType().equals(PunishmentType.PERMANENT_BAN)).count());

            ModuleManager moduleManager = PlazmixCore.getInstance().getModuleManager();

            commandSender.sendMessage(" §f[Модули]: ");
            commandSender.sendMessage(" §8▪ §7Всего подключено: §b" + moduleManager.getModuleMap().size());
            commandSender.sendMessage(" §8▪ §7Активно: §b" + moduleManager.getModuleMap().values().stream().filter(CoreModule::isEnabled).count());


            commandSender.sendMessage(" §f[Protocol]:");
            commandSender.sendMessage(" §8▪ §7Client: §b" + Protocol.PLAY.TO_CLIENT.getIdToPackets().size());
            commandSender.sendMessage(" §8▪ §7Server: §b" + Protocol.PLAY.TO_SERVER.getIdToPackets().size());
            commandSender.sendMessage(" §8▪ §7Всего: §b" + (Protocol.PLAY.TO_SERVER.getIdToPackets().size() + Protocol.PLAY.TO_CLIENT.getIdToPackets().size()));

            commandSender.sendMessage(" §7Система была запущена §f(" + DateUtil.formatTime(PlazmixCore.getInstance().getStartSessionMillis(), DateUtil.DEFAULT_DATE_PATTERN) + ") §b" + NumberUtil.getTime(PlazmixCore.getInstance().getSessionMillis()) + " §7назад");
            return;
        }

        switch (args[0].toLowerCase()) {

            case "langreload": {
                LanguageManager languageManager = LanguageManager.INSTANCE;

                for (LanguageType languageType : LanguageType.VALUES = LanguageType.values())
                    languageType.getResource().initResources();

                // Хз, скорее всего языковые ключи не обновляется у самих игроков
                for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {

                    languageManager.getPlayerLanguageMap().remove(corePlayer.getPlayerId());
                    corePlayer.setLanguageType(languageManager.getPlayerLanguage(corePlayer));
                }

                // Ну и отправляем пакеты о перезагрузке языков на все сервера
                commandSender.sendMessage("§d§lCore §8:: §fЛокализация была успешно перезагружена!");
                PlazmixCore.getInstance().broadcastPacket(new SLanguagesReloadPacket());
                break;
            }

            case "help": {
                commandSender.sendMessage("§d§lCore §8:: §fСписок доступных команд:");
                commandSender.sendMessage(" §7Информация о подключенном сервере - §b/core <название сервера>");
                commandSender.sendMessage(" §7Перезагрузить конфиги локализации - §b/core langreload");
                break;
            }

            case "restart": {
                if (args[1].equals("*")) {

                    for (AbstractServer abstractServer : serverManager.getBukkitServers().values()) {
                        abstractServer.restart();
                    }

                    for (AbstractServer abstractServer : serverManager.getBungeeServers().values()) {
                        abstractServer.restart();
                    }

                    commandSender.sendMessage("§d§lCore §8:: §fВсе подключенные сервера были отправлены на перезагрузку!");
                    break;
                }

                if (args[1].startsWith("@") && args[1].length() > 1) {

                    Collection<BukkitServer> servers = PlazmixCore.getInstance().getServersByPrefix(args[1].substring(1));
                    for (BukkitServer bukkitServer : servers) {

                        bukkitServer.restart();
                    }

                    commandSender.sendMessage("§d§lCore §8:: §fНа перезагрузку было отправлено §b" + servers.size() + " §fсерверов с префиксом §b" + args[1].toUpperCase());
                    break;
                }

                AbstractServer abstractServer = serverManager.getBukkit(args[1]);

                if (abstractServer == null) {
                    abstractServer = serverManager.getBungee(args[1]);

                    if (abstractServer == null) {
                        commandSender.sendLangMessage("SERVER_NOT_FOUND");
                        return;
                    }
                }

                commandSender.sendMessage("§d§lCore §8:: §fСервер §b" + abstractServer.getName() + " §fуспешно отправлен на перезагрузку");
                abstractServer.restart();
                return;
            }

            default:
                AbstractServer abstractServer = serverManager.getBukkit(args[0]);

                if (abstractServer == null) {
                    abstractServer = serverManager.getBungee(args[0]);

                    if (abstractServer == null) {
                        commandSender.sendLangMessage("SERVER_NOT_FOUND");
                        return;
                    }
                }

                commandSender.sendMessage("§d§lCore §8:: §fИнформация о сервере:");
                commandSender.sendMessage(" §7Название: §b" + abstractServer.getName());
                commandSender.sendMessage(" §7Motd: §f" + abstractServer.getMotd());
                commandSender.sendMessage(" §7Директория: §b" + (abstractServer.isBungee() ? "Bungee" : "Bukkit"));
                commandSender.sendMessage(" §7Версия ядра: §b" + abstractServer.getMinecraftVersionName());
                commandSender.sendMessage(" §7Количество игроков: §e" + abstractServer.getOnlineCount());

                InetSocketAddress inetSocketAddress = abstractServer.getInetSocketAddress();
                commandSender.sendMessage(" §7IP: §f" + inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort());

                // Создаем кнопки
                BaseComponent[] restartButton = JsonChatMessage.create("§c§l[ Рестарт ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §cперезагрузить сервер")
                        .addClick(ClickEvent.Action.RUN_COMMAND, "/core restart " + abstractServer.getName())
                        .build();

                BaseComponent[] tpButton = JsonChatMessage.create("§a§l[ Телепорт ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §aтелепортироваться на сервер")
                        .addClick(ClickEvent.Action.RUN_COMMAND, "/server " + abstractServer.getName())
                        .build();

                JsonChatMessage.create()

                        .addText("\n")
                        .addComponents(tpButton)

                        .addText("        ")
                        .addComponents(restartButton)

                        .sendMessage(commandSender);
        }
    }
}
