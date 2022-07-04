package net.plazmix.auth.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.command.CommandSendingType;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;

public class LogoutCommand extends CommandExecutor {

    public LogoutCommand() {
        super("logout");
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (commandSender.getCommandSendingType().equals(CommandSendingType.CONSOLE)) {
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(commandSender.getName());

        if (!AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
            commandSender.sendLangMessage("PLAYER_NOT_AUTH");

            return;
        }

        if (!AuthManager.INSTANCE.hasPlayerAccount(corePlayer.getPlayerId())) {
            commandSender.sendLangMessage("PLAYER_NOT_REGISTER");

            return;
        }

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(corePlayer.getPlayerId());
        authPlayer.logout();
    }
}
