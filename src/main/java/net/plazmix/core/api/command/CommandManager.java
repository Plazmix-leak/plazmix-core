package net.plazmix.core.api.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class CommandManager {

    @Getter
    private final Map<String, CommandExecutor> commandMap = new HashMap<>();

    /**
     * Выполнить команду от имени отправтеля
     *
     * @param commandSender - отправитель
     * @param command - команда
     */
    public boolean dispatchCommand(@NonNull CommandSender commandSender, @NonNull String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        String[] commandArg = command.trim().split(" ", -1);
        CommandExecutor commandExecutor = getCommand(commandArg[0]);

        if (commandExecutor == null) {
            return false;
        }

        log.info("[Player] " + commandSender.getName() + " was dispatched command: /" + command);

        commandExecutor.onExecute(commandSender, Arrays.copyOfRange(commandArg, 1, commandArg.length));
        return true;
    }

    /**
     * Зарегистрировать команду
     *
     * @param commandExecutor - команда
     */
    public void registerCommand(@NonNull CommandExecutor commandExecutor) {
        commandMap.put(commandExecutor.getCommand(), commandExecutor);

        for (String commandAlias : commandExecutor.getAliases()) {
            commandMap.put(commandAlias, commandExecutor);
        }
    }

    /**
     * Проверяет, зарегистрирована ли команда
     *
     * @param commandName - имя команды
     */
    public boolean commandIsExists(@NonNull String commandName) {
        String[] commandArg = commandName.replaceFirst("/", "").split(" ", -1);

        return getCommand(commandArg[0]) != null;
    }

    /**
     * Получить команду по ее названию
     *
     * @param commandName - имя команды
     */
    public CommandExecutor getCommand(@NonNull String commandName) {
        return commandMap.get(commandName.toLowerCase());
    }

}
