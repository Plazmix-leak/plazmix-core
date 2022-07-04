package net.plazmix.core.api.module.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.utility.DateUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

public class ModuleCommand extends CommandExecutor {

    public ModuleCommand() {
        super("module", "modules", "модуль", "модули");

        setOnlyAuthorized(true);
        setCanUseLoginServer(false);

        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length == 0) {
            sendHelpMessage(commandSender);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "enable": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §fИспользуйте - §d/module enable <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (coreModule.isEnabled()) {
                    commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль уже запущен!");
                    break;
                }

                coreModule.enableModule();
                commandSender.sendMessage("§d§lCore §8:: §fМодуль §e" + coreModule.getName() + " §fуспешно запущен!");
                break;
            }

            case "disable": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §cОшибка, пишите - /module disable <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (!coreModule.isEnabled()) {
                    commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль уже выключен!");
                    break;
                }

                coreModule.disableModule();
                commandSender.sendMessage("§d§lCore §8:: §fМодуль §e" + coreModule.getName() + " §fуспешно выключен!");
                break;
            }

            case "reload": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §fИспользуйте - §d/module reload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {

                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.reloadModule();
                        }

                        commandSender.sendMessage("§d§lCore §8:: §fВсе доступные модули кора были успешно перезагружены!");
                        break;
                    }

                    default: {
                        CoreModule coreModule
                                = PlazmixCore.getInstance().getModuleManager().getModule(args[1]);

                        if (coreModule == null) {
                            commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        coreModule.reloadModule();
                        commandSender.sendMessage("§d§lCore §8:: §fМодуль §e" + coreModule.getName() + " §fбыл перезагружен!");
                    }
                }

                break;
            }

            case "load": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §fИспользуйте - §d/module load <имя файла/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {
                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        commandSender.sendMessage("§d§lCore §8:: §fВсе доступные модули кора были отправлены на загрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.unloadModule();
                        }

                        PlazmixCore.getInstance().getModuleManager().loadModules(PlazmixCore.getInstance().getModulesFolder());
                        break;
                    }

                    default: {
                        String fileName = args[1].concat(".jar");
                        File moduleFile = new File(PlazmixCore.getInstance().getModulesFolder(), fileName);

                        if (!Files.exists(moduleFile.toPath())) {
                            commandSender.sendMessage("§d§lCore §8:: §cОшибка, файла " + fileName + " не существует в директории модулей!");
                            break;
                        }

                        PlazmixCore.getInstance().getModuleManager()
                                .loadModuleFile(moduleFile);

                        commandSender.sendMessage("§d§lCore §8:: §fJAR модуль " + fileName + " §fбыл успешно найден и загружен!");
                    }
                }

                break;
            }

            case "unload": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §fИспользуйте - §d/module unload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {

                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        commandSender.sendMessage("§d§lCore §8:: §fВсе доступные модули кора отправлены на выгрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.unloadModule();
                        }

                        break;
                    }

                    default: {
                        CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                                .getModule(args[1]);

                        if (coreModule == null) {
                            commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        coreModule.unloadModule();
                        commandSender.sendMessage("§d§lCore §8:: §fМодуль " + coreModule.getName() + " §fбыл отключен и выгружен нахуй)))");
                    }
                }

                break;
            }

            case "info": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lCore §8:: §fИспользуйте - §d/module info <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    commandSender.sendMessage("§cОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                commandSender.sendMessage("§d§lCore §8:: §fИнформация о модуле §e" + coreModule.getName() + "§f:");
                commandSender.sendMessage(" §7Автор: §e" + coreModule.getAuthor());
                commandSender.sendMessage(" §7Зависимости: §e" + (coreModule.getDepends().length == 0 ? "§cнет" : Joiner.on("§f, §a").join(coreModule.getDepends())));
                commandSender.sendMessage(" §7Был запущен: §e" + DateUtil.formatTime(coreModule.getEnableMillis(), DateUtil.DEFAULT_DATETIME_PATTERN) + " §7(" + NumberUtil.getTime(System.currentTimeMillis() - coreModule.getEnableMillis()) + " назад)");
                commandSender.sendMessage(" §7Статус: §" + (coreModule.isEnabled() ? "aвключен" : "cвыключен"));
                break;
            }

            case "list": {
                Collection<CoreModule> coreModuleCollection
                        = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                StringBuilder stringBuilder = new StringBuilder();
                for (CoreModule coreModule : coreModuleCollection) {

                    stringBuilder.append(coreModule.isEnabled() ? ChatColor.GREEN : ChatColor.RED)
                            .append(coreModule.getName()).append("§f, ");
                }

                commandSender.sendMessage("§d§lCore §8:: §fСписок модулей кора (" + coreModuleCollection.size() + "): §e" + stringBuilder.substring(0, stringBuilder.toString().length() - 4));
                break;
            }

            default:
                commandSender.sendMessage("§d§lCore §8:: §cОшибка, данный аргумент не существует!");
                sendHelpMessage(commandSender);
        }
    }

    protected void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage("§d§lCore §8:: §fСписок доступных команд:");

        commandSender.sendMessage(" §7Загрузить модуль - §e/module load <имя файла/*>");
        commandSender.sendMessage(" §7Отключить и выгрузить модуль - §e/module unload <название/*>");
        commandSender.sendMessage(" §7Перезагрузить модуль - §e/module reload <название/*>");

        commandSender.sendMessage(" §7Включить модуль - §e/module enable <название>");
        commandSender.sendMessage(" §7Отключить модуль - §e/module disable <название>");

        commandSender.sendMessage(" §7Информация о модуле - §e/module info <название>");
        commandSender.sendMessage(" §7Показать список модулей - §e/module list");
    }

}
