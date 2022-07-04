package net.plazmix.gameselector.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.game.GameServerInfo;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubMode;
import net.plazmix.gameselector.utility.AlphabetUtil;

import java.util.Collection;
import java.util.stream.Collectors;

public class GameModeSelectInventory extends CorePaginatedInventory {

    public static final String NO_SERVERS_TEXTURE   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY1ZDc0NjY4YmRiNTRkMzRjNDgyNThkYjU2YTIxOGFjZGIzYjBmODMwOTZkYTY5MjIzZTZhMGMzNmM1ODZkIn19fQ==";
    private final Collection<ServerSubMode> serverSubModeCollection;

    public GameModeSelectInventory(@NonNull Collection<ServerSubMode> serverSubModeCollection) {
        super(6, "Выбор игровой арены");

        this.serverSubModeCollection = serverSubModeCollection;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);


        int allServersCount = 0;
        int allMapsCount = 0;

        if (serverSubModeCollection.isEmpty()) {
            addItem(32, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cНичего не найдено!")
                    .addLore("§7Пожалуйста, посетите страницу позже")

                    .build());
        }

        for (ServerSubMode subMode : serverSubModeCollection) {

            int serversCount = PlazmixCore.getInstance().getConnectedServersCount(subMode.getSubPrefix());
            int mapsCount = serversCount <= 0 ? 0 : PlazmixCore.getInstance().getConnectedServers(subMode.getSubPrefix())
                    .stream()
                    .filter(bukkitServer -> {

                        GameServerInfo serverInfo = GameServerInfo.of(bukkitServer);
                        return serverInfo != null && serverInfo.isAvailable();
                    })
                    .map(bukkitServer -> {
                        GameServerInfo serverInfo = GameServerInfo.of(bukkitServer);

                        if (serverInfo == null) {
                            return "[null]";
                        }

                        return serverInfo.getMap();
                    })
                    .filter(s -> !s.equals("[null]"))
                    .collect(Collectors.toSet())
                    .size();

            allServersCount += serversCount;
            allMapsCount += mapsCount;


            ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(serversCount > 0 ? AlphabetUtil.getTextureByString(subMode.getName().replaceFirst("SkyWars ", "")) : NO_SERVERS_TEXTURE);

            itemBuilder.addLore("");
            itemBuilder.addLore("§7Серверов доступно: §f" + serversCount);
            itemBuilder.addLore("§7Карт доступно: §f" + mapsCount);
            itemBuilder.addLore("");

            if (serversCount > 0) {
                itemBuilder.setDisplayName(ServerMode.getMode(subMode.getSubPrefix()).getChatColor() + subMode.getName());
                itemBuilder.addLore("§e▸ Нажмите, чтобы перейти!");

            } else {

                itemBuilder.setMaterial(Material.STAINED_GLASS_PANE);
                itemBuilder.setDurability(14);

                itemBuilder.setDisplayName(ChatColor.RED + subMode.getName());

                itemBuilder.addLore("§cСервера не найдены!");
            }

            addItemToMarkup(itemBuilder.build(), (baseInventory, inventoryClickEvent) -> {
                if (serversCount <= 0)
                    return;

                new GameArenaSelectInventory(serverSubModeCollection, subMode).openInventory(player);
            });
        }

        // Set info item.
        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§aОбщая информация")

                .addLore("§7Всего подрежимов: §f" + serverSubModeCollection.size())
                .addLore("")
                .addLore("§7Всего серверов доступно: §f" + allServersCount)
                .addLore("§7Всего карт доступно: §f" + allMapsCount)

                .build());
    }

}
