package net.plazmix.coreconnector.direction.bukkit.listener;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.module.type.skin.SkinsModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SkinsListener implements Listener {

    private final SkinsModule skinsModule
            = NetworkModule.getInstance().getSkinsModule();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerSkin playerSkin = skinsModule.generate(player.getName());

        // обновляем скин игроку
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", playerSkin.getSkinObject().getValue(), playerSkin.getSkinObject().getSignature()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        skinsModule.playerSkinsHistoryMap.remove(NetworkModule.getInstance().getPlayerId(event.getPlayer().getName()));
    }

}
