package net.plazmix.joiner.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.joiner.inventory.JoinerInventory;

public class JoinerCommand extends CommandExecutor {

    public JoinerCommand() {
        super("joiner", "joinermessages", "джоинер", "jm", "джоин");

        setMinimalGroup(Group.STAR);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new JoinerInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

}
