package net.plazmix.guilds.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.MouseAction;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.common.guild.CoreGuild;
import net.plazmix.core.common.guild.GuildSqlHandler;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.guilds.function.GuildsListSort;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class GuildTopInventory extends CoreSimpleInventory {

    private GuildsListSort guildListSort = GuildsListSort.BY_MONEY;

    public GuildTopInventory() {
        super(5, "Топ гильдий");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        // Set friends management items.
        addItem(5, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)

                        .setDisplayName("§aСортировка списка")
                        .addLore("")
                        .addLore("§7При помощи данной функции можно")
                        .addLore("§7отсортировать список гильдий")

                        .addLore("")
                        .addLore("§7Текущая сортировка: §f" + guildListSort.getSortingName())
                        .addLore("")

                        .addLore("§a▸ Нажмите ЛКМ, чтобы сменить сортировку на следующую")
                        .addLore("§a▸ Нажмите ПКМ, чтобы сменить сортировку на предыдущую")

                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTViZTIyYjVkNGE4NzVkNzdkZjNmNzcxMGZmNDU3OGVmMjc5MzlhOTY4NGNiZGIzZDMxZDk3M2YxNjY4NDkifX19")
                        .build(),

                (player1, inventoryClickEvent) -> {

                    if (inventoryClickEvent.getMouseAction().equals(MouseAction.LEFT)) {
                        guildListSort = guildListSort.next();
                    }

                    else if (inventoryClickEvent.getMouseAction().equals(MouseAction.RIGHT)) {
                        guildListSort = guildListSort.back();
                    }

                    updateInventory(corePlayer);
                });

        // Add player guilds list.
        int guildCounter = 0;
        for (CoreGuild coreGuild : GuildSqlHandler.INSTANCE.getGuildsCacheMap()
                .valueCollection()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(guild -> guildListSort.getSortFunction().apply(guild))))
                .limit(10)
                .collect(Collectors.toCollection(LinkedList::new))) {

            CorePlayer leaderName = PlazmixCore.getInstance().getOfflinePlayer(coreGuild.getLeaderName());

            addItem(inventoryMarkup.getMarkupList().get(guildCounter), ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(coreGuild.getLeaderName())

                    .setDisplayName("§7Гильдия: §e" + ChatColor.translateAlternateColorCodes('&', coreGuild.getTitle()))

                    .addLore("")
                    .addLore("§7Создатель: " + leaderName.getDisplayName())
                    .addLore("")
                    .addLore(guildListSort.getLoreFunction().apply(guildListSort.getSortFunction().apply(coreGuild)))

                    .build());

            guildCounter++;
        }

        if (guildCounter == 0) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cУпс, ничего не найдено :c")
                    .build());
        }
    }
}
