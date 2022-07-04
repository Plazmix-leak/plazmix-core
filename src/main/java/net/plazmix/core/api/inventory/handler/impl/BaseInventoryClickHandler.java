package net.plazmix.core.api.inventory.handler.impl;

import lombok.NonNull;
import net.plazmix.core.api.event.impl.InventoryClickEvent;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.handler.BaseInventoryHandler;
import net.plazmix.core.api.utility.WeakObjectCache;

public interface BaseInventoryClickHandler extends BaseInventoryHandler {

    void onClick(@NonNull BaseInventory baseInventory, @NonNull InventoryClickEvent inventoryClickEvent);

    @Override
    default void handle(@NonNull BaseInventory baseInventory,
                        WeakObjectCache objectCache) {

        onClick(baseInventory, objectCache.getObject(InventoryClickEvent.class, "event"));
    }
}
