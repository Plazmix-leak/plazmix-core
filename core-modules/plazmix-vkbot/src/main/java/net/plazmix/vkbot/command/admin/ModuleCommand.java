package net.plazmix.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Collection;

public class ModuleCommand extends VkCommand {

    public ModuleCommand() {
        super("module", "модуль");

        setMinimalGroup(Group.ADMIN);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        if (args.length == 0) {
            sendHelpMessage(vkBot, message);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "enable": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module enable <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (coreModule.isEnabled()) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль уже запущен!");
                    break;
                }

                coreModule.enableModule();
                vkBot.printMessage(message.getPeerId(), "Модуль " + coreModule.getName() + " успешно запущен!");
                break;
            }

            case "disable": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module disable <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (!coreModule.isEnabled()) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль уже выключен!");
                    break;
                }

                coreModule.disableModule();
                vkBot.printMessage(message.getPeerId(), "Модуль " + coreModule.getName() + " успешно выключен!");
                break;
            }

            case "reload": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module reload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {

                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        vkBot.printMessage(message.getPeerId(), "Все доступные модули кора были отправлены на перезагрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {

                            if (!coreModule.isEnabled()) {
                                coreModule.loadModule();

                                continue;
                            }

                            coreModule.reloadModule();
                        }

                        break;
                    }

                    default: {
                        CoreModule coreModule
                                = PlazmixCore.getInstance().getModuleManager().getModule(args[1]);

                        if (coreModule == null) {
                            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        vkBot.printMessage(message.getPeerId(), "Модуль " + coreModule.getName() + " отправлен на перезагрузку!");

                        if (!coreModule.isEnabled()) {
                            coreModule.loadModule();

                            break;
                        }

                        coreModule.reloadModule();
                    }
                }

                break;
            }

            case "load": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module load <имя файла/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {

                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        vkBot.printMessage(message.getPeerId(), "Все доступные модули кора были отправлены на загрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.disableModule();
                        }

                        PlazmixCore.getInstance().getModuleManager().loadModules(
                                PlazmixCore.getInstance().getModulesFolder()
                        );

                        break;
                    }

                    default: {
                        String fileName = args[1].concat(".jar");
                        File moduleFile = new File(PlazmixCore.getInstance().getModulesFolder(), fileName);

                        if (!Files.exists(moduleFile.toPath())) {
                            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, файла " + fileName + " не существует в директории модулей!");
                            break;
                        }

                        vkBot.printMessage(message.getPeerId(), "JAR модуль " + fileName + " был успешно найден и загружен!");

                        PlazmixCore.getInstance().getModuleManager()
                                .loadModuleFile(moduleFile);
                    }
                }

                break;
            }

            case "unload": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module unload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {

                    case "*": {
                        Collection<CoreModule> coreModuleCollection
                                = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                        vkBot.printMessage(message.getPeerId(), "Все доступные модули кора отправлены на отключение и выгрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.unloadModule();
                        }

                        break;
                    }

                    default: {
                        CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                                .getModule(args[1]);

                        if (coreModule == null) {
                            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        vkBot.printMessage(message.getPeerId(), "Модуль " + coreModule.getName() + " был отключен и выгружен!");
                        coreModule.unloadModule();
                    }
                }

                break;
            }

            case "info": {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module info <название>");
                    break;
                }

                CoreModule coreModule = PlazmixCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                StringBuilder stringBuilder = new StringBuilder();
                {
                    stringBuilder.append("Информация о модуле " + coreModule.getName() + ":");
                    stringBuilder.append("\n Автор: " + coreModule.getAuthor());
                    stringBuilder.append("\n Депенды: " + (coreModule.getDepends().length == 0 ? "Пусто" : Joiner.on(" ").join(coreModule.getDepends())));
                    stringBuilder.append("\n Был запущен: " + new Timestamp(coreModule.getEnableMillis()).toGMTString() + " (" + NumberUtil.getTime(System.currentTimeMillis() - coreModule.getEnableMillis()) + " назад)");
                    stringBuilder.append("\n Статус: " + (coreModule.isEnabled() ? "включен" : "выключен"));
                }

                vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
                break;
            }

            case "list": {
                Collection<CoreModule> coreModuleCollection
                        = PlazmixCore.getInstance().getModuleManager().getModuleMap().values();

                StringBuilder stringBuilder = new StringBuilder();
                for (CoreModule coreModule : coreModuleCollection) {

                    stringBuilder
                            .append("\n")
                            .append(coreModule.getName())
                            .append(coreModule.isEnabled() ? " [ON]" : " [OFF]");
                }

                vkBot.printMessage(message.getPeerId(), "Список модулей кора (" + coreModuleCollection.size() + "): " + stringBuilder.toString());
                break;
            }

            default:
                sendHelpMessage(vkBot, message);
        }
    }

    protected void sendHelpMessage(VkBot vkBot, Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        {
            stringBuilder.append("❗ Список доступных команд:");
            stringBuilder.append("\n Загрузить JAR модуля - !module load <имя файла/*>");
            stringBuilder.append("\n Отключить и выгрузить модуль - !module unload <название/*>");
            stringBuilder.append("\n Включить модуль - !module enable <название>");
            stringBuilder.append("\n Выключить модуль - !module disable <название>");
            stringBuilder.append("\n Перезагрузить все модули - !module reload *");
            stringBuilder.append("\n Информация о модуле - !module info <название>");
            stringBuilder.append("\n Показать список модулей - !module list");
        }

        vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
    }
}
