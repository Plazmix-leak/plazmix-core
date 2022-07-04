package net.plazmix.joiner.inventory;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.joiner.JoinMessage;
import net.plazmix.joiner.JoinMessageCategory;
import net.plazmix.joiner.JoinerManager;

import java.util.Arrays;
import java.util.List;

public class JoinerSelectMessageInventory extends CorePaginatedInventory {

    private final LocalizationResource localizationResource;
    private final JoinMessageCategory joinMessageCategory;

    public JoinerSelectMessageInventory(LocalizationResource localizationResource, JoinMessageCategory joinMessageCategory) {
        super(5, localizationResource.getMessage("JOINER_MENU_TITLE"));

        this.localizationResource = localizationResource;
        this.joinMessageCategory = joinMessageCategory;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        for (int messageId : Arrays.stream(joinMessageCategory.getMessagesMap().keySet().toArray()).sorted().toArray()) {
            JoinMessage joinMessage = joinMessageCategory.getJoinMessage(messageId);

            addItemToMarkup(ItemBuilder.newBuilder(joinMessage.isPurchased(player) ? (joinMessage.isSelected(player) ? Material.WATER_BUCKET : Material.BUCKET) : Material.STAINED_GLASS_PANE)
                            .setDurability(joinMessage.isPurchased(player) ? 0 : joinMessage.canPurchase(player) ? 4 : 14)
                            .setDisplayName((joinMessage.isSelected(player) ? ChatColor.YELLOW : joinMessage.isPurchased(player) ? ChatColor.GREEN : ChatColor.RED) + "Сообщение #" + messageId)

                            .addLore("")
                            .addLore("§7Пример сообщения: ")
                            .addLore(" " + joinMessage.parse(player))

                            .addLore("")
                            .addLore("§7Цена: §6" + joinMessage.getGoldsCost() + " плазмы")

                            .addLore("")
                            .addLore(joinMessage.isSelected(player) ? "§e▸ Сообщение выбрано!" : joinMessage.isPurchased(player) ? "§a▸ Нажмите, чтобы выбрать!" : joinMessage.canPurchase(player) ? "§a▸ Нажмите, чтобы приобрести!" : "§cУ Вас недостаточно плазмы для покупки!")

                            .build(),

                    (player1, event) -> {

                        if (joinMessage.isPurchased(player) && !joinMessage.isSelected(player)) {
                            JoinerManager.INSTANCE.setSelectedMessage(messageId, joinMessageCategory.ordinal(), player.getName());
                            player.sendLangMessage("JOINER_SELECT_MESSAGE", "%id%", NumberUtil.spaced(messageId), "%category%", joinMessageCategory.getDisplayName());
                            updateInventory(player);
                            return;
                        }

                        if (joinMessage.isSelected(player)) {
                            player.sendMessage("§d§lPlazmix §8:: §cОшибка, сообщение уже выбрано!");
                            return;
                        }

                        if (!joinMessage.canPurchase(player)) {
                            return;
                        }

                        JoinerManager.INSTANCE.addPurchasedMessage(messageId, joinMessageCategory.ordinal(), player.getName());
                        EconomyManager.INSTANCE.changePlayerPlazma(player.getName(), player.getPlazma() - joinMessage.getGoldsCost());

                        player.sendLangMessage("JOINER_BUY_MESSAGE",
                                "%id%", NumberUtil.spaced(messageId),
                                "%category%", joinMessageCategory.getDisplayName(),
                                "%cost%", NumberUtil.spaced(joinMessage.getGoldsCost()));

                        updateInventory(player);
                    });
        }

        addItem(41, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                        .setTranslatedName(player,"BACK_TITLE", null)
                        .setTranslatedLore(player, "BACK_LINES", null)
                        .build(),

                (player1, inventoryClickEvent) -> new JoinerInventory(localizationResource).openInventory(player));
    }

}
