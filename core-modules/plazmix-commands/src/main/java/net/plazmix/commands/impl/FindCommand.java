package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;

public class FindCommand extends CommandExecutor {

    public FindCommand() {
        super("find", "search", "найти");
        setOnlyAuthorized(true);
    }


    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendLangMessage("FIND_HELP");
            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(args[0]);

        if (corePlayer == null) {
            commandSender.sendLangMessage("PLAYER_OFFLINE");
            return;
        }

        commandSender.sendLangMessage("FIND_MESSAGE", "%player%", corePlayer.getDisplayName(), "%server%", corePlayer.getBukkitServer().getName());
    }

}
