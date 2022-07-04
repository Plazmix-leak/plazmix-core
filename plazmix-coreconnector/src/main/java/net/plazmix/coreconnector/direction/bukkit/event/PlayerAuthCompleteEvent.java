package net.plazmix.coreconnector.direction.bukkit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.plazmix.coreconnector.core.auth.AuthPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayerAuthCompleteEvent extends Event {

    private final AuthPlayer authPlayer;

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(authPlayer.getPlayerName());
    }


    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
