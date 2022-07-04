package net.plazmix.core.api.inventory.handler.impl;

import lombok.NonNull;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.handler.BaseInventoryHandler;
import net.plazmix.core.api.utility.WeakObjectCache;
import net.plazmix.core.connection.player.CorePlayer;

public interface BaseInventoryDisplayableHandler extends BaseInventoryHandler {

    void onOpen(@NonNull CorePlayer player);
    void onClose(@NonNull CorePlayer player);

    @Override
    default void handle(@NonNull BaseInventory baseInventory,
                        WeakObjectCache objectCache) {

        CorePlayer player = objectCache.getObject(CorePlayer.class, "player");
        boolean isOpen = objectCache.getObject(boolean.class, "isOpen");

        if (isOpen) {
            onOpen(player);
        } else {
            onClose(player);
        }
    }
}
