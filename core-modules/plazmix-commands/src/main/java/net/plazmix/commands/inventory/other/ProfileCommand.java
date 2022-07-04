package net.plazmix.commands.inventory.other;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.api.utility.PercentUtil;
import net.plazmix.core.api.utility.Placeholders;
import net.plazmix.core.common.friend.CoreFriend;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class ProfileCommand extends CommandExecutor {

    public ProfileCommand() {
        super("profile", "профиль", "проф", "профайл");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new ProfileInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class ProfileInventory extends CoreSimpleInventory {
        public ProfileInventory(LocalizationResource localizationResource) {
            super(6, localizationResource.getMessage("PROFILE_MENU_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            addItem(14, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull(corePlayer.getName())

                            .setTranslatedName(corePlayer, "PROFILE_MENU_BASE_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_BASE_LINES", Placeholders.newInstance()
                                    .replace("%spacepass%", corePlayer.getPass().isActivated() ? "§dПриобретён" : "§cНе приобретён")

                                    .replace("%server%", corePlayer.getBukkitServer().getName())
                                    .replace("%level%", NumberUtil.spaced(corePlayer.getLevel()))
                                    .replace("%level_percent%", NumberUtil.getIntPercent(corePlayer.getExperience(), corePlayer.getMaxExperience()))

                                    .replace("%status%", corePlayer.getGroup().getColouredName())

                                    .replace("%balance%", NumberUtil.spaced(corePlayer.getCoins()))
                                    .replace("%plazma%", NumberUtil.spaced(corePlayer.getPlazma()))

                                    .replace("%language%", corePlayer.getLanguageType().getDisplayName())
                                    .replace("%version%", String.valueOf(corePlayer.getMinecraftVersionName())))

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("stats"));

            addItem(20, ItemBuilder.newBuilder(Material.BLAZE_POWDER)
                            .setTranslatedName(corePlayer, "PROFILE_MENU_SKIN_HISTORY_TITLE", null)
                            .setTranslatedLore(corePlayer,"PROFILE_MENU_SKIN_HISTORY_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("skinhistory"));

            addItem(22, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)

                            .setTranslatedName(corePlayer, "PROFILE_MENU_BOOSTER_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_BOOSTER_LINES", null)

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQ5MmNhOTQwNzkxMzZkMjUyNTcwM2QzNzVjMjU1N2VhYzIwMWVlN2RkMzljZTExYzY0YTljMzgxNDdlY2M0ZCJ9fX0=")
                            .build(),

                    (player1, event) -> corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, данная команда в разработке..."));

            addItem(23, ItemBuilder.newBuilder(Material.REDSTONE_COMPARATOR)
                            .setTranslatedName(corePlayer,"PROFILE_MENU_SETTINGS_TITLE", null)
                            .setTranslatedLore(corePlayer,"PROFILE_MENU_SETTINGS_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, данная команда в разработке..."));

            addItem(24, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhNGQyMThkZjlkMzJjZDQ3ZDljMWQyOTQ4NzcxMjJiZTU5MTliNDE4YTZjYzNkMDg5MTYyYjEzM2YyZGIifX19")

                            .setTranslatedName(corePlayer, "PROFILE_MENU_REWARD_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_REWARD_LINES", null)
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("rewards"));

            addItem(26, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)

                            .setTranslatedName(corePlayer, "PROFILE_MENU_CONVERT_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_CONVERT_LINES", null)

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjIxMmJjM2Q5OWYxNTI5MjM3Y2I0Y2U3MTk4NjM1ZjJiNTM2MGFmY2IxM2MwMzQxZjUzODBhNGRjOGY2ODIzIn19fQ==")
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("lvlconvert"));

            addItem(30, ItemBuilder.newBuilder(Material.ENCHANTMENT_TABLE)

                            .setTranslatedName(corePlayer, "PROFILE_MENU_DONATEMENU_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_DONATEMENU_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("donatemenu"));

            addItem(32, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZlYWZlZjk4MGQ2MTE3ZGFiZTg5ODJhYzRiNDUwOTg4N2UyYzQ2MjFmNmE4ZmU1YzliNzM1YTgzZDc3NWFkIn19fQ==")

                            .setTranslatedName(corePlayer, "PROFILE_MENU_LANGUAGE_TITLE", null)
                            .setTranslatedLore(corePlayer, "PROFILE_MENU_LANGUAGE_LINES", Placeholders.newInstance()

                                    .replace("%ru_translated_percent%", "100%")
                                    .replace("%en_translated_percent%", /* getTranslationPercent(LanguageType.ENGLISH) + */ "0%")
                                    .replace("%uk_translated_percent%", /* getTranslationPercent(LanguageType.UKRAINE) + */ "0%"))

                            .build(),

                    (player1, event) -> corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, данная команда в разработке..."));

            addItem(34, ItemBuilder.newBuilder(Material.BREWING_STAND_ITEM)

                    .setTranslatedName(corePlayer, "PROFILE_MENU_LEVEL_TITLE", null)
                    .setTranslatedLore(corePlayer, "PROFILE_MENU_LEVEL_LINES", Placeholders.newInstance()
                            .replace("%level%", NumberUtil.spaced(corePlayer.getLevel()))
                            .replace("%level_progress%", NumberUtil.getIntPercent(corePlayer.getExperience(), corePlayer.getMaxExperience()))
                            .replace("%level_nextlvl%", NumberUtil.spaced(corePlayer.getLevel() + 1))
                            .replace("%level_percent%", NumberUtil.spaced(100 - NumberUtil.getIntPercent(corePlayer.getExperience(), corePlayer.getMaxExperience())))
                            .replace("%level_exp_required%", NumberUtil.spaced(corePlayer.getMaxExperience() - corePlayer.getExperience())))

                    .build());

            addItem(49, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setDisplayName("§6Список друзей §8§k§l|")

                            .addLore("")
                            .addLore("§7Просмотр списка друзей")
                            .addLore("§7и основную информацию о них")
                            .addLore("")
                            .addLore("§7Всего друзей: §c" + CoreFriend.of(corePlayer).getFriendsCount() + "§f/§a20")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы открыть список друзей")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4M2JhYjUwYTMyMjQwMjQ4ODZmMjUyNTFkMjRiNmRiOTNkNzNjMjQzMjU1OWZmNDllNDU5YjRjZDZhIn19fQ==")
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("f list"));

            addItem(50, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setDisplayName("§6Участники компании §8§k§l|")

                            .addLore("")
                            .addLore("§7Просмотр списка участников")
                            .addLore("§7Вашей текущей компании")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы открыть список")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhNzk4OWI1ZDZlNjIxYTEyMWVlZGFlNmY0NzZkMzUxOTNjOTdjMWE3Y2I4ZWNkNDM2MjJhNDg1ZGMyZTkxMiJ9fX0=")
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("party list"));

            addItem(51, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setDisplayName("§6Список участников гильдии §8§k§l|")

                            .addLore("")
                            .addLore("§7Просмотр списка участников гильдии")
                            .addLore("§7и основную информацию о них")
                            .addLore("")
                            .addLore("§e▸ Нажми, чтобы открыть список участников")

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNhM2YzMjRiZWVlZmI2YTBlMmM1YjNjNDZhYmM5MWNhOTFjMTRlYmE0MTlmYTQ3NjhhYzMwMjNkYmI0YjIifX19")
                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("g list"));
        }

        private double getTranslationPercent(LanguageType languageType) {
            return Math.floor(PercentUtil.getPercent(languageType.getResource().getLocalizationMessages().size(), LanguageType.RUSSIAN.getResource().getLocalizationMessages().size()));
        }
    }
}

