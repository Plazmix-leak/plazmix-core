package net.plazmix.coreconnector.direction.bungee.listener;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.protocol.client.CPlayerMutePacket;

public final class ChatListener implements Listener {

    public static final TIntObjectMap<CPlayerMutePacket> PLAYER_MUTES_MAP = new TIntObjectHashMap<>();

    @EventHandler
    public void onChatMute(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = ((ProxiedPlayer) event.getSender());

        int playerId = NetworkModule.getInstance().getPlayerId(proxiedPlayer.getName());

        if (!PLAYER_MUTES_MAP.containsKey(playerId)) {
            return;
        }

        CPlayerMutePacket mutePacket = PLAYER_MUTES_MAP.get(playerId);

        if (mutePacket.isExpired()) {
            return;
        }

        proxiedPlayer.sendMessage("§cВаш чат был временно заблокирован с причиной:");
        proxiedPlayer.sendMessage(" §e" + mutePacket.getReason());
        proxiedPlayer.sendMessage("§cРазблокировка произойдет через ..."); //+ NumberUtil.getTime(mutePacket.getExpireTimeMillis() - System.currentTimeMillis()));

        event.setCancelled(true);
    }

}
