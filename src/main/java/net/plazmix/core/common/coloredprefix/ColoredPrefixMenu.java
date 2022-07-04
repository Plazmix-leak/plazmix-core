package net.plazmix.core.common.coloredprefix;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.ItemFlag;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.itemstack.enchantment.EnchantmentType;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.connection.player.CorePlayer;

public class ColoredPrefixMenu extends CorePaginatedInventory {

    public ColoredPrefixMenu() {
        super(6, "Смена цвета префикса");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        BaseInventorySimpleMarkup markup = new BaseInventorySimpleMarkup(inventoryRows);

        markup.addHorizontalRow(2, 2);
        markup.addHorizontalRow(3, 2);
        markup.addHorizontalRow(4, 2);

        markup.removeInventorySlot(12);
        markup.removeInventorySlot(13);
        markup.removeInventorySlot(21);
        markup.removeInventorySlot(22);
        markup.removeInventorySlot(30);
        markup.removeInventorySlot(31);

        setInventoryMarkup(markup);

        ChatColor currentColor = ColoredPrefixSqlHandler.INSTANCE.getPrefixColor(player.getPlayerId());

        int colors = 0;
        for (PrefixColorType prefixColor : PrefixColorType.values()) {

            if (prefixColor.chatColor == null)
                continue;

            addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(prefixColor.skull)

                    .setDisplayName(prefixColor.chatColor + prefixColor.name)

                    .addLore("")
                    .addLore("§7Пример: " + ColoredPrefixSqlHandler.INSTANCE.format(prefixColor.chatColor, player.getGroup()) + " " + player.getName())
                    .addLore("")
                    .addLore(currentColor == null || !currentColor.equals(prefixColor.chatColor) ? "§e▸ Нажмите, чтобы выбрать!" : "§c▸ Данный цвет уже выбран!")

                    .addEnchant(currentColor == null || !currentColor.equals(prefixColor.chatColor) ? null : EnchantmentType.KNOCKBACK, 1)
                    .addFlag(ItemFlag.HIDE_ENCHANTS)

                    .build(),

                    (baseInventory, inventoryClickEvent) -> {

                        if (currentColor != null && currentColor.equals(prefixColor.chatColor)) {
                            return;
                        }

                        ColoredPrefixSqlHandler.INSTANCE.setPrefixColor(player.getPlayerId(), prefixColor.chatColor);

                        player.closeInventory();
                        player.sendMessage("§d§lPlazmix §8:: §fЦвет префикса был успешно изменен на " + prefixColor.chatColor + prefixColor.name);
                    });

            colors++;
        }

        PrefixColorType currentPrefixColorType = PrefixColorType.find(currentColor);
        String displayColorType = PrefixColorType.UNKNOWN.name;

        if (currentPrefixColorType != null && currentPrefixColorType.chatColor != null) {
            displayColorType = currentPrefixColorType.chatColor + currentPrefixColorType.name;
        }

        addItem(21, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§eОбщая информация")

                .addLore("")
                .addLore("§7Всего цветов: §f" + colors)
                .addLore("§7Выбрано: " + displayColorType)

                .build());

        addItem(50, ItemBuilder.newBuilder(Material.BARRIER)
                .setDisplayName("§cСбросить установленный цвет")
                .build(),

                (baseInventory, inventoryClickEvent) -> {

                    ColoredPrefixSqlHandler.INSTANCE.setPrefixColor(player.getPlayerId(), null);

                    player.closeInventory();
                    player.sendMessage("§d§lPlazmix §8:: §fЦвет префикса был успешно сброшен на стандартный!");
                });
    }

    @RequiredArgsConstructor
    private enum PrefixColorType {

        RED(ChatColor.RED, "Красный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmViOTExZWE5NGI1YTFjZjc3ZjNjYTYzN2EzYjE2NjJiMzUxMjFiZDcyZTExODY1MTE4NGYyZmIxMDYwZDEifX19"),
        ORANGE(ChatColor.GOLD, "Оранжевый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQzYjhkYmQ1OTY3ZDgzYTliZjk5NzcxYzI2OGVkZjNjOWVmNmZiNmU2OWM5YWY2ZjNkN2JkNGNiZDlhOSJ9fX0="),
        YELLOW(ChatColor.YELLOW, "Желтый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcyNzc2M2RmMWQxYmE1OTc1MDdlNjE0YjZjNzEzMmEwODY5Yzc0NzJjNGZkYmJlY2Q5OTEzNGNkNTUifX19"),
        LIME(ChatColor.GREEN, "Лаймовый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI3MTZiMzc1MjNmNDUzZTE4NzFmMjI2M2Y4MjNjMjgwYmI4ZGQ3M2Q2OTZkNTI3YjllZWM4N2NkZjMyIn19fQ=="),
        GREEN(ChatColor.DARK_GREEN, "Зеленый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc2ZDRjNWE4YTQxOTdkODZkM2ExNjQ3ZmQzZjllNmQ0OGUwZTBhYjdhOGFlMWU5YmU1MGY2N2UyNWMifX19"),
        BLUE(ChatColor.BLUE, "Синий", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDYxMjcxZDlhNTdmZWE1YThkMzhmYTgyMWU2NDY5ODEyYTIwMzcxNzYwNmEyNGIwN2I0MmMyYTZiYzczIn19fQ=="),
        DARK_AQUA(ChatColor.DARK_AQUA, "Бирюзовый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVhZWJhNzBjM2VjYTQ5ODMzNmY2NDkzNDQ1MWExOWJlYzA5YjRiZWZmNzRmOTlkYTRjNzY1MWUzNDBiMDMifX19"),
        AQUA(ChatColor.AQUA, "Голубой","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZiMTZiMjI0MDJlZTI3YTlhYTE0ZmVkMTA3ODcyYmM1YzRiNTJlMTMzODE1NDZmOGJjMTNkYjQ5OTU1MjkxNiJ9fX0="),
        PINK(ChatColor.LIGHT_PURPLE, "Розовый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUxY2YwNzVhMWY1MzMxNWQ3NjE4MjFjZWY3MmEyNWY2YzE1ZGQ5OGE3YTM4NzJmYzliZWM1OTdhYmMzYjEifX19"),
        PURPLE(ChatColor.DARK_PURPLE, "Фиолетовый","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY4NTE2MDAwM2VhYTkxMzVlMDJiMGMyODhiYzZmYzAyMzU2YmViYjAxYmE3MzE3ZmUxMTQzNWU5NjdmMGVjNSJ9fX0="),
        WHITE(ChatColor.WHITE, "Белый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1YjQ2YjVmYzU5M2IwODZlYmVjNTMyYzY2ZmMwMzFhOTQxNjEyZmM2ZGVmNTc1NjFkODNhOWNlYTIwZjcifX19"),
        GRAY(ChatColor.GRAY, "Серый", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDg2ZGI0ODExNjJlZjE0NDE5MjUzNGU1MjkyYjRlNDgxZDlhNWM1OGJkOTRhYzc3YjE4ZDMzYmIzNDAyMSJ9fX0="),

        UNKNOWN(null, "§cНе найдено", "")
        ;

        private final ChatColor chatColor;
        private final String name;
        private final String skull;

        public static PrefixColorType find(ChatColor chatColor) {
            if (chatColor == null) {
                return PrefixColorType.UNKNOWN;
            }

            for (PrefixColorType prefixColor : values()) {

                if (prefixColor.chatColor.equals(chatColor)) {
                    return prefixColor;
                }
            }

            return null;
        }
    }

}
