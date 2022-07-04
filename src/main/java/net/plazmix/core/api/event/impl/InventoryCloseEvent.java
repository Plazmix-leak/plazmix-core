package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public class InventoryCloseEvent extends Event {

    private final CorePlayer player;
    private final BaseInventory inventory;
}
