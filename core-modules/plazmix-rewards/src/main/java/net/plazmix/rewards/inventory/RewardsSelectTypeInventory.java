package net.plazmix.rewards.inventory;

import lombok.NonNull;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.connection.player.CorePlayer;

public final class RewardsSelectTypeInventory extends CoreSimpleInventory {

    public RewardsSelectTypeInventory() {
        super(5, "Награды");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        addItem(22, ItemBuilder.newBuilder(Material.STORAGE_MINECART)
                        .setDisplayName("§eЕжедневные награды")
                        .addLore("")
                        .addLore("§7Данный список содержит в себе")
                        .addLore("§7список наград, которые можно забирать")
                        .addLore("§7каждый день")
                        .addLore("")
                        .addLore("§e▸ Нажмите, чтобы открыть!")
                        .build(),

                (baseInventory, inventoryClickEvent) -> new DailyRewardsInventory().openInventory(player));

        addItem(24, ItemBuilder.newBuilder(Material.DIAMOND)
                        .setDisplayName("§eСезонные награды")
                        .addLore("")
                        .addLore("§7Данный список содержит в себе")
                        .addLore("§7список сезонных наград, которые")
                        .addLore("§7обновляются каждый сезон")
                        .addLore("")
                        .addLore("§e▸ Нажмите, чтобы открыть!")
                        .build(),

                (baseInventory, inventoryClickEvent) -> player.dispatchCommand("spacepass"));
    }

}
