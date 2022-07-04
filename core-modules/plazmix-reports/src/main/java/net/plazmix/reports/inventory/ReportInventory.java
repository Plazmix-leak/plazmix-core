package net.plazmix.reports.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.DateUtil;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.report.ReportManager;
import net.plazmix.core.common.report.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportInventory extends CorePaginatedInventory {

    public ReportInventory() {
        super(5, "Жалобы");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        int intruderCount = 0;
        int reportCount = 0;

        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        for (String intruderName : ReportManager.INSTANCE.getReportMap().keySet()) {

            CorePlayer intruderPlayer = PlazmixCore.getInstance().getOfflinePlayer(intruderName);
            List<String> itemLore = new ArrayList<>();

            itemLore.add("");
            itemLore.add("§7Нарушитель: " + intruderPlayer.getDisplayName() + (intruderPlayer.isOnline() ? " §a(В сети)" : " §c(Не в сети)"));

            if (intruderPlayer.isOnline()) {
                itemLore.add("§7Текущий сервер: §f" + intruderPlayer.getBukkitServer().getName());

                itemLore.add("");
                itemLore.add("§7Всего жалоб: §e" + ReportManager.INSTANCE.getReportsByIntruder(intruderName).size());
                itemLore.add("");
                itemLore.add("§7Список тех, кто пожаловался:");

                for (Report report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                    CorePlayer offlineOwnerPlayer = PlazmixCore.getInstance().getOfflinePlayer(report.getReportOwner());

                    itemLore.add(" §8- " + offlineOwnerPlayer.getDisplayName() + "§7: " + offlineOwnerPlayer.getGroup().getSuffix() + report.getReportReason() + " §8(" + DateUtil.formatTime(report.getReportDate(), DateUtil.DEFAULT_DATETIME_PATTERN) + ")");
                    reportCount++;
                }

                itemLore.add("");
                itemLore.add("§e▸ Нажмите ЛКМ, чтобы телепортироваться на сервер нарушителя!");
                itemLore.add("§e▸ Нажмите ПКМ, чтобы отменить и удалить жалобу");
                itemLore.add("§e▸ Нажмите Q, чтобы наказать нарушителя!");

                addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                                .setDurability(3)
                                .setPlayerSkull(intruderName)

                                .setDisplayName("§eЖалоба #" + (intruderCount + 1))
                                .addLore(itemLore.toArray(new String[0]))

                                .build(),

                        (corePlayer1, inventoryClickEvent) -> {
                            corePlayer.closeInventory();

                            switch (inventoryClickEvent.getMouseAction()) {
                                case LEFT: {

                                    if (intruderPlayer.getName().equalsIgnoreCase(corePlayer.getName())) {
                                        corePlayer.sendMessage("§d§lЖалобы §8:: §cОшибка, вы не можете следить за собой!");
                                        break;
                                    }

                                    if (!intruderPlayer.isOnline()) {
                                        corePlayer.sendLangMessage("PLAYER_OFFLINE");
                                        break;
                                    }

                                    PlazmixCore.getInstance().getCommandManager().dispatchCommand(corePlayer, "watch " + intruderName);

                                    break;
                                }

                                case RIGHT: {
                                    corePlayer.sendMessage("§d§lЖалобы §8:: §fЖалобы на игрока " + intruderPlayer.getDisplayName() + " §fбыли отменены");

                                    for (Report report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                                        CorePlayer offlineOwnerPlayer = PlazmixCore.getInstance().getOfflinePlayer(report.getReportOwner());

                                        offlineOwnerPlayer.sendMessage("§d§lЖалобы §8:: " + corePlayer.getDisplayName() + " §fрассмотрел Вашу жалобу на игрока " +
                                                intruderPlayer.getDisplayName() + " §fи признал его §bНЕВИНОВНЫМ");
                                    }

                                    for (CorePlayer onlineStaff : PlazmixCore.getInstance().getOnlinePlayers(corePlayer2 -> corePlayer2.getGroup().isStaff()))
                                        onlineStaff.sendMessage("§d§lЖалобы §8:: " + corePlayer.getDisplayName() + " §fотменил все жалобы на игрока " + intruderPlayer.getDisplayName());

                                    ReportManager.INSTANCE.getReportMap().removeAll(intruderName.toLowerCase());
                                    break;
                                }

                                case DROP: {
                                    PunishmentManager.INSTANCE.tempBanPlayer(corePlayer, intruderPlayer, "Использование читов (Жалоба)", TimeUnit.DAYS.toMillis(30));

                                    for (CorePlayer onlineStaff : PlazmixCore.getInstance().getOnlinePlayers(corePlayer2 -> corePlayer2.getGroup().isStaff()))
                                        onlineStaff.sendMessage("§d§lЖалобы §8:: " + corePlayer.getDisplayName() + " §fнаказал " + intruderPlayer.getDisplayName() + " §fиз-за только что рассмотренной жалобы!");

                                    for (Report report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                                        CorePlayer offlineOwnerPlayer = PlazmixCore.getInstance().getOfflinePlayer(report.getReportOwner());

                                        offlineOwnerPlayer.sendMessage("§d§lЖалобы §8:: " + corePlayer.getDisplayName() + " §fрассмотрел Вашу жалобу на игрока" + intruderPlayer.getDisplayName() + " §fи признал его §cВИНОВНЫМ");
                                        offlineOwnerPlayer.sendMessage(" §e+1 плазма");

                                        offlineOwnerPlayer.addPlazma(1);
                                    }

                                    ReportManager.INSTANCE.getReportMap().removeAll(intruderName.toLowerCase());
                                    break;
                                }
                            }
                        });

                intruderCount++;
            }
        }

        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§aОбщая информация")
                .addLore("§7Всего нарушителей: §e" + intruderCount)
                .addLore("§7Всего жалоб: §e" + reportCount)
                .build());

        if (intruderCount == 0 || reportCount == 0) {

            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cУпс, ничего не найдено :c")
                    .build());
        }
    }
}
