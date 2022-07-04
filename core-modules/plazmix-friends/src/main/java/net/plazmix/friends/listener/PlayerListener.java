package net.plazmix.friends.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.event.impl.ProtocolPacketHandleEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.client.CPlayerConnectPacket;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.common.friend.FriendRequestManager;
import net.plazmix.core.common.friend.CoreFriend;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements EventListener {

    @EventHandler
    public void onPacketHandle(ProtocolPacketHandleEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof CPlayerConnectPacket) {
            CoreFriend.of(((CPlayerConnectPacket) packet).getPlayerName()).inject(null);
        }
    }

    @EventHandler
    public void onPlayerAuthComplete(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();
        CoreFriend coreFriend = CoreFriend.of(corePlayer);

        if (!(coreFriend != null && coreFriend.getFriendsCount() > 0)) {
            return;
        }

        for (CorePlayer onlineFriend : coreFriend.getFriendsOfflinePlayers(CorePlayer::isOnline)) {
            onlineFriend.sendMessage("§d§lДрузья §8:: " + corePlayer.getDisplayName() + " §fзашел на сервер!");
        }

        new CommonScheduler("friends_" + event.getCorePlayer().getPlayerId() + "_injection") {

            @Override
            public void run() {

                // Загрузка всех друзей игрока
                CoreFriend.of(corePlayer)
                        .inject(inject -> corePlayer.sendMessage("§d§lДрузья §8:: §fУ Вас §e" + NumberUtil.formatting(inject.getFriendsCount(), "§fдруг", "§fдруга", "§fдрузей")
                                + ", §a" + inject.getFriendsOfflinePlayers(CorePlayer::isOnline).size() + " §fиз которых в сети!"));

                // Чекаем актуальные запросы в друзья
                Collection<Integer> friendRequestCollection = FriendRequestManager.INSTANCE.getFriendsRequestsIds(corePlayer.getPlayerId());
                if (!friendRequestCollection.isEmpty()) {

                    corePlayer.sendMessage("§d§lДрузья §8:: §fУ Вас есть §e" + friendRequestCollection.size() + " §fнеотвеченных заявок!");
                    corePlayer.sendMessage(" §fЧтобы проверить их, напишите - §e/friends requests");
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerJoin(PlayerAuthCompleteEvent event) {
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        if (!AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
            return;
        }

        CoreFriend coreFriend = CoreFriend.of(corePlayer);

        if (!(coreFriend != null && coreFriend.getFriendsCount() > 0)) {
            return;
        }

        for (CorePlayer onlineFriend : coreFriend.getFriendsOfflinePlayers(CorePlayer::isOnline)) {
            onlineFriend.sendMessage("§d§lДрузья §8:: " + corePlayer.getDisplayName() + " §fвышел с сервера");
        }
    }


}
