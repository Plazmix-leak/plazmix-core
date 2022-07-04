package net.plazmix.commands.inventory.other;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.common.language.LanguageManager;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class LanguageCommand extends CommandExecutor {

    public LanguageCommand() {
        super("language", "lang", "язык");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new LanguageSelectInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class LanguageSelectInventory extends CoreSimpleInventory {

        public LanguageSelectInventory(LocalizationResource localizationResource) {
            super(5, localizationResource.getMessage("LANGUAGE_SELECT_MENU_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer player) {
            addItem(11, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDisplayName(ChatColor.YELLOW + "Русский")

                            .addLore("§7Измените стандартный язык сервера")
                            .addLore("§7на русский язык")
                            .addLore("")
                            .addLore("§e▸ Нажмите, чтобы выбрать!")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZlYWZlZjk4MGQ2MTE3ZGFiZTg5ODJhYzRiNDUwOTg4N2UyYzQ2MjFmNmE4ZmU1YzliNzM1YTgzZDc3NWFkIn19fQ==")
                            .setDurability(3)

                            .build(),

                    (player1, event) -> {

                        LanguageManager.INSTANCE.updatePlayerLanguage(player, LanguageType.RUSSIAN);
                        player.closeInventory();

                        player.sendLangMessage("LANG_SELECT");
                    });

            addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDisplayName(ChatColor.YELLOW + "English")

                            .addLore("§7Switch the default server language")
                            .addLore("§7to English language")
                            .addLore("")
                            .addLore("§e▸ Click to select!")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19")
                            .setDurability(3)

                            .build(),

                    (player1, event) -> {

                        LanguageManager.INSTANCE.updatePlayerLanguage(player, LanguageType.ENGLISH);

                        player.closeInventory();
                        player.sendLangMessage("LANG_SELECT");
                    });

            addItem(13, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDisplayName(ChatColor.YELLOW + "Український")

                            .addLore("§7Ізменіте стандартна мова сервера")
                            .addLore( "§7на українську мову")
                            .addLore( "")
                            .addLore( "§e▸ Натисніть, щоб вибрати!")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiOWY1MmUzNmFhNWM3Y2FhYTFlN2YyNmVhOTdlMjhmNjM1ZThlYWM5YWVmNzRjZWM5N2Y0NjVmNWE2YjUxIn19fQ==")
                            .setDurability(3)

                            .build(),

                    (player1, event) -> {

                        LanguageManager.INSTANCE.updatePlayerLanguage(player, LanguageType.UKRAINE);

                        player.closeInventory();
                        player.sendLangMessage("LANG_SELECT");
                    });

            addItem(41, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                            .setTranslatedName(player,"BACK_TITLE", null)
                            .setTranslatedLore(player, "BACK_LINES", null)

                            .build(),

                    (player1, event) -> player.dispatchCommand("profile"));
        }
    }
}
