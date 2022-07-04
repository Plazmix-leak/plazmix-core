package net.plazmix.auth.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.common.group.Group;

public class UnregisterCommand extends CommandExecutor {

    public UnregisterCommand() {
        super("unregister", "unreg", "playerunregister", "punreg", "разрегистрировать");

        setMinimalGroup(Group.ADMIN);

        setOnlyAuthorized(true);
        setCanUseLoginServer(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendLangMessage("UNREGISTER_USAGE");

            return;
        }

        commandSender.sendLangMessage("UNREGISTER_SUCCESS");

        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(args[0]);
        authPlayer.unregister();

        authPlayer.logout();
    }

}
