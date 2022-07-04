package net.plazmix.core.api.command.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;

public class ShutdownCommand extends CommandExecutor {

    public ShutdownCommand() {
        super("corestop", "coreend", "shutdown");

        setOnlyAuthorized(true);
        setMinimalGroup(Group.SR_DEVELOPER);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        PlazmixCore.getInstance().shutdown();
    }

}
