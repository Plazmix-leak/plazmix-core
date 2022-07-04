package net.plazmix.quiter.inventory;

import lombok.NonNull;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.quiter.QuiterMessageCategory;

public class QuiterInventory extends CorePaginatedInventory {

    private final LocalizationResource localizationResource;

    public QuiterInventory(LocalizationResource localizationResource) {
        super(5, localizationResource.getMessage("QUITER_MENU_TITLE"));

        this.localizationResource = localizationResource;
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {
        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        for (QuiterMessageCategory category : QuiterMessageCategory.values()) {
            addItem(category.getInventorySlot(), category.getItemStack(player), (player1, event) -> {

                new QuiterSelectMessageInventory(localizationResource, category).openInventory(player);
            });
        }

        addItem(41, ItemBuilder.newBuilder(Material.BARRIER)
                        .setDisplayName("§cЗакрыть меню")
                        .addLore("§7▸ Нажмите здесь, чтобы закрыть меню!")

                        .build(),

                (player1, event) -> player.closeInventory());
    }

}
