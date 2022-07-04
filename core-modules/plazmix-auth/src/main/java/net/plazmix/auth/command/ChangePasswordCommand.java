package net.plazmix.auth.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;

public class ChangePasswordCommand extends CommandExecutor {

    public ChangePasswordCommand() {
        super("changepassword", "changepass", "cpass");

        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(commandSender.getName());

        if (!AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
            commandSender.sendLangMessage("PLAYER_NOT_AUTH");

            return;
        }

        if (!AuthManager.INSTANCE.hasPlayerAccount(corePlayer.getPlayerId())) {
            commandSender.sendLangMessage("PLAYER_NOT_REGISTER");

            return;
        }

        if (args.length < 2) {
            commandSender.sendLangMessage("CHANGE_PASSWORD_USAGE");

            return;
        }

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(corePlayer.getPlayerId());

        if (!authPlayer.equalsPassword(args[0])) {
            commandSender.sendLangMessage("PLAYER_WRONG_PASSWORD");

            return;
        }

        commandSender.sendLangMessage("PLAYER_SUCCESS_CHANGE_PASSWORD");

        authPlayer.setNewPassword(args[1]);
        authPlayer.logout();
    }
}
