package net.plazmix.guilds.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.guild.CoreGuild;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuildMembersInventory extends CorePaginatedInventory {

    public GuildMembersInventory() {
        super(6, "Участники гильдии");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        CoreGuild coreGuild = CoreGuild.of(corePlayer.getName());

        BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);

        CorePlayer guildLeader = PlazmixCore.getInstance().getOfflinePlayer(coreGuild.getLeaderName());

        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§aОбщая информация")
                .addLore("§7Лидер: " + guildLeader.getDisplayName())
                .addLore("§7Всего участников: §f" + coreGuild.getMemberIdsMap().size())

                .build());

        // Add player to guild members list.
        for (CorePlayer guildMember : coreGuild.getMemberIdsMap().keySet().stream()
                .map(playerId -> PlazmixCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(playerId)))
                .sorted(Collections.reverseOrder(Comparator.comparingInt(player -> coreGuild.getStatus(player.getName()).getLevel())))
                .collect(Collectors.toList())) {

            ItemBuilder itemBuilder = ItemBuilder.newBuilder(guildMember.isOnline() ? Material.SKULL_ITEM : Material.STAINED_GLASS_PANE)
                    .setDurability(guildMember.isOnline() ? 3 : 14)
                    .setPlayerSkull(guildMember.getName());

            itemBuilder.setDisplayName("§7Ник: " + guildMember.getDisplayName());
            itemBuilder.addLore("");

            //itemBuilder.addLore("§7Электронная почта: СКОРО");
            itemBuilder.addLore("§7Ранг: " + coreGuild.getStatus(guildMember.getName()).getDisplayName());

            itemBuilder.addLore("");

            if (guildMember.isOnline()) {
                itemBuilder.addLore("§7Статус: §aв сети");
                itemBuilder.addLore("§7Текущий сервер: §e" + guildMember.getBukkitServer().getName());
                itemBuilder.addLore("");

                itemBuilder.addLore("§7Версия клиента: §f" + guildMember.getMinecraftVersionName());
                itemBuilder.addLore("§7Игровой язык: §c" + guildMember.getLanguageType().getDisplayName());

                itemBuilder.addLore("");
                itemBuilder.addLore("§e▸ Нажмите ЛКМ, чтобы телепортироваться!");

                if (coreGuild.getStatus(guildMember.getName()).getLevel() == 3) {
                    itemBuilder.addLore("§e▸ Нажмите ПКМ, чтобы изменить ранг!");
                }

            } else {

                itemBuilder.addLore("§7Статус: §cне в сети");
                itemBuilder.addLore("§7Последний сервер: §e" + guildMember.getPlayerOfflineData().getLastServerName());
                itemBuilder.addLore("");
                itemBuilder.addLore("§7Последний раз был в сети:");
                itemBuilder.addLore(" §f" + NumberUtil.getTime(System.currentTimeMillis() - guildMember.getPlayerOfflineData().getLastOnline().getTime()) + " назад");
            }

            addItemToMarkup(itemBuilder.build(), (baseInventory, inventoryClickEvent) -> {

                        switch (inventoryClickEvent.getMouseAction()) {
                            case LEFT: {

                                if (guildMember.isOnline()) {
                                    corePlayer.connectToServer(guildMember.getBukkitServer());
                                }

                                break;
                            }

                            case RIGHT: {

                                if (coreGuild.getStatus(guildMember.getName()).getLevel() == 3) {
                                    GiveRankPlayerJson(corePlayer);
                                }

                                break;
                            }
                        }
            });
        }

        ItemStack frameItemStack = ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(1)

                .setDisplayName("§7")
                .build();

        addItem(10, frameItemStack);
        addItem(11, frameItemStack);
        addItem(12, frameItemStack);
        addItem(13, frameItemStack);
        addItem(14, frameItemStack);
        addItem(15, frameItemStack);
        addItem(16, frameItemStack);
        addItem(17, frameItemStack);
        addItem(18, frameItemStack);
        addItem(37, frameItemStack);
        addItem(38, frameItemStack);
        addItem(39, frameItemStack);
        addItem(40, frameItemStack);
        addItem(41, frameItemStack);
        addItem(42, frameItemStack);
        addItem(43, frameItemStack);
        addItem(44, frameItemStack);
        addItem(45, frameItemStack);

        addItem(50, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)
                        .setTranslatedName(corePlayer, "BACK_TITLE", null)
                        .setTranslatedLore(corePlayer, "BACK_LINES", null)
                        .build(),

                (player1, event) -> new GuildMenuInventory().openInventory(corePlayer));
    }

    private void GiveRankPlayerJson(@NonNull CorePlayer corePlayer) {
        corePlayer.closeInventory();
        corePlayer.dispatchCommand("guild rank");
        JsonChatMessage.create(" §9[ Изменить ранк игроку]")
                .addHover(HoverEvent.Action.SHOW_TEXT, "§7Нажмите, чтобы изменить ранк игроку")
                .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/guild rank ")

                .sendMessage(corePlayer);
    }
}
