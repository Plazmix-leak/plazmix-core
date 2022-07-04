package net.plazmix.guilds.inventory;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.common.guild.CoreGuild;
import net.plazmix.core.connection.player.CorePlayer;

public class GuildMenuInventory extends CoreSimpleInventory {

    public GuildMenuInventory() {
        super(5, "Меню гильдии");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        CoreGuild coreGuild = CoreGuild.of(corePlayer.getName());

        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                        .setDisplayName("§aОбщая информация")

                        .addLore("")
                        .addLore("§7Название: §c" + ChatColor.translateAlternateColorCodes('&', coreGuild.getTitle()))
                        .addLore("§7Участников: §e" + coreGuild.getMemberIdsMap().size())
                        .addLore("§7Коинов: §a" + coreGuild.getEconomy().getCoins())
                        .addLore("§7Плазмы: §6" + coreGuild.getEconomy().getGolds())
                        .addLore("§7Опыта: §a" + coreGuild.getEconomy().getExperience())
                        .addLore("")

                        .build());

        addItem(20, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDisplayName("§eПригласить игрока")
                        .setDurability(3)

                        .addLore("§8Приглашения игрока в гильдию")
                        .addLore("")
                        .addLore("§8▸ §6/guild invite <ник игрока> §7- Пригласить в гильдию")
                        .addLore("")
                        .addLore("§e▶ Нажмите, чтобы узнать подробнее.")

                        .setPlayerSkull("zasf")
                        .build(),

                (player1, event) -> InvitePlayerJson(corePlayer));

        addItem(22, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDisplayName("§eНастройки гильдии")
                        .setDurability(3)

                        .addLore("§8Настройки для гильдии")
                        .addLore("")
                        .addLore("§7Настройки гильдии включают в себя:")
                        .addLore("§7отображение тэга над головой,")
                        .addLore("§7включение экономический операций")
                        .addLore("")
                        .addLore("§e▶ Нажмите, чтобы узнать подробнее.")

                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3N2RhZmM4YzllYTA3OTk2MzMzODE3OTM4NjZkMTQ2YzlhMzlmYWQ0YzY2ODRlNzExN2Q5N2U5YjZjMyJ9fX0=")
                        .build(),

                (player1, event) -> corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, данная команда в разработке!"));

        addItem(24, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDisplayName("§eМагазин гильдии")
                        .setDurability(3)

                        .addLore("§8Магазин гильдии")
                        .addLore("")
                        .addLore("§7Здесь Вы сможете улучшить гильдию")
                        .addLore("§7купив тэг над головой,")
                        .addLore("§7улучшение кол-ва участников")
                        .addLore("§7и многое другое...")
                        .addLore("")
                        .addLore("§e▶ Нажмите, чтобы узнать подробнее.")

                        .setPlayerSkull("MrSnowDK")
                        .build(),

                (player1, event) -> corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, данная команда в разработке!"));

        addItem(26, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDisplayName("§eУчастники гильдии")
                        .setDurability(3)

                        .addLore("§8Участники гильдии")
                        .addLore("")
                        .addLore("§7Просмотр всех участников гильдии")
                        .addLore("")
                        .addLore("§e▶ Нажмите, чтобы узнать подробнее.")

                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkZDRmZTRhNDI5YWJkNjY1ZGZkYjNlMjEzMjFkNmVmYTZhNmI1ZTdiOTU2ZGI5YzVkNTljOWVmYWIyNSJ9fX0=")
                        .build(),

                (player1, event) -> corePlayer.openInventory(new GuildMembersInventory()));
    }

    private void InvitePlayerJson(@NonNull CorePlayer corePlayer) {
        corePlayer.closeInventory();
        JsonChatMessage.create(" §9[ Добавить в гильдию ]")
                .addHover(HoverEvent.Action.SHOW_TEXT, "§7Нажмите, чтобы отправить запрос в гильдию")
                .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/guild invite ")

                .sendMessage(corePlayer);
    }

}
