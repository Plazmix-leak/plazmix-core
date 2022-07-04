package net.plazmix.quiter.inventory;

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
import net.plazmix.quiter.QuitMessage;
import net.plazmix.quiter.QuiterManager;
import net.plazmix.quiter.QuiterMessageCategory;

import java.util.Arrays;
import java.util.List;

public class QuiterSelectMessageInventory extends CorePaginatedInventory {

    private final LocalizationResource localizationResource;
    private final QuiterMessageCategory quiterMessageCategory;

    public QuiterSelectMessageInventory(LocalizationResource localizationResource, QuiterMessageCategory quiterMessageCategory) {
        super(5, localizationResource.getMessage("QUITER_MENU_TITLE"));

        this.localizationResource = localizationResource;
        this.quiterMessageCategory = quiterMessageCategory;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        for (int messageId : Arrays.stream(quiterMessageCategory.getMessagesMap().keySet().toArray()).sorted().toArray()) {
            QuitMessage quitMessage = quiterMessageCategory.getJoinMessage(messageId);

            addItemToMarkup(ItemBuilder.newBuilder(quitMessage.isPurchased(player) ? (quitMessage.isSelected(player) ? Material.WATER_BUCKET : Material.BUCKET) : Material.STAINED_GLASS_PANE)
                            .setDurability(quitMessage.isPurchased(player) ? 0 : quitMessage.canPurchase(player) ? 4 : 14)
                            .setDisplayName((quitMessage.isSelected(player) ? ChatColor.YELLOW : quitMessage.isPurchased(player) ? ChatColor.GREEN : ChatColor.RED) + "Сообщение #" + messageId)

                            .addLore("")
                            .addLore("§7Пример сообщения: ")
                            .addLore(" " + quitMessage.parse(player))

                            .addLore("")
                            .addLore("§7Цена: §6" + quitMessage.getGoldsCost() + " плазмы")

                            .addLore("")
                            .addLore(quitMessage.isSelected(player) ? "§e▸ Сообщение выбрано!" : quitMessage.isPurchased(player) ? "§a▸ Нажмите, чтобы выбрать!" : quitMessage.canPurchase(player) ? "§a▸ Нажмите, чтобы приобрести!" : "§cУ Вас недостаточно плазмы для покупки!")

                            .build(),

                    (player1, event) -> {

                        if (quitMessage.isPurchased(player) && !quitMessage.isSelected(player)) {
                            QuiterManager.INSTANCE.setSelectedMessage(messageId, quiterMessageCategory.ordinal(), player.getName());
                            player.sendLangMessage("QUITER_SELECT_MESSAGE", "%id%", NumberUtil.spaced(messageId), "%category%", quiterMessageCategory.getDisplayName());
                            updateInventory(player);
                            return;
                        }

                        if (quitMessage.isSelected(player)) {
                            player.sendMessage("§d§lPlazmix §8:: §cОшибка, сообщение уже выбрано!");
                            return;
                        }

                        if (!quitMessage.canPurchase(player)) {
                            return;
                        }

                        QuiterManager.INSTANCE.addPurchasedMessage(messageId, quiterMessageCategory.ordinal(), player.getName());
                        EconomyManager.INSTANCE.changePlayerPlazma(player.getName(), player.getPlazma() - quitMessage.getGoldsCost());

                        player.sendLangMessage("QUITER_BUY_MESSAGE",
                                "%id%", NumberUtil.spaced(messageId),
                                "%category%", quiterMessageCategory.getDisplayName(),
                                "%cost%", NumberUtil.spaced(quitMessage.getGoldsCost()));

                        updateInventory(player);
                    });
        }

        addItem(41, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                        .setTranslatedName(player,"BACK_TITLE",null)
                        .setTranslatedLore(player, "BACK_LINES", null)
                        .build(),

                (player1, inventoryClickEvent) -> new QuiterInventory(localizationResource).openInventory(player));
    }

}
