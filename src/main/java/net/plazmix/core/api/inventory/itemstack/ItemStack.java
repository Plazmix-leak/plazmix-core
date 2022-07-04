package net.plazmix.core.api.inventory.itemstack;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plazmix.core.api.inventory.itemstack.enchantment.EnchantmentData;
import net.plazmix.core.api.inventory.itemstack.enchantment.EnchantmentType;
import net.plazmix.core.protocol.BufferedQuery;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class ItemStack
        implements Cloneable {

    private Material material;

    private String displayName;
    private String playerSkull = "";

    private int durability, amount = 1;

    private List<String> loreList = new ArrayList<>();

    private final Set<EnchantmentData> enchantSet = new HashSet<>();
    private final Set<ItemFlag> itemFlagSet = new HashSet<>();


    public ItemStack(Material material, int durability, int amount) {
        this.material = material;
        this.durability = durability;
        this.amount = amount;
    }

    public ItemStack(Material material, int durability) {
        this (material, durability, 1);
    }

    public ItemStack(Material material) {
        this (material, 0);
    }


    public void addLore(String... lore) {
        loreList.addAll(Arrays.asList(lore));
    }

    public void addEnchant(EnchantmentType enchantmentType, int enchantmentLevel) {
        enchantSet.add(new EnchantmentData(enchantmentType, enchantmentLevel));
    }

    public void addFlag(ItemFlag itemFlag) {
        itemFlagSet.add(itemFlag);
    }

    public void writeBytes(ByteBuf byteBuf) {
        BufferedQuery.writeVarInt(material.ordinal(), byteBuf);

        BufferedQuery.writeString(displayName, byteBuf);
        BufferedQuery.writeString(playerSkull, byteBuf);

        BufferedQuery.writeVarInt(durability, byteBuf);
        BufferedQuery.writeVarInt(amount, byteBuf);

        BufferedQuery.writeStringArray(loreList, byteBuf);

        BufferedQuery.writeStringArray(enchantSet.stream()
                .map(enchantmentData -> enchantmentData.getEnchantmentType().name() + ":" + enchantmentData.getEnchantmentLevel())
                .collect(Collectors.toList()), byteBuf);

        BufferedQuery.writeStringArray(itemFlagSet.stream()
                .map(ItemFlag::name)
                .collect(Collectors.toList()), byteBuf);
    }

    @SneakyThrows
    @Override
    public ItemStack clone() {
        return ((ItemStack) super.clone());
    }

}
