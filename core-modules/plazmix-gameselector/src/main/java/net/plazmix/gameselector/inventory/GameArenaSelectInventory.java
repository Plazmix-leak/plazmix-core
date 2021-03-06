package net.plazmix.gameselector.inventory;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.game.GameServerInfo;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerSubMode;
import net.plazmix.gameselector.utility.AlphabetUtil;

import java.util.Collection;

public class GameArenaSelectInventory extends CorePaginatedInventory {

    private final Collection<ServerSubMode> serverSubModeCollection;
    private final ServerSubMode serverSubMode;

    public GameArenaSelectInventory(Collection<ServerSubMode> serverSubModeCollection, ServerSubMode serverSubMode) {
        super(6, serverSubMode.getName());

        this.serverSubModeCollection = serverSubModeCollection;
        this.serverSubMode = serverSubMode;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {

        // Initialize maps.
        BukkitServer bestArena = null;
        Multimap<String, BukkitServer> modeMaps = LinkedHashMultimap.create();

        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getConnectedServers(serverSubMode.getSubPrefix())) {

            GameServerInfo serverInfo = GameServerInfo.of(bukkitServer);
            if (serverInfo == null || !serverInfo.isAvailable()) continue;

            modeMaps.put(serverInfo.getMap(), bukkitServer);

            if (bestArena == null) {
                bestArena = bukkitServer;

            } else {

                if (bukkitServer.getOnlineCount() > bestArena.getOnlineCount() && bukkitServer.getOnlineCount() < serverInfo.getMaxPlayers()) {
                    bestArena = bukkitServer;
                }
            }
        }

        // Add best arena.
        if (bestArena != null) {
            GameServerInfo serverInfo = GameServerInfo.of(bestArena);

            if (serverInfo != null) {
                boolean available = serverInfo.isAvailable();

                BukkitServer finalBestArena = bestArena;
                addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                                .setDurability(3)
                                .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY1YzI3MGM1ZmJhZDkxZDU4N2VlZWVkOGMxM2RhNzIxODQ4MWFhNjRiZGJlZTRhNWU5ZjFlYjUyNzQ4ZWUxYiJ9fX0=")

                                .setDisplayName((available ? ChatColor.YELLOW : ChatColor.RED) + "???????????? ????????????")

                                .addLore("")
                                .addLore("??7??????????: ??f" + serverInfo.getMap())
                                .addLore("??7????????????: ??a" + bestArena.getOnlineCount() + "??f/??c" + serverInfo.getMaxPlayers())
                                .addLore("")

                                .addLore((available ? "??e??? ??????????????, ?????????? ????????????????????????????!" : "??c??? ???????????? ???????????????????? ?????? ??????????????????????!"))
                                .build(),

                        (baseInventory, inventoryClickEvent) -> {

                            if (available) {
                                player.connectToServer(finalBestArena);
                            }
                        });
            }
        }

        // Add maps items.
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(2, 2);
        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        inventoryMarkup.removeInventorySlot(12);
        inventoryMarkup.removeInventorySlot(13);
        inventoryMarkup.removeInventorySlot(21);
        inventoryMarkup.removeInventorySlot(22);

        setInventoryMarkup(inventoryMarkup);


        if (modeMaps.isEmpty()) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("??c???????????? ???? ??????????????!")
                    .addLore("??7????????????????????, ???????????????? ???????????????? ??????????")

                    .build());
        }

        for (String mapName : modeMaps.keySet()) {
            Collection<BukkitServer> servers = modeMaps.get(mapName);

            addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setAmount(servers.size())

                            .setPlayerSkull(AlphabetUtil.getTextureByString(mapName))
                            .setDisplayName(ChatColor.YELLOW + mapName)

                            .addLore("")
                            .addLore("??7???????????????? ????????????????: ??f" + servers.size())
                            .addLore("??7?????????? ????????????: ??f" + servers.stream().mapToInt(AbstractServer::getOnlineCount).sum())
                            .addLore("")
                            .addLore(!player.getGroup().isDefault() ? "??e??? ??????????????, ?????????? ????????????????????????????!" : "??c??? ?????? ???????????? ???????????? ?????????? ?????? ?????????????????? ???????????? " + Group.STAR.getColouredName())

                            .build(),
                    (baseInventory, inventoryClickEvent) -> servers.stream().filter(server -> !player.getGroup().isDefault()).findAny().ifPresent(player::connectToServer));
        }

        // Back item.
        addItem(50, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)
                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyNTkwMmIzODllZDZjMTQ3NTc0ZTQyMmRhOGY4ZjM2MWM4ZWI1N2U3NjMxNjc2YTcyNzc3ZTdiMWQifX19")

                        .setDisplayName("??e?????????????????? ??????????")
                        .addLore("??7??? ??????????????, ?????????? ?????????????????? ???? ???????????????? ??????????!")
                        .build(),

                (baseInventory, inventoryClickEvent) -> new GameModeSelectInventory(serverSubModeCollection).openInventory(player));
    }

}
