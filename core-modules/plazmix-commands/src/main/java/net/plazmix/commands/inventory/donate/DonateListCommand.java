package net.plazmix.commands.inventory.donate;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class DonateListCommand extends CommandExecutor {

    public DonateListCommand() {
        super("donate", "донат", "донейт", "донате");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new DonateInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class DonateInventory extends CoreSimpleInventory {

        public DonateInventory(LocalizationResource localizationResource) {
            super(6, localizationResource.getMessage("DONATE_MENU_MAIN_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer player) {
            addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setTranslatedName(player, "DONATE_MENU_STATUS_TITLE", null)
                            .setTranslatedLore(player, "DONATE_MENU_STATUS_LINES", null)

                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY3NWQxYjc4NWQxOGQ0N2IzZWE4ZjBhN2UwZmQ0YTFmYWU5ZTdkMzIzY2YzYjEzOGM4Yzc4Y2ZlMjRlZTU5In19fQ==")

                            .build(),

                    (player1, event) -> player.dispatchCommand("donateinfo"));

            addItem(16, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyNWRiZTQ3NjY3ZDBjZTIzMWJhYTIyM2RlZTk1M2JiZmM5Njk2MDk3Mjc5ZDcyMzcwM2QyY2MzMzk3NjQ5ZSJ9fX0=")

                    .setTranslatedName(player, "DONATE_MENU_MONEY_TITLE", null)
                    .setTranslatedLore(player, "DONATE_MENU_MONEY_LINES", null)

                    .build(),

            (player1, event) -> player.dispatchCommand("plazma"));

            addItem(31, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDVjYTVlODVjNGFlYTYwOGJkMzQ0M2NhYmRmMWMyYmRkZTNiNDMxYWQzYWEzOGZmZmUwNGEzMmViN2U1MjUifX19")

                    .setTranslatedName(player, "DONATE_MENU_PAYMENT_TITLE", null)
                    .setTranslatedLore(player, "DONATE_MENU_PAYMENT_LINES", null)

                    .build());

            addItem(33, ItemBuilder.newBuilder(Material.KNOWLEDGE_BOOK)

                    .setTranslatedName(player, "DONATE_MENU_SURCHARGE_TITLE", null)
                    .setTranslatedLore(player, "DONATE_MENU_SURCHARGE_LINES", null)

                    .build());


            addItem(50, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                            .setTranslatedName(player, "BACK_TITLE", null)
                            .setTranslatedLore(player, "BACK_LINES", null)
                            .build(),

                    (player1, event) -> player.dispatchCommand("profile"));
        }
    }
}
