package net.plazmix.coreconnector.direction.bukkit.inventory;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.plazmix.coreconnector.CoreConnector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.protocol.client.CInventoryOpenPacket;
import net.plazmix.coreconnector.protocol.client.CInventorySetItemPacket;
import net.plazmix.coreconnector.protocol.server.SInventoryClosePacket;
import net.plazmix.coreconnector.protocol.server.SInventoryInteractPacket;

import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ShapedCoreInventory {

    public static final TIntObjectMap<ShapedCoreInventory> PLAYERS_INVENTORIES_CACHE
            = new TIntObjectHashMap<>();

    public static synchronized ShapedCoreInventory getInventory(@NonNull Player player) {
        return PLAYERS_INVENTORIES_CACHE.get(NetworkModule.getInstance().getPlayerId(player.getName()));
    }

    public static synchronized void openInventory(@NonNull CInventoryOpenPacket inventoryPacket) {

        // Create inventory.
        ShapedCoreInventory inventory = new ShapedCoreInventory(inventoryPacket);

        // Close previous player inventory.
        //
        // Так надо, иначе InventoryCloseEvent сработает рандомно,
        // и многие предметы тупо не отрисуются, потому что
        // сам инвентарь будет равен уже null
        //
        Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> {
            int playerId = NetworkModule.getInstance().getPlayerId(inventoryPacket.getPlayerName());

            PLAYERS_INVENTORIES_CACHE.remove(playerId);

            inventory.openInventory();
            PLAYERS_INVENTORIES_CACHE.put(playerId, inventory);
        });
    }


    @NonFinal Inventory itemContainer;
    CInventoryOpenPacket inventoryPacket;

    TIntObjectMap<Consumer<InventoryClickEvent>> buttonActionsMap = new TIntObjectHashMap<>();

    private ShapedCoreInventory(@NonNull CInventoryOpenPacket inventoryPacket) {
        this.inventoryPacket = inventoryPacket;
        this.itemContainer = createContainer();
    }

    @NonNull
    synchronized Inventory createContainer() {
        return Bukkit.createInventory(null, inventoryPacket.getInventoryRows() * 9, inventoryPacket.getInventoryTitle());
    }


    public synchronized void setPacketItem(@NonNull CInventorySetItemPacket packet) {

        // Create a interact packet.
        SInventoryInteractPacket inventoryInteractPacket = new SInventoryInteractPacket(inventoryPacket.getPlayerName(), packet.getSlot(), null);

        buttonActionsMap.put(packet.getSlot(), (inventoryClickEvent) -> {

            inventoryInteractPacket.setClickType(inventoryClickEvent.getClick());
            CoreConnector.getInstance().sendPacket(inventoryInteractPacket);
        });

        // Set item to bukkit container.
        itemContainer.setItem(packet.getSlot() - 1, packet.getItemStack());
    }

    private synchronized void openInventory() {
        Player playerHandle = Bukkit.getPlayer(inventoryPacket.getPlayerName());

        if (playerHandle != null && playerHandle.isOnline()) {
            playerHandle.openInventory(itemContainer);
        }
    }

    public synchronized void sendClosePacket() {
        PLAYERS_INVENTORIES_CACHE.remove(NetworkModule.getInstance().getPlayerId(inventoryPacket.getPlayerName()));

        CoreConnector.getInstance().sendPacket(new SInventoryClosePacket(inventoryPacket.getPlayerName()));
    }

}
