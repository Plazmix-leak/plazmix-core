package net.plazmix.core.api.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.sounds.SoundType;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

public class TestCommand extends CommandExecutor {

    public TestCommand() {
        super("coretest");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(commandSender.getName());

        corePlayer.playSound(SoundType.BLOCK_STONE_BREAK, 1, 1);
    }

}
