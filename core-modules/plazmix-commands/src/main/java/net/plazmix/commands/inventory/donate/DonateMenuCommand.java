package net.plazmix.commands.inventory.donate;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class DonateMenuCommand extends CommandExecutor {

    public DonateMenuCommand() {
        super("dm", "donatemenu", "донатменю");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new DonateMenuInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class DonateMenuInventory extends CoreSimpleInventory {

        public DonateMenuInventory(LocalizationResource localizationResource) {
            super(5, localizationResource.getMessage("DONATE_MENU_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {

            addItem(20, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y3Y2RlZWZjNmQzN2ZlY2FiNjc2YzU4NGJmNjIwODMyYWFhYzg1Mzc1ZTlmY2JmZjI3MzcyNDkyZDY5ZiJ9fX0=")

                            .setTranslatedName(corePlayer, "DONATE_MENU_JOINER_TITLE", null)
                            .setTranslatedLore(corePlayer, "DONATE_MENU_JOINER_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("joiner"));

            addItem(22, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4ZmVkNmU1YTUyNmRlYTMxNTZiZDJiNDY5YWExMjVjZWMyZjY1YTk3ZTYyYmUxYTE2YTIyNjMxOTRiNWZjMCJ9fX0=")

                            .setTranslatedName(corePlayer, "DONATE_MENU_FASTMESSAGE_TITLE", null)
                            .setTranslatedLore(corePlayer, "DONATE_MENU_FASTMESSAGE_LINES", null)
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("fastmessage"));

            addItem(24, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE2MjYzOTRkOTY0NjJmNjJlMGIyOTk2ZmFjYjQwMGJkYjc4YmRiOTlmMGQxODk2OTJlZTk2NDdkMTk3NTgzOSJ9fX0=")

                            .setTranslatedName(corePlayer, "DONATE_MENU_QUITER_TITLE", null)
                            .setTranslatedLore(corePlayer, "DONATE_MENU_QUITER_LINES", null)
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("quiter"));

            addItem(26, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdmZjEzNzc3NTQ1NjNhYjQxYjhhMDMwNWRhYzAzZGU2M2UwMmU1YTM5YTY5NTZhZmQ2Y2NhYmYyOTVhOTZkOCJ9fX0=")

                            .setTranslatedName(corePlayer, "DONATE_MENU_PREFIX_TITLE", null)
                            .setTranslatedLore(corePlayer, "DONATE_MENU_PREFIX_LINES", null)
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("prefix"));

            addItem(41, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                            .setTranslatedName(corePlayer, "BACK_TITLE", null)
                            .setTranslatedLore(corePlayer, "BACK_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("profile"));
        }
    }
}
