package net.plazmix.core.api.inventory.impl;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.impl.InventoryOpenEvent;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.BaseInventoryItem;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryClickHandler;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryDisplayableHandler;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryUpdateHandler;
import net.plazmix.core.api.inventory.item.BaseInventoryClickItem;
import net.plazmix.core.api.inventory.item.BaseInventorySimpleItem;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.update.BaseInventoryUpdateTask;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SInventoryOpenPacket;
import net.plazmix.core.connection.protocol.server.SInventoryClearPacket;
import net.plazmix.core.connection.protocol.server.SInventorySetItemPacket;

import java.util.LinkedList;
import java.util.List;

@Getter
public abstract class CorePaginatedInventory implements BaseInventory {

    protected final int inventoryRows;
    protected final String inventoryTitle;

    protected int currentPage;

    protected BaseInventoryInfo inventoryInfo;
    protected BaseInventoryMarkup inventoryMarkup;

    protected final List<BaseInventoryItem> pageButtonList = new LinkedList<>();

    protected final BaseInventorySettings inventorySettings = new BaseInventorySettings();


    public CorePaginatedInventory(int inventoryRows, @NonNull String inventoryTitle) {
        this.inventoryRows = inventoryRows;
        this.inventoryTitle = inventoryTitle;

        this.inventoryInfo = new BaseInventoryInfo(this, inventoryTitle, inventoryRows * 9, inventoryRows);
    }

    protected void backwardPage(@NonNull CorePlayer player) {
        if (currentPage - 1 < 0) {
            throw new RuntimeException(String.format("Page cannot be < 0 (%s - 1 < 0)", currentPage));
        }

        this.currentPage--;
        openInventory(player);
    }

    protected void forwardPage(@NonNull CorePlayer player, int allPagesCount) {
        if (currentPage >= allPagesCount) {
            throw new RuntimeException(String.format("Page cannot be >= max pages count (%s >= %s)", currentPage, allPagesCount));
        }

        this.currentPage++;
        openInventory(player);
    }

    protected void drawPage(@NonNull CorePlayer player) {
        pageButtonList.clear();

        if (inventoryMarkup != null) {
            inventoryMarkup.getMarkupList().clear();
        }

        clearInventory(player);
        drawInventory(player);

        int pagesCount = pageButtonList.size() / inventoryMarkup.getMarkupList().size();

        if (!(currentPage >= pagesCount)) {
            addItem(inventoryInfo.getInventorySize() - 3, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)
                            .setDisplayName("§eСледующая страница")
                            .addLore("§7Нажмите, чтобы открыть следующую страницу!")

                            .build(),

                    (inventory, event) -> forwardPage(player, pagesCount));
        }

        if (!(currentPage - 1 < 0)) {
            addItem(inventoryInfo.getInventorySize() - 5, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)
                            .setDisplayName("§eПредыдущая страница")
                            .addLore("§7Нажмите, чтобы открыть предыдущую страницу!")

                            .build(),

                    (inventory, event) -> backwardPage(player));
        }

        for (int i = 0; i < inventoryMarkup.getMarkupList().size(); i++) {
            int itemIndex = currentPage * inventoryMarkup.getMarkupList().size() + i;

            if (pageButtonList.size() > itemIndex) {
                int buttonSlot = inventoryMarkup.getMarkupList().get(i);

                BaseInventoryItem baseInventoryItem = pageButtonList.get(itemIndex);
                baseInventoryItem.setSlot(buttonSlot);

                addItem(baseInventoryItem);
            }
        }
    }

    @Override
    public void openInventory(@NonNull CorePlayer player) {
        PlazmixCore.getInstance().getInventoryManager().addInventory(player, this);

        // send packet to the server.
        SInventoryOpenPacket inventoryOpenPacket = new SInventoryOpenPacket(player.getName(), inventoryTitle, inventoryRows);
        player.getBukkitServer().sendPacket(inventoryOpenPacket);

        drawPage(player);

        for (BaseInventoryItem inventoryItem : inventoryInfo.getInventoryItemMap().valueCollection()) {
            player.getBukkitServer().sendPacket(new SInventorySetItemPacket(player.getName(), inventoryItem.getSlot(), inventoryItem.getItemStack()));
        }

        // call the event.
        InventoryOpenEvent inventoryOpenEvent = new InventoryOpenEvent(player, this);
        PlazmixCore.getInstance().getEventManager().callEvent(inventoryOpenEvent);
    }

    @Override
    public void openInventory(@NonNull CorePlayer player, @NonNull BaseInventoryDisplayableHandler inventoryDisplayableHandler) {
        addHandler(BaseInventoryDisplayableHandler.class, inventoryDisplayableHandler);

        openInventory(player);
    }

    @Override
    public void clearInventory(@NonNull CorePlayer player) {
        inventoryInfo.getInventoryItemMap().clear();

        SInventoryClearPacket inventoryProcessPacket = new SInventoryClearPacket(player.getName());
        player.getBukkitServer().sendPacket(inventoryProcessPacket);
    }

    @Override
    public void updateInventory(@NonNull CorePlayer player) {

        // Да я ебал в рот это фиксить, реально, заебался...
        openInventory(player);
    }

    @Override
    public void updateInventory(@NonNull CorePlayer player, @NonNull BaseInventoryUpdateHandler inventoryUpdateHandler) {
        updateInventory(player);

        inventoryUpdateHandler.onUpdate(this, player);
    }

    @Override
    public void enableAutoUpdate(@NonNull CorePlayer player, BaseInventoryUpdateHandler inventoryUpdateHandler, long secondDelay) {
        PlazmixCore.getInstance().getInventoryManager().addInventoryUpdateTask(this, new BaseInventoryUpdateTask(this, secondDelay, () -> {

            if (inventoryUpdateHandler != null)
                updateInventory(player, inventoryUpdateHandler);
            else
                updateInventory(player);
        }));
    }

    @Override
    public void closeInventory(@NonNull CorePlayer player) {
        if (player.getOpenedInventory() == null) {
            return;
        }

        player.closeInventory();

        PlazmixCore.getInstance().getInventoryManager().removeInventory(player, this);
        PlazmixCore.getInstance().getInventoryManager().removeInventoryUpdateTask(this);
    }

    @Override
    public void addItem(@NonNull BaseInventoryItem baseInventoryItem) {
        baseInventoryItem.onDraw(this);
        inventoryInfo.addItem(baseInventoryItem.getSlot(), baseInventoryItem);
    }

    @Override
    public void setInventoryMarkup(@NonNull BaseInventoryMarkup baseInventoryMarkup) {
        this.inventoryMarkup = baseInventoryMarkup;
    }


    public void addItem(int itemSlot, @NonNull ItemStack itemStack) {
        addItem(new BaseInventorySimpleItem(itemSlot, itemStack));
    }

    public void addItem(int itemSlot, @NonNull ItemStack itemStack, @NonNull BaseInventoryClickHandler inventoryClickHandler) {
        addItem(new BaseInventoryClickItem(itemSlot, itemStack, inventoryClickHandler));
    }


    public void addItemToMarkup(@NonNull BaseInventoryItem baseInventoryItem) {
        pageButtonList.add(baseInventoryItem);
    }

    public void addItemToMarkup(@NonNull ItemStack itemStack) {
        addItemToMarkup(new BaseInventorySimpleItem(0, itemStack));
    }

    public void addItemToMarkup(@NonNull ItemStack itemStack, @NonNull BaseInventoryClickHandler inventoryClickHandler) {
        addItemToMarkup(new BaseInventoryClickItem(0, itemStack, inventoryClickHandler));
    }

    @Override
    public abstract void drawInventory(@NonNull CorePlayer player);

}
