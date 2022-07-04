package net.plazmix.friends;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.friends.command.FriendCommand;
import net.plazmix.friends.listener.PlayerListener;
import net.plazmix.core.common.friend.CoreFriend;

@CoreModuleInfo(name = "PlazmixFriends", author = "Plazmix")
public class PlazmixFriends extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new FriendCommand());
        getManagement().registerListener(new PlayerListener());

        getCore().getMysqlConnection().createTable(true, "CoreFriends", "`Id` INT NOT NULL, `FriendId` INT NOT NULL");

        for (CorePlayer corePlayer : getCore().getOnlinePlayers()) {
            CoreFriend.of(corePlayer).inject(null);
        }
    }

    @Override
    protected void onDisable() { }
}
