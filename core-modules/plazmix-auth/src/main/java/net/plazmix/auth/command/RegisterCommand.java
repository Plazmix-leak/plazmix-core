package net.plazmix.auth.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.mojang.MojangApi;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

public class RegisterCommand extends CommandExecutor {

    public RegisterCommand() {
        super("register", "reg");

        setCanUseLoginServer(true);
        setOnlyAuthorized(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof CorePlayer)) {
            return;
        }

        int playerId = NetworkManager.INSTANCE.getPlayerId(commandSender.getName());

        if (AuthManager.INSTANCE.hasPlayerAccount(playerId)) {
            commandSender.sendLangMessage("LOGIN_USAGE");
            return;
        }

        if (args.length < 2) {
            commandSender.sendLangMessage("REGISTER_USAGE");
            return;
        }

        String currentPassword = args[0];
        String confirmPassword = args[1];

        if (currentPassword.length() < 6 || currentPassword.length() > 24) {
            commandSender.sendLangMessage("PASSWORD_NOT_VALID");
            return;
        }

        if (!currentPassword.equals(confirmPassword)) {
            commandSender.sendLangMessage("PASSWORD_NOT_MATCH");
            return;
        }

        CorePlayer corePlayer = ((CorePlayer) commandSender);

        commandSender.sendLangMessage("ACCOUNT_NOT_LINKED_VK");
        AuthManager.INSTANCE.registerPlayer(commandSender.getName(), confirmPassword, MojangApi.isPremium(corePlayer));
    }

}
