package net.plazmix.core.api.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.mode.ServerMode;

@Getter
public abstract class CommandExecutor {

    private final String command;
    private final String[] aliases;

    public CommandExecutor(@NonNull String command, @NonNull String... aliases) {
        this.command = command;
        this.aliases = aliases;
    }

    @Setter
    private Group minimalGroup = Group.ABOBA;

    @Setter
    private boolean onlyPlayers = false;

    @Setter
    private boolean onlyAuthorized = true;

    @Setter
    private boolean canUseLoginServer = false;

    /**
     * Выполнение команды
     *
     * @param commandSender - отправитель
     * @param args - аргументы
     */
    protected abstract void executeCommand (
            @NonNull CommandSender commandSender, @NonNull String[] args
    );

    public void onExecute(@NonNull CommandSender commandSender,
                          @NonNull String[] args) {

        if (onlyPlayers && commandSender.getCommandSendingType() == CommandSendingType.CONSOLE) {
            return;
        }

        if (commandSender.getGroup().getLevel() < minimalGroup.getLevel()) {
            commandSender.sendLangMessage("NO_PERM", "%group%", minimalGroup.getColouredName());
            return;
        }

        if (commandSender instanceof CorePlayer) {
            CorePlayer corePlayer = ((CorePlayer) commandSender);

            if (onlyAuthorized && (!AuthManager.INSTANCE.hasAuthSession(corePlayer) || AuthManager.INSTANCE.hasTwofactorSession(corePlayer.getName()))) {
                commandSender.sendLangMessage("USE_ONLY_AUTH");
                return;
            }

            if (!canUseLoginServer && ServerMode.isTyped(corePlayer.getBukkitServer(), ServerMode.AUTH)) {
                commandSender.sendLangMessage("USE_LOGIN_SERVER");
                return;
            }
        }

        executeCommand(commandSender, args);
    }

}
