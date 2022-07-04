package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.MouseAction;
import net.plazmix.core.connection.player.CorePlayer;

@Getter
@RequiredArgsConstructor
public class InventoryClickEvent extends Event {

    private final CorePlayer player;
    private final BaseInventory inventory;

    private final MouseAction mouseAction;

    private final int itemSlot;

}
