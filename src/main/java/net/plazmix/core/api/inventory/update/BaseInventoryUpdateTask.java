package net.plazmix.core.api.inventory.update;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.inventory.BaseInventory;

@RequiredArgsConstructor
@Getter
public class BaseInventoryUpdateTask {

    private final BaseInventory lattyInventory;

    private final long updateTaskDelay;
    private final Runnable inventoryUpdateTask;
}
