package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;

public class BuildCommand extends CommandExecutor {

    public BuildCommand() {
        super("build", "билда");

        setMinimalGroup(Group.BUILDER);

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (corePlayer.getBukkitServer().getName().startsWith("build")) {
            corePlayer.sendLangMessage("ALREADY_CONNECTION");
            return;
        }

        if (PlazmixCore.getInstance().getServersByPrefix("build").isEmpty()) {
            corePlayer.sendLangMessage("SERVER_NOT_FOUND");

        } else {
            corePlayer.connectToServer("build-1");
        }
    }

}
