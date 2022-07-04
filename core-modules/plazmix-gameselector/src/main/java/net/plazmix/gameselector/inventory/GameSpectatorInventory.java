package net.plazmix.gameselector.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.game.GameServerInfo;
import net.plazmix.core.connection.server.impl.BukkitServer;

public class GameSpectatorInventory extends CorePaginatedInventory {

    private final String prefix;

    public GameSpectatorInventory(String prefix) {
        super(5, "Наблюдатель");

        this.prefix = prefix;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {

        // Add maps items.
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(2, 2);
        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);

        int arenaCounter = 0;
        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getConnectedServers(prefix)) {
            GameServerInfo serverInfo = GameServerInfo.of(bukkitServer);

            if (serverInfo == null || serverInfo.isAvailable())
                continue;

            addItemToMarkup(ItemBuilder.newBuilder(Material.PAPER)
                            .setDisplayName(ChatColor.YELLOW + serverInfo.getMap())

                            .addLore("")
                            .addLore("§7Сервер: §f" + bukkitServer.getName())
                            .addLore("§7Играют: §a" + bukkitServer.getOnlineCount() + "§7/§c" + serverInfo.getMaxPlayers())
                            .addLore("")
                            .addLore("§e▸ Нажмите, чтобы наблюдать!")

                            .build(),
                    (baseInventory, inventoryClickEvent) -> player.connectToServer(bukkitServer));

            arenaCounter++;
        }

        if (arenaCounter <= 0) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cНичего не найдено!")
                    .addLore("§7Пожалуйста, посетите страницу позже")

                    .build());
        }
    }

}
