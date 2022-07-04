package net.plazmix.core.api.inventory.itemstack.enchantment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class EnchantmentData {

    private EnchantmentType enchantmentType;
    private int enchantmentLevel;
}
