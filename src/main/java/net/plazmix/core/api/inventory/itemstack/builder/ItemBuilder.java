package net.plazmix.core.api.inventory.itemstack.builder;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.inventory.itemstack.ItemFlag;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.enchantment.EnchantmentType;
import net.plazmix.core.api.utility.Placeholders;
import net.plazmix.core.common.language.LanguageManager;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.List;

@RequiredArgsConstructor
public final class ItemBuilder {

    private final ItemStack itemStack;


    public static ItemBuilder newBuilder(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }

    public static ItemBuilder newBuilder(@NonNull Material material) {
        return new ItemBuilder(material);
    }

    private ItemBuilder(@NonNull Material material) {
        this.itemStack = new ItemStack(material);
    }


    public ItemBuilder setMaterial(Material material) {
        this.itemStack.setMaterial(material);

        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);

        return this;
    }

    public ItemBuilder setDurability(int durability) {
        this.itemStack.setDurability(durability);

        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.itemStack.setDisplayName(displayName);

        return this;
    }

    public ItemBuilder setTranslatedName(@NonNull LanguageType translation, @NonNull String key, Placeholders placeholders) {
        String text = translation.getResource().getMessage(key);

        if (placeholders != null && text != null) {
            text = placeholders.applyFor(text);
        }

        return setDisplayName(text);
    }

    public ItemBuilder setTranslatedName(@NonNull CorePlayer player, @NonNull String key, Placeholders placeholders) {
        return setTranslatedName(LanguageManager.INSTANCE.getPlayerLanguage(player.getName()), key, placeholders);
    }


    public ItemBuilder setTranslatedLore(@NonNull LanguageType translation, @NonNull String key, Placeholders placeholders) {
        List<String> lore = translation.getResource().getMessageList(key);

        if (placeholders != null && lore != null) {
            lore.replaceAll(placeholders::applyFor);
        }

        return setLore(lore);
    }

    public ItemBuilder setTranslatedLore(@NonNull CorePlayer player, @NonNull String key, Placeholders placeholders) {
        return setTranslatedLore(LanguageManager.INSTANCE.getPlayerLanguage(player.getName()), key, placeholders);
    }

    public ItemBuilder setPlayerSkull(String playerSkull) {
        this.itemStack.setPlayerSkull(playerSkull);

        return this;
    }

    public ItemBuilder addLore(String... lore) {
        this.itemStack.addLore(lore);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemStack.getLoreList().clear();
        itemStack.getLoreList().addAll(lore);

        return this;
    }

    public ItemBuilder addFlag(ItemFlag itemFlag) {
        this.itemStack.addFlag(itemFlag);

        return this;
    }

    public ItemBuilder addEnchant(EnchantmentType enchantmentType, int enchantmentLevel) {
        if (enchantmentType == null) {
            return this;
        }

        this.itemStack.addEnchant(enchantmentType, enchantmentLevel);
        return this;
    }


    public ItemStack build() {
        return itemStack;
    }

}
