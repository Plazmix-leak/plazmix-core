package net.plazmix.friends.inventory;

import lombok.NonNull;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.MouseAction;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.common.friend.CoreFriend;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.friends.function.FriendsListFilter;
import net.plazmix.friends.function.FriendsListSort;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsListInventory extends CorePaginatedInventory {

    private FriendsListFilter friendsListFilter = FriendsListFilter.NO_FILTER;
    private FriendsListSort friendsListSort = FriendsListSort.BY_ONLINE;

    public FriendsListInventory() {
        super(5, "Список друзей");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        CoreFriend coreFriend = CoreFriend.of(corePlayer.getPlayerId());

        // Inventory players markup.
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);

        // Set information item.
        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§aОбщая информация")
                .addLore("§7Всего друзей: §f" + coreFriend.getFriendsCount())
                .addLore("§7В сети: §f" + coreFriend.getFriendsOfflinePlayers(CorePlayer::isOnline).size())

                .build());

        // Set friends management items.
        addItem(3, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)

                        .setDisplayName("§aСортировка списка")
                        .addLore("")
                        .addLore("§7При помощи данной функции можно")
                        .addLore("§7отсортировать список друзей")

                        .addLore("")
                        .addLore("§7Текущая сортировка: §f" + friendsListSort.getSortingName())
                        .addLore("")

                        .addLore("§a▸ Нажмите ЛКМ, чтобы сменить сортировку на следующую")
                        .addLore("§a▸ Нажмите ПКМ, чтобы сменить сортировку на предыдущую")

                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTViZTIyYjVkNGE4NzVkNzdkZjNmNzcxMGZmNDU3OGVmMjc5MzlhOTY4NGNiZGIzZDMxZDk3M2YxNjY4NDkifX19")
                        .build(),

                (player1, inventoryClickEvent) -> {

                    if (inventoryClickEvent.getMouseAction().equals(MouseAction.LEFT)) {
                        friendsListSort = friendsListSort.next();
                    }

                    else if (inventoryClickEvent.getMouseAction().equals(MouseAction.RIGHT)) {
                        friendsListSort = friendsListSort.back();
                    }

                    updateInventory(corePlayer);
                });

        addItem(7, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)

                        .setDisplayName("§aФильтр списка")
                        .addLore("")
                        .addLore("§7При помощи данной функции можно")
                        .addLore("§7отфильтровать список друзей")

                        .addLore("")
                        .addLore("§7Текущий фильтр: §f" + friendsListFilter.getSortingName())
                        .addLore("")

                        .addLore("§a▸ Нажмите ЛКМ, чтобы сменить фильтр на следующий")
                        .addLore("§a▸ Нажмите ПКМ, чтобы сменить фильтр на предыдущий")

                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRiZjhkODBhZTlkODdiYzI0OGI4OGRhNmFkNDdkZjQ2YmVmMjRiNmVmNzBkOTI2YmY4MDdiNGYwNDM3In19fQ==")
                        .build(),

                (player1, inventoryClickEvent) -> {

                    if (inventoryClickEvent.getMouseAction().equals(MouseAction.LEFT)) {
                        friendsListFilter = friendsListFilter.next();
                    }

                    else if (inventoryClickEvent.getMouseAction().equals(MouseAction.RIGHT)) {
                        friendsListFilter = friendsListFilter.back();
                    }

                    updateInventory(corePlayer);
                });

        // Add player friends list.
        List<CorePlayer> friendsPlayersList = coreFriend.getFriendsOfflinePlayers(friendsListFilter.getItemFilter())
                .stream()
                .sorted(friendsListSort.getFriendComparator())
                .collect(Collectors.toCollection(LinkedList::new));

        if (friendsPlayersList.isEmpty()) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cНичего не найдено!")
                    .build());

            return;
        }

        for (CorePlayer friendPlayer : friendsPlayersList) {
            AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(friendPlayer.getPlayerId());

            ItemBuilder itemBuilder = ItemBuilder.newBuilder(friendPlayer.isOnline() ? Material.SKULL_ITEM : Material.STAINED_GLASS_PANE)
                    .setDurability(friendPlayer.isOnline() ? 3 : 14)
                    .setPlayerSkull(friendPlayer.getName());

            itemBuilder.setDisplayName(friendPlayer.getDisplayName());
            itemBuilder.addLore("");

            itemBuilder.addLore("§7Привязанный VK: " + (!authPlayer.hasVKUser() ? "§cне привязано" : "§f@id" + authPlayer.getVkId()));
            itemBuilder.addLore("§7Игровой язык: §f" + friendPlayer.getLanguageType().getDisplayName());

            itemBuilder.addLore("");

            if (friendPlayer.isOnline()) {
                itemBuilder.addLore("§7Статус: §aв сети");
                itemBuilder.addLore("§7Текущий сервер: §e" + friendPlayer.getBukkitServer().getName());
                itemBuilder.addLore("");

                itemBuilder.addLore("§7Версия клиента: §f" + friendPlayer.getMinecraftVersionName());

                itemBuilder.addLore("");
                itemBuilder.addLore("§e▸ Нажмите, чтобы телепортироваться!");

            } else {

                itemBuilder.addLore("§7Статус: §cне в сети");
                itemBuilder.addLore("§7Последний сервер: §e" + friendPlayer.getPlayerOfflineData().getLastServerName());
                itemBuilder.addLore("");
                itemBuilder.addLore("§7Последний раз был в сети:");
                itemBuilder.addLore(" §f" + NumberUtil.getTime(System.currentTimeMillis() - friendPlayer.getPlayerOfflineData().getLastOnline().getTime()) + " назад");
            }

            addItemToMarkup(itemBuilder.build(), (baseInventory, inventoryClickEvent) -> {

                if (friendPlayer.isOnline()) {
                    corePlayer.connectToServer(friendPlayer.getBukkitServer());
                }
            });
        }

    }

}
