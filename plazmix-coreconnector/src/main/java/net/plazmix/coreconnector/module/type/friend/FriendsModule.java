package net.plazmix.coreconnector.module.type.friend;

import net.plazmix.coreconnector.module.BaseServerModule;

import java.util.List;

public class FriendsModule extends BaseServerModule {

    public static final String LIST_FRIEND_KEY      = "LIST_FRIEND";
    public static final String ADD_FRIEND_KEY       = "ADD_FRIEND";
    public static final String REMOVE_FRIEND_KEY    = "REMOVE_FRIEND";

    public FriendsModule() {
        super("TynixFriends");

        container.setOnReadPacketKey(LIST_FRIEND_KEY, List.class);
        container.setOnReadPacketKey(ADD_FRIEND_KEY, String.class);
        container.setOnReadPacketKey(REMOVE_FRIEND_KEY, String.class);
    }


}
