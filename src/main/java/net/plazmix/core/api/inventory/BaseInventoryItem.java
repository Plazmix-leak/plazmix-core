package net.plazmix.core.api.inventory;

import lombok.NonNull;
import net.plazmix.core.api.inventory.itemstack.ItemStack;

public interface BaseInventoryItem {

    int getSlot();

    void setSlot(int itemSlot);

    ItemStack getItemStack();


    void onDraw(@NonNull BaseInventory baseInventory);
}
