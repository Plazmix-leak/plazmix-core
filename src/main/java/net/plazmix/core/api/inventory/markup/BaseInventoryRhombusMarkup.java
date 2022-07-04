package net.plazmix.core.api.inventory.markup;

import net.plazmix.core.api.utility.NumberUtil;

public class BaseInventoryRhombusMarkup extends BaseInventorySimpleMarkup {

    public BaseInventoryRhombusMarkup(int inventoryRows, int firstRowIndex, int tabSize) {
        super(inventoryRows);

        addHorizontalRow(firstRowIndex, tabSize + 1);

        for (int rowIndex : NumberUtil.toManyArray(firstRowIndex + 1, inventoryRows - 2)) {
            addHorizontalRow(rowIndex, tabSize);
        }

        addHorizontalRow(inventoryRows - 1, tabSize + 1);
    }

    public BaseInventoryRhombusMarkup(int inventoryRows, int firstRowIndex) {
        this(inventoryRows, firstRowIndex, 1);
    }

    public BaseInventoryRhombusMarkup(int inventoryRows) {
        this(inventoryRows, 2, 1);
    }
}
