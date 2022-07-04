package net.plazmix.vkbot.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.user.BotUser;

public class HelpCommand extends VkCommand {

    public HelpCommand() {
        super("help", "помощь", "хелп");
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("❗Список доступных команд: \n" +
                        " \n" +
                        "⚠ Узнать информацию об аккаунте - !инфо \n" +
                        "⚠ Узнать ник пользователя - !ник \n" +
                        " \n" +
                        "\uD83D\uDCCC Привязать аккаунт - !привязать <ник> <пароль> \n" +
                        "\uD83D\uDCCC Отвязать аккаунт - !отвязать <ник> \n" +
                        "\uD83D\uDCCC Сменить/восстановить пароль - !восстановить \n" +
                        " \n" +
                        "\uD83D\uDC40 Узнать онлайн общего или определённого сервера - !онлайн <сервер> \n" +
                        "\uD83D\uDC40 Найти игрока на сервере - !найти <ник>");

        if (botUser.hasPrimaryAccount() && PlazmixCore.getInstance().getOfflinePlayer(botUser.getPrimaryAccountName()).getGroup().isStaff()) {
            stringBuilder.append("\n\n❗ Список команд для команды проекта: \n" +
                    " \n" +
                    "\uD83C\uDD95 Кикнуть игрока - !кик <ник> <причина> \n");
            if (botUser.hasPrimaryAccount() && PlazmixCore.getInstance().getOfflinePlayer(botUser.getPrimaryAccountName()).getGroup().isAdmin()) {
                stringBuilder.append("\n\n❗ Список команд для администрации: \n" +
                        " \n" +
                        "\uD83C\uDD95 Выдать группу игроку - !группа <игрок> <название/номер> \n" +
                        "\uD83C\uDD95 Перенести данные игрока на другой аккаунт - \n" +
                        "!перенос <игрок 1> <игрок 2> \n" +
                        " \n" +
                        "\uD83C\uDF6D Посмотреть информацию о подключенном сервере - !сервер <название> \n" +
                        "\uD83C\uDF6D Удаленно перезагрузить сервер - !рестарт <название> \n" +
                        "\uD83C\uDF6D Объявить сообщение на весь сервер - !alert <сообщение> \n" +
                        "\uD83C\uDF6D Объявить сообщение на весь сервер - !ip <ник> \n" +
                        "\uD83C\uDF6D Управление модулями - !модуль");
            }
        }
        vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
    }
}
