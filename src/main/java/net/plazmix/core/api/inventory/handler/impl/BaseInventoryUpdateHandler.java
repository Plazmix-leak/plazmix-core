package net.plazmix.core.api.inventory.handler.impl;

import lombok.NonNull;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.handler.BaseInventoryHandler;
import net.plazmix.core.api.utility.WeakObjectCache;
import net.plazmix.core.connection.player.CorePlayer;

public interface BaseInventoryUpdateHandler extends BaseInventoryHandler {

    void onUpdate(@NonNull BaseInventory baseInventory, @NonNull CorePlayer player);

    @Override
    default void handle(@NonNull BaseInventory baseInventory,
                        WeakObjectCache objectCache) {

        onUpdate(baseInventory, objectCache.getObject(CorePlayer.class, "player"));
    }
}
