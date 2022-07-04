package net.plazmix.commands.inventory.donate;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class DonatePlazmaCommand extends CommandExecutor {

    public DonatePlazmaCommand() {
        super("plazma", "плазма");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new DonatePlazmaInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class DonatePlazmaInventory extends CoreSimpleInventory {

        public DonatePlazmaInventory(LocalizationResource localizationResource) {
            super(4, localizationResource.getMessage("DONATE_MENU_PLAZMA_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                            .setDisplayName("§fМешок с плазмой §7(x5)")

                            .addLore("")
                            .addLore("§8▸ §d5 §fплазмы на ваш игровой аккаунт")
                            .addLore("§8▸ §fЦена: §a§l25 §aрублей")
                            .addLore("")
                            .addLore(" §7Маленький мешок с плазмой")
                            .addLore(" §7Подойдет для новичков и для")
                            .addLore(" §7тех, кто еще не знает зачем")
                            .addLore(" §7нужна эта валюта")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы получить ссылку на сайт")

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")
                            .build(),

                    (player1, event) -> {

                        corePlayer.closeInventory();
                        corePlayer.sendLangMessage("DONATE_BUY_MESSAGE");
                    });


            addItem(13, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                            .setDisplayName("§fМешок с плазмой §7(x25)")

                            .addLore("")
                            .addLore("§8▸ §d25 §fплазмы на ваш игровой аккаунт")
                            .addLore("§8▸ §fЦена: §a§l125 §aрублей")
                            .addLore("")
                            .addLore(" §7Мешок с плазмой побольше")
                            .addLore(" §7На него уже можно купить вещи")
                            .addLore(" §7на §aOneBlock§7, кейс или бустер")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы получить ссылку на сайт")

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")
                            .build(),

                    (player1, event) -> {

                        corePlayer.closeInventory();
                        corePlayer.sendLangMessage("DONATE_BUY_MESSAGE");
                    });

            addItem(14, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                            .setDisplayName("§fМешок с плазмой §7(x50)")

                            .addLore("")
                            .addLore("§8▸ §d50 §fплазмы на ваш игровой аккаунт")
                            .addLore("§8▸ §fЦена: §a§l250 §aрублей")
                            .addLore("")
                            .addLore(" §7Средний мешок с плазмой")
                            .addLore(" §7С ним Вы сможете позволить")
                            .addLore(" §7намного больше на сервере!")
                            .addLore(" §7Может, пора в ТОП?")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы получить ссылку на сайт")

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")
                            .build(),

                    (player1, event) -> {

                        corePlayer.closeInventory();
                        corePlayer.sendLangMessage("DONATE_BUY_MESSAGE");
                    });

            addItem(15, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                            .setDisplayName("§fМешок с плазмой §7(x100)")

                            .addLore("")
                            .addLore("§8▸ §d100 §fплазмы на ваш игровой аккаунт")
                            .addLore("§8▸ §fЦена: §a§l500 §aрублей")
                            .addLore("")
                            .addLore(" §7Крупный мешок с плазмой")
                            .addLore(" §7Для игроков, которым хочется")
                            .addLore(" §7получить все и сразу!")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы получить ссылку на сайт")

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")
                            .build(),

                    (player1, event) -> {

                        corePlayer.closeInventory();
                        corePlayer.sendLangMessage("DONATE_BUY_MESSAGE");
                    });

            addItem(16, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                            .setDisplayName("§fМешок с плазмой §7(x200)")

                            .addLore("")
                            .addLore("§8▸ §d200 §fплазмы на ваш игровой аккаунт")
                            .addLore("§8▸ §fЦена: §a§l1000 §aрублей")
                            .addLore("")
                            .addLore(" §7Огромный мешок с плазмой")
                            .addLore(" §7который только можно купить!")
                            .addLore(" §7Специально для богачей нашего проекта!")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы получить ссылку на сайт")

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")
                            .build(),

                    (player1, event) -> {

                        corePlayer.closeInventory();
                        corePlayer.sendLangMessage("DONATE_BUY_MESSAGE");
                    });

            addItem(32, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                            .setTranslatedName(corePlayer,"BACK_TITLE", null)
                            .setTranslatedLore(corePlayer, "BACK_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("donate"));
        }
    }
}
