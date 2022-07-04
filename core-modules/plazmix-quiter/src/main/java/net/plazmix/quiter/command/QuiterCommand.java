package net.plazmix.quiter.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.quiter.inventory.QuiterInventory;

public class QuiterCommand extends CommandExecutor {

    public QuiterCommand() {
        super("quiter", "quitemessage", "quite", "выход");

        setMinimalGroup(Group.STAR);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new QuiterInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

}
