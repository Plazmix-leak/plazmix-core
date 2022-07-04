package net.plazmix.commands.inventory.other;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class StaffCommand extends CommandExecutor {

    public StaffCommand() {
        super("staff", "staffs", "stafflist", "стафф", "персонал", "админы");

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new StaffSelectGroupInventory().openInventory((CorePlayer) commandSender);
    }


    protected static class StaffSelectGroupInventory extends CoreSimpleInventory {

        public StaffSelectGroupInventory() {
            super(5, "Персонал сервера");
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            drawItemByGroup(26, Group.JR_MODER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTlhMWMxOTFlMGViYWJlODlkZGYxOGE4YmFjOGY0MjgwZTNhYzZiYzY2MWMxM2NlMWRmZjY3NGRhZDI4ODVlMyJ9fX0=");
            drawItemByGroup(25, Group.MODER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTlhMWMxOTFlMGViYWJlODlkZGYxOGE4YmFjOGY0MjgwZTNhYzZiYzY2MWMxM2NlMWRmZjY3NGRhZDI4ODVlMyJ9fX0=");
            drawItemByGroup(24, Group.SR_MODER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTlhMWMxOTFlMGViYWJlODlkZGYxOGE4YmFjOGY0MjgwZTNhYzZiYzY2MWMxM2NlMWRmZjY3NGRhZDI4ODVlMyJ9fX0=");
            drawItemByGroup(23, Group.ADMIN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgyN2JmODg5ODY5ZDc0MzNkZmQ5ZTBjZWM2ZmQ4ZjQ0NGZkMmI2MjI2NzNjZjI0MTY2NTVlZTMwZTY3NzcyZiJ9fX0=");
            drawItemByGroup(22, Group.DEVELOPER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUyMzQ2OTBiNjU2NzQ2OTNkZTMxMDk1ODc4YzExNWM5MTlhNzEzYWI4YThhNjY3ZTA1YmY5OTM3OTlhOTdlOSJ9fX0=");
            drawItemByGroup(21, Group.SR_DEVELOPER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUxYmI4MmU2NGNmMDNkZTQzNDBlYWQ0NDkwNzdlZDViZDFkZmU5Yzc0ZjgzZDU0NTA2ZjRlZTk4YzI2NGJkYyJ9fX0=");
            drawItemByGroup(20, Group.OWNER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE0ZDE3MzM3OWZhNDg3MDdmZjJmMTQ2Yzg3NTE1YmM1YTM3NjI5YzY0YzdjYWE1ZTBmZmJiYzZiMTIxNTM5ZCJ9fX0=");
        }

        protected void drawItemByGroup(int itemSlot,
                                       @NonNull Group currentGroup,
                                       @NonNull String playerSkull) {

            Collection<CorePlayer> staffPlayersCollection = PlazmixCore.getInstance().getOfflinePlayersByGroup(currentGroup);

            if (staffPlayersCollection.isEmpty()) {
                addItem(itemSlot, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability(14)
                        .setDisplayName(currentGroup.getColouredName())
                        .addLore("",
                                "§cДанный список персонала пустой!",
                                " §8В сети " + staffPlayersCollection.stream().filter(CorePlayer::isOnline).count() + " человек",
                                "")

                        .setPlayerSkull(playerSkull)
                        .build());

                return;
            }

            addItem(itemSlot, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setDisplayName(currentGroup.getColouredName())
                            .addLore("",
                                    "§7Нажмите, чтобы перейти в список персонала!",
                                    " §8В сети " + staffPlayersCollection.stream().filter(CorePlayer::isOnline).count() + " человек",
                                    "")

                            .setPlayerSkull(playerSkull)
                            .build(),

                    (inventory, inventoryClickEvent) -> new StaffListInventory(currentGroup, this).openInventory(inventoryClickEvent.getPlayer()));
        }

    }

    protected static class StaffListInventory extends CorePaginatedInventory {

        private final Group currentGroup;
        private final CoreSimpleInventory previousInventory;

        public StaffListInventory(@NonNull Group currentGroup,
                                  @NonNull CoreSimpleInventory previousInventory) {

            super(5, currentGroup.getName());

            this.currentGroup = currentGroup;
            this.previousInventory = previousInventory;
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

            getInventoryMarkup().addHorizontalRow(2, 1);
            getInventoryMarkup().addHorizontalRow(3, 1);
            getInventoryMarkup().addHorizontalRow(4, 1);

            Collection<CorePlayer> staffPlayersCollection = PlazmixCore.getInstance().getOfflinePlayersByGroup(currentGroup);

            for (CorePlayer offlinePlayer : staffPlayersCollection.stream().sorted(Comparator.comparing(CorePlayer::isOnline).reversed())
                    .collect(Collectors.toList())) {

                if (offlinePlayer.isOnline()) {
                    addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                                    .setDurability(3)
                                    .setPlayerSkull(offlinePlayer.getName())

                                    .setDisplayName(offlinePlayer.getDisplayName())
                                    .addLore("",
                                            "§7В сети: §aда",
                                            "§7Сервер: §f" + offlinePlayer.getBukkitServer().getName(),
                                            "",
                                            "§eНажмите, чтобы присоединиться к серверу!")

                                    .build(),

                            (corePlayer1, mouseAction) -> {

                                BukkitServer bukkitServer = offlinePlayer.getBukkitServer();

                                if (corePlayer.getBukkitServer().getName().equals(bukkitServer.getName())) {
                                    corePlayer.sendLangMessage("ALREADY_CONNECTION");
                                    return;
                                }

                                corePlayer.sendMessage("§d§lPlazmix §8:: §fВы успешно подключились к серверу " + offlinePlayer.getDisplayName() + " §f- §e" + bukkitServer.getName());
                                corePlayer.connectToServer(bukkitServer);
                            });

                    continue;
                }

                addItemToMarkup(ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability(14)
                        .setPlayerSkull(offlinePlayer.getName())

                        .setDisplayName(offlinePlayer.getDisplayName())
                        .addLore("",
                                "§7Последний сервер: §f" + offlinePlayer.getPlayerOfflineData().getLastServerName(),
                                "§7В сети: §cнет",
                                "",
                                "§7Последний вход:",
                                " §f" + NumberUtil.getTime(System.currentTimeMillis() - offlinePlayer.getPlayerOfflineData().getLastOnline().getTime()) + " назад",
                                "")

                        .build());
            }

            addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                    .setDisplayName("§aОбщая информация")
                    .addLore("§7Прослеживаемая группа: " + currentGroup.getColouredName(),
                            "§7Общее количество персонала: §e" + staffPlayersCollection.size(),
                            "§7Персонал онлайн: §e" + staffPlayersCollection.stream().filter(CorePlayer::isOnline).count())
                    .build());

            if (staffPlayersCollection.isEmpty()) {
                addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                        .setDisplayName("§cУпс, ничего не найдено :c")
                        .addLore("§7Попробуйте зайти сюда немного позже!")
                        .build());
            }

            addItem(41, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyNTkwMmIzODllZDZjMTQ3NTc0ZTQyMmRhOGY4ZjM2MWM4ZWI1N2U3NjMxNjc2YTcyNzc3ZTdiMWQifX19")
                            .setDisplayName("§eВернуться на главную")
                            .addLore("§7Нажмите, чтобы вернуться на главную страницу")
                            .build(),

                    (corePlayer1, mouseAction) -> previousInventory.openInventory(corePlayer));
        }
    }

}
