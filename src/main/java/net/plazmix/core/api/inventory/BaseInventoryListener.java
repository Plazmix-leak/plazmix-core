package net.plazmix.core.api.inventory;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.InventoryClickEvent;
import net.plazmix.core.api.event.impl.InventoryCloseEvent;
import net.plazmix.core.api.event.impl.InventoryOpenEvent;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryClickHandler;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryDisplayableHandler;
import net.plazmix.core.api.inventory.item.BaseInventoryClickItem;
import net.plazmix.core.api.utility.WeakObjectCache;
import net.plazmix.core.connection.player.CorePlayer;

public class BaseInventoryListener implements EventListener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        CorePlayer player = inventoryOpenEvent.getCorePlayer();
        BaseInventory baseInventory = PlazmixCore.getInstance().getInventoryManager().getPlayerInventory(player);

        if (baseInventory == null) {
            return;
        }

        WeakObjectCache weakObjectCache = WeakObjectCache.create();

        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("isOpen", true);
        weakObjectCache.addObject("event", inventoryOpenEvent);

        baseInventory.getInventoryInfo().handleHandlers(BaseInventoryDisplayableHandler.class, weakObjectCache);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        CorePlayer player = inventoryCloseEvent.getPlayer();
        BaseInventory baseInventory = PlazmixCore.getInstance().getInventoryManager().getPlayerInventory(player);

        if (baseInventory == null) {
            return;
        }

        PlazmixCore.getInstance().getInventoryManager().removeInventory(player, baseInventory);
        PlazmixCore.getInstance().getInventoryManager().removeInventoryUpdateTask(baseInventory);


        WeakObjectCache weakObjectCache = WeakObjectCache.create();

        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("isOpen", false);
        weakObjectCache.addObject("event", inventoryCloseEvent);

        baseInventory.getInventoryInfo().handleHandlers(BaseInventoryDisplayableHandler.class, weakObjectCache);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        CorePlayer player = inventoryClickEvent.getPlayer();
        BaseInventory baseInventory = PlazmixCore.getInstance().getInventoryManager().getPlayerInventory(player);

        if (baseInventory == null) {
            return;
        }

        int itemSlot = inventoryClickEvent.getItemSlot();
        BaseInventoryItem baseInventoryItem = baseInventory.getInventoryInfo().getItem(itemSlot);

        if (baseInventoryItem == null && !baseInventory.getInventorySettings().isUseOnlyCacheItems()) {
            return;
        }

        if (baseInventoryItem instanceof BaseInventoryClickItem) {
            ((BaseInventoryClickItem) baseInventoryItem).getInventoryClickHandler().onClick(baseInventory, inventoryClickEvent);
        }

        WeakObjectCache weakObjectCache = WeakObjectCache.create();

        weakObjectCache.addObject("slot", itemSlot);
        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("event", inventoryClickEvent);

        baseInventory.getInventoryInfo().handleHandlers(BaseInventoryClickHandler.class, weakObjectCache);
    }

}
