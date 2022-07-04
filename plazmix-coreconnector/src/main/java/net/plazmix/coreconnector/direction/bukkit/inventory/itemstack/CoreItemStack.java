package net.plazmix.coreconnector.direction.bukkit.inventory.itemstack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.enchantment.EnchantmentData;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.enchantment.type.EnchantmentType;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.flag.ItemFlag;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.material.Material;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.utility.ReflectionUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public final class CoreItemStack implements Cloneable {

    public static CoreItemStack parse(@NonNull ByteBuf byteBuf) {
        Material material = Material.ALL_TYPES[BufferedQuery.readVarInt(byteBuf)];

        String displayName = BufferedQuery.readString(byteBuf);
        String playerSkull = BufferedQuery.readString(byteBuf);

        int durability = BufferedQuery.readVarInt(byteBuf);
        int amount = BufferedQuery.readVarInt(byteBuf);

        List<String> loreList = BufferedQuery.readStringArray(byteBuf);

        Set<EnchantmentData> enchantSet = BufferedQuery.readStringArray(byteBuf)
                .stream()
                .map(enchantmentString -> new EnchantmentData(EnchantmentType.valueOf(enchantmentString.split(":")[0]), Integer.parseInt(enchantmentString.split(":")[1])))
                .collect(Collectors.toSet());

        Set<ItemFlag> itemFlagSet = BufferedQuery.readStringArray(byteBuf)
                .stream()
                .map(ItemFlag::valueOf)
                .collect(Collectors.toSet());

        // Initialize itemStack
        CoreItemStack itemStack = new CoreItemStack(material, durability, amount);

        itemStack.setDisplayName(displayName);
        itemStack.setPlayerSkull(playerSkull);
        itemStack.setLoreList(loreList);

        itemStack.setEnchantSet(enchantSet);
        itemStack.setItemFlagSet(itemFlagSet);

        return itemStack;
    }

    private final Material material;

    private String displayName;
    private String playerSkull = "";

    private int durability;
    private int amount = 1;

    private List<String> loreList = new ArrayList<>();

    private Set<EnchantmentData> enchantSet = new HashSet<>();
    private Set<ItemFlag> itemFlagSet = new HashSet<>();

    public CoreItemStack(Material material, int durability, int amount) {
        this.material = material;
        this.durability = durability;
        this.amount = amount;
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

    public ItemStack toBukkitItem() {
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(org.bukkit.Material.getMaterial(material.name())), amount, (byte) durability);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(loreList);

        if (material.equals(Material.SKULL_ITEM) && durability == 3) {
            SkullMeta skullMeta = ((SkullMeta) itemMeta);

            if (playerSkull.length() <= 25) {
                PlayerSkin playerSkin = NetworkModule.getInstance().getSkinsModule().getCurrentPlayerSkin(playerSkull);

                // если ник лицензионный
                if (playerSkin != null) {

                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
                    gameProfile.getProperties().put("textures", new Property("textures", playerSkin.getSkinObject().getValue(), playerSkin.getSkinObject().getSignature()));

                    ReflectionUtil.setField(skullMeta, "profile", gameProfile);

                } else {

                    skullMeta.setOwner(playerSkull);
                }

            } else {

                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
                gameProfile.getProperties().put("textures", new Property("textures", playerSkull));

                ReflectionUtil.setField(skullMeta, "profile", gameProfile);
            }
        }

        for (EnchantmentData enchantmentData : enchantSet) {
            itemStack.addEnchantment(Objects.requireNonNull(Enchantment.getByName(enchantmentData.getEnchantmentType().name())), enchantmentData.getEnchantmentLevel());
        }

        for (ItemFlag itemFlag : itemFlagSet) {
            itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.valueOf(itemFlag.name()));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    @SneakyThrows
    @Override
    public CoreItemStack clone() {
        return (CoreItemStack) super.clone();
    }
}
