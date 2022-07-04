package net.plazmix.rewards.inventory;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.dailyreward.DailyPlayer;
import net.plazmix.core.common.dailyreward.DailyReward;
import net.plazmix.core.common.dailyreward.DailyRewardInfo;
import net.plazmix.core.common.dailyreward.DailyRewardManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.concurrent.TimeUnit;

public final class DailyRewardsInventory extends CoreSimpleInventory {

    public static final String PASSED_REWARD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkMmY5MzQ3NmFiNjlmYWY1YTUxOWViNTgzMmRiODQxYzg1MjY2ZTAwMWRlNWIyNmU0MjdmNDFkOThlNWM3ZSJ9fX0=";
    public static final String AVAILABLE_REWARD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNiMjI4ZjcwYTM1ZDBhYTMyMzUwNDY3ZDllOGMwOWFhZTlhZTBhZTA4NzVmZGM4YzMxMWE4NzZiZTE5MDcxNyJ9fX0=";
    public static final String NO_OPENED_REWARD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJlNzRiYjBkOWI1MmFkYmNjNzMyMDBkNDk0MGVhNWIwZWRjM2NhMmFhZDIwOTgwMzgzMGYxOWI0MTNlZTE2ZiJ9fX0=";


    public DailyRewardsInventory() {
        super(5, "Ежедневные награды");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        DailyPlayer dailyPlayer = DailyPlayer.of(player.getName());

        // Initialize inventory markup.
        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(2, 1);
        inventoryMarkup.addHorizontalRow(3, 1);
        inventoryMarkup.addHorizontalRow(4, 1);


        // Set items to markup.
        int dailyItemCounter = 0;
        DailyRewardInfo previousRewardInfo = null;

        for (DailyRewardInfo dailyRewardInfo : DailyRewardManager.INSTANCE.getDailyRewardInfos()) {
            if (dailyItemCounter >= inventoryMarkup.getMarkupList().size())
                break;

            ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM);
            itemBuilder.setDurability(3);
            itemBuilder.setAmount(dailyRewardInfo.getDayIndex());

            itemBuilder.addLore("");
            itemBuilder.addLore("§7Ежедневная награда, которая");
            itemBuilder.addLore("§7содержит в себе:");

            for (DailyReward dailyReward : dailyRewardInfo.getDailyRewards()) {
                itemBuilder.addLore(" §8• §6" + dailyReward.getDisplay());
            }

            itemBuilder.addLore("");

            // Награда уже взята игроком.
            if (dailyPlayer.getLastRewardDay() >= dailyRewardInfo.getDayIndex()) {

                itemBuilder.setDisplayName(ChatColor.RED + "День " + dailyRewardInfo.getDayIndex());
                itemBuilder.setPlayerSkull(PASSED_REWARD_TEXTURE);

                itemBuilder.addLore(ChatColor.RED + "▸ Данная награда уже взята!");
            }

            // Награда доступна для получения.
            else if (isCanReceive(dailyPlayer, dailyRewardInfo)) {

                itemBuilder.setDisplayName(ChatColor.GREEN + "День " + dailyRewardInfo.getDayIndex());
                itemBuilder.setPlayerSkull(AVAILABLE_REWARD_TEXTURE);

                itemBuilder.addLore(ChatColor.GREEN + "▸ Нажмите, чтобы получить!");
            }

            // Награда НЕдоступна для получения.
            else {
                itemBuilder.setDisplayName(ChatColor.AQUA + "День " + dailyRewardInfo.getDayIndex());
                itemBuilder.setPlayerSkull(NO_OPENED_REWARD_TEXTURE);

                if (previousRewardInfo != null && dailyPlayer.getLastRewardDay() >= previousRewardInfo.getDayIndex()) {

                    itemBuilder.addLore("§7Можно будет получить через:");
                    itemBuilder.addLore(" §e" + NumberUtil.getTime((dailyPlayer.getLastRewardTimestamp().getTime() + TimeUnit.DAYS.toMillis(dailyRewardInfo.getDayIndex() - dailyPlayer.getLastRewardDay())) - System.currentTimeMillis()));
                    itemBuilder.addLore("");
                }

                itemBuilder.addLore("§8▸ §3Время получения еще не наступило");
            }

            addItem(inventoryMarkup.getMarkupList().get(dailyItemCounter), itemBuilder.build(),
                    (baseInventory, inventoryClickEvent) -> {

                        if (dailyRewardInfo.getDayIndex() >= dailyPlayer.getLastRewardDay() && (System.currentTimeMillis() - dailyPlayer.getLastRewardTimestamp().getTime() >= TimeUnit.DAYS.toMillis(1)) && dailyRewardInfo.getDayIndex() == (dailyPlayer.getLastRewardDay() + 1)) {
                            dailyPlayer.addReward(dailyRewardInfo);

                            player.closeInventory();
                        }
                    });

            dailyItemCounter++;
            previousRewardInfo = dailyRewardInfo;
        }
    }

    private boolean isCanReceive(DailyPlayer dailyPlayer, DailyRewardInfo dailyRewardInfo) {
        return dailyRewardInfo.getDayIndex() >= dailyPlayer.getLastRewardDay() && (System.currentTimeMillis() - dailyPlayer.getLastRewardTimestamp().getTime() >= TimeUnit.DAYS.toMillis(1)) && dailyRewardInfo.getDayIndex() == (dailyPlayer.getLastRewardDay() + 1);
    }

}
