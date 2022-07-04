package net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.enchantment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.enchantment.type.EnchantmentType;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class EnchantmentData {

    private EnchantmentType enchantmentType;
    private int enchantmentLevel;
}
