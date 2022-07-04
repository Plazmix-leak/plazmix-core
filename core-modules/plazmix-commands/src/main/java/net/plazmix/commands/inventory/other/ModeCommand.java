package net.plazmix.commands.inventory.other;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.api.utility.Placeholders;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;

public class ModeCommand extends CommandExecutor {

    public ModeCommand() {
        super("menu", "mode", "играть", "меню");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new ModeInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class ModeInventory extends CoreSimpleInventory {
        public ModeInventory(LocalizationResource localizationResource) {
            super(6, localizationResource.getMessage("MENU_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {

            // SKYWARS
            addItem(14, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)

                            .setTranslatedName(corePlayer, "MENU_SKYWARS_NAVIGATION_TITLE", null)
                            .setTranslatedLore(corePlayer, "MENU_SKYWARS_NAVIGATION_LINES", null)

                            .addLore(getModeStatus(ServerMode.SKYWARS))

                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhiZThhYmQ2NmQwOWE1OGNlMTJkMzc3NTQ0ZDcyNmQyNWNhZDdlOTc5ZThjMjQ4MTg2NmJlOTRkM2IzMmYifX19")
                            .build(),

                    (player1, event) -> corePlayer.connect(ServerMode.SKYWARS, ServerSubModeType.GAME_LOBBY));

            // BEDWARS
            addItem(15, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiMjkwYTEzZGY4ODI2N2VhNWY1ZmNmNzk2YjYxNTdmZjY0Y2NlZTVjZDM5ZDQ2OTcyNDU5MWJhYmVlZDFmNiJ9fX0=")

                            .setTranslatedName(corePlayer, "MENU_BEDWARS_NAVIGATION_TITLE", null)
                            .setTranslatedLore(corePlayer, "MENU_BEDWARS_NAVIGATION_LINES", null)

                            .addLore(getModeStatus(ServerMode.BEDWARS))

                            .build(),

                    (player1, event) -> corePlayer.connect(ServerMode.BEDWARS, ServerSubModeType.GAME_LOBBY));

           // PRACTICE
           addItem(16, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRmM2ZmOTIwOTFlZmEwMTlkY2ZkNTgzMzRlMjUyZjM5Nzc1Zjk3MDI1NzkzMjY4NTExZGVhZTVmZGE2ZjFlMCJ9fX0=")

                           .setTranslatedName(corePlayer, "MENU_DUELS_NAVIGATION_TITLE", null)
                           .setTranslatedLore(corePlayer, "MENU_DUELS_NAVIGATION_LINES", null)

                           .addLore(getModeStatus(ServerMode.DUELS))
                           .build(),

                   (player1, event) -> corePlayer.connect(ServerMode.DUELS, ServerSubModeType.GAME_LOBBY));

           // ARCADE GAMES
           addItem(17, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setTranslatedName(corePlayer, "MENU_ARCADE_NAVIGATION_TITLE", null)
                           .setTranslatedLore(corePlayer, "MENU_ARCADE_NAVIGATION_LINES", null)

                           .addLore(getModeStatus(ServerMode.ARCADE))

                           .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ0MDhjNTY5OGYyZDdhOGExNDE1ZWY5NTkyYWViNGJmNjJjOWFlN2NjZjE4ODQ5NzUzMGJmM2M4Yjk2NDhlNSJ9fX0=")
                           .build(),

                   (player1, event) -> corePlayer.connect(ServerMode.ARCADE, ServerSubModeType.GAME_LOBBY));

           // GUNGAME
           addItem(24, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setDisplayName("§dGunGame")
                           .addLore("")

                           .addLore(getModeStatus(ServerMode.GUNGAME))

                           .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ2NDQ5MzczNjgyMzgxYTY1Y2FlNjVhMjI1M2Q4YjM2YjI5Mzc3NjQxMmM1ZGY4ZGVhNGQ5NjQzOTNhZjdhIn19fQ==")
                           .build(),

                   (player1, event) -> corePlayer.connect(ServerMode.GUNGAME, ServerSubModeType.GAME_ARENA));


           // ========================================================================================================== //

           // PROFILE
           addItem(11, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setTranslatedName(corePlayer, "MENU_PROFILE_TITLE", Placeholders.newInstance()
                                   .replace("%player%", corePlayer.getDisplayName()))
                           .setTranslatedLore(corePlayer, "MENU_PROFILE_LINES", null)

                           .setPlayerSkull(corePlayer.getName())
                           .build(),

                   (player1, event) -> corePlayer.dispatchCommand("profile"));

           // YOUTUBE
           addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setDisplayName("§cYOUTUBE")
                           .addLore("")
                           .addLore("§fНаш ютуб канал:")
                           .addLore("§bhttps://plzm.xyz/yt")
                           .addLore("")

                           .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE0NTFlNWY4NWY4YmY3NDcyZjZlNmQxOWI3ZGRkOGZhZTM5OWUwOTkxMWYyMWJjZTdmOTcxMzFiNDJhNWE1NyJ9fX0=")
                           .build(),

                   (player1, event) -> corePlayer.sendMessage("§c§lYouTube §8:: §fПрисоединяйся: §ehttps://plzm.xyz/yt"));

           // DONATE
           addItem(20, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setTranslatedName(corePlayer, "MENU_DONATE_TITLE", null)
                           .setTranslatedLore(corePlayer, "MENU_DONATE_LINES", null)

                           .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNjN2Y2NDQxYmQ3MWZjOTc0ZTk5NzdiY2IyMmVmYmM0YjYxMjc3YzQ5ZWZiMjQyM2FiOTE1NDg5NWJlIn19fQ==")
                           .build(),

                   (player1, event) -> corePlayer.dispatchCommand("donate"));

           // VK
           addItem(21, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                   .setDurability(3)

                   .setDisplayName("§bVK")
                   .addLore("")
                   .addLore("§fНаша группа вконтакте:")
                   .addLore("§bhttps://plzm.xyz/vk")
                   .addLore("")

                   .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkNzNkNjE2ZDIxYzE5MzAxZjJmMDc2Y2JjNTQ3YzdjMWI1MWJkNWUxYTQ1ZDdjNTlkNWFkYjgyODA4ZSJ9fX0=")
                   .build(),

                   (player1, event) -> corePlayer.sendMessage("§b§lVKontakte §8:: §fПрисоединяйся: §ehttps://plzm.xyz/vk"));

           // MAIN LOBBY
           addItem(29, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                           .setDurability(3)

                           .setTranslatedName(corePlayer, "MENU_MAIN_LOBBY_TITLE", null)
                           .setTranslatedLore(corePlayer, "MENU_MAIN_LOBBY_LINES", null)

                           .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I1NmU0OTA4NWY1NWQ1ZGUyMTVhZmQyNmZjNGYxYWZlOWMzNDMxM2VmZjk4ZTNlNTgyNDVkZWYwNmU1ODU4YyJ9fX0=")
                           .build(),

                   (player1, event) -> corePlayer.dispatchCommand("hub"));

           // DISCORD
           addItem(30, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                   .setDurability(3)

                   .setDisplayName("§9DISCORD")
                   .addLore("")
                   .addLore("§fНаш дискорд сервер:")
                   .addLore("§9https://plzm.xyz/discord")
                   .addLore("")

                   .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0=")
                   .build(),

                   (player1, event) -> corePlayer.sendMessage("§9§lDiscord §8:: §fПрисоединяйся: §ehttps://plzm.xyz/discord"));

           if (corePlayer.getGroup().isAdmin()) {
               //FIXME
               // add || corePlayer.getGroup().isBuilder()
               addItem(46, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                       .setDurability(3)

                       .setDisplayName("§fТехнические сервера")
                       .addLore("")
                       .addLore("§fЗдесь будут находиться")
                       .addLore("§fтехнические сервера")
                       .addLore("")

                       .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDUzZjBjOWE2NTI4OTdlZWNhYmMyMWE0NWRkZGE3NzQ2NGYyYTYyNTA5YjQ4MWE2YTNiNTMxMjBhZDBlNzgxNSJ9fX0=")
                       .build());
           }

            // ========================================================================================================== //

            ItemStack frameItemStack = ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDisplayName("§7")
                    .setDurability(2)
                    .build();

            addItem(38, frameItemStack);
            addItem(39, frameItemStack);
            addItem(41, frameItemStack);
            addItem(42, frameItemStack);
            addItem(43, frameItemStack);
            addItem(44, frameItemStack);

            ItemStack itemStack = ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDisplayName("§cРежим в разработке...")
                    .setDurability(3)
                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMmMzMTYzZjFjYTZlN2Y0MTVlNTgzNzYzYTZkMjE5OGEyZjg2NDdiYzFmNWE3YzNiMmY5YzkyYjRkMjNiNyJ9fX0=")

                    .build();

            addItem(23, itemStack);
            addItem(25, itemStack);
            addItem(26, itemStack);
            addItem(32, itemStack);
            addItem(33, itemStack);
            addItem(34, itemStack);
            addItem(35, itemStack);


        }

        private String[] getModeStatus(ServerMode serverMode) {
            int modeServersCount = PlazmixCore.getInstance().getServersByPrefix(serverMode.getServersPrefix()).size();

            if (modeServersCount <= 0) {
                return new String[]{
                        "§cСервера данного режима недоступны!",
                        "§cЕсли Вы считаете это ошибкой, то сообщите",
                        "§cнам в личные сообщения - vk.me/plazmixnetwork"
                };
            }
            return new String[]{
                    "§8▪ §fСерверов: §e" + NumberUtil.spaced(PlazmixCore.getInstance().getConnectedServersCount(serverMode.getServersPrefix())),
                    "§8▪ §fОнлайн режима: §e" + NumberUtil.spaced(PlazmixCore.getInstance().getOnlineByServerPrefix(serverMode.getServersPrefix())),
                    "",
                    "§e▸ Нажми, чтобы перейти на режим"
            };
        }
    }
}

