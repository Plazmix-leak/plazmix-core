package net.plazmix.coreconnector.direction.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import net.plazmix.coreconnector.direction.bukkit.inventory.ShapedCoreInventory;

import java.util.function.Consumer;

public final class ShapedCoreInventoryListener
        implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        ShapedCoreInventory inventory = ShapedCoreInventory.getInventory((Player) event.getWhoClicked());

        if (!(event.getClickedInventory() instanceof PlayerInventory) && inventory != null) {
            Consumer<InventoryClickEvent> itemAction = inventory.getButtonActionsMap().get(event.getSlot() + 1);

            if (itemAction != null) {
                itemAction.accept(event);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        ShapedCoreInventory inventory = ShapedCoreInventory.getInventory((Player) event.getPlayer());

        if (inventory != null) {
            inventory.sendClosePacket();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ShapedCoreInventory inventory = ShapedCoreInventory.getInventory(event.getPlayer());

        if (inventory != null) {
            inventory.sendClosePacket();
        }
    }

}
