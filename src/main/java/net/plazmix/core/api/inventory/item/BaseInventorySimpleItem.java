package net.plazmix.core.api.inventory.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.BaseInventoryItem;
import net.plazmix.core.api.inventory.itemstack.ItemStack;

@AllArgsConstructor
@Getter
public class BaseInventorySimpleItem implements BaseInventoryItem {

    @Setter
    private int slot;

    private final ItemStack itemStack;

    @Override
    public void onDraw(@NonNull BaseInventory baseInventory) {
        // не важно
    }

}
