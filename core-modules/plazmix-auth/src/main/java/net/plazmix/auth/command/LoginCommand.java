package net.plazmix.auth.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;

public class LoginCommand extends CommandExecutor {

    public LoginCommand() {
        super("login", "log", "l");

        setCanUseLoginServer(true);
        setOnlyAuthorized(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof CorePlayer)) {
            return;
        }

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(commandSender.getName());

        if (authPlayer == null) {
            commandSender.sendLangMessage("PLAYER_NOT_REGISTER");
            return;
        }

        if (AuthManager.INSTANCE.hasAuthSession(authPlayer.getHandle())) {
            commandSender.sendLangMessage("PLAYER_ALREADY_AUTH");
            return;
        }

        if (AuthManager.INSTANCE.hasTwofactorSession(authPlayer.getPlayerName())) {
            return;
        }

        if (args.length == 0) {
            commandSender.sendLangMessage("LOGIN_USAGE");
            return;
        }

        String playerPassword = args[0];

        if (!authPlayer.equalsPassword(playerPassword)) {
            commandSender.sendLangMessage("PLAYER_WRONG_PASSWORD");
            return;
        }

        authPlayer.completeWithTwofactorCode();
    }

}
