package net.plazmix.core.api.inventory.handler;

import lombok.NonNull;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.utility.WeakObjectCache;

public interface BaseInventoryHandler {

    void handle(@NonNull BaseInventory inventory, WeakObjectCache objectCache);
}
