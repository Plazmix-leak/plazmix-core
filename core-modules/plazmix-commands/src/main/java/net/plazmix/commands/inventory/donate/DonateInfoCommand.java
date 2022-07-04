package net.plazmix.commands.inventory.donate;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.Placeholders;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class DonateInfoCommand extends CommandExecutor {

    public DonateInfoCommand() {
        super("привилегии", "privilege", "donateinfo", "donatelist");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new DonateInfoInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class DonateInfoInventory extends CoreSimpleInventory {

        public DonateInfoInventory(LocalizationResource localizationResource) {
            super(5, localizationResource.getMessage("DONATE_TITLE"));
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            addItem(5, ItemBuilder.newBuilder(Material.SIGN)

                    .setTranslatedName(corePlayer, "DONATE_STATUS_TITLE", null)
                    .setTranslatedLore(corePlayer, "DONATE_STATUS_LINE", null)

                    .build());

            addItem(21, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                    .setTranslatedName(corePlayer, "DONATE_DESCRIPTION_STAR_TITLE", Placeholders.newInstance()
                            .replace("%star%", Group.STAR.getPrefix())
                            .replace("%cost%", 199))

                    .setTranslatedLore(corePlayer, "DONATE_DESCRIPTION_STAR_LINES", Placeholders.newInstance()
                            .replace("%cost%", 199))

                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE0NjljZTY3ZGZjMjg0MzNhZWE4YTQ1YWI4MTZlNTFiZDM5YmE5ZWI4MTVkMjI1NzllNzc2OThkYTBiZjI5NSJ9fX0=")
                    .setDurability(3)

                    .build());

            addItem(22, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                    .setTranslatedName(corePlayer, "DONATE_DESCRIPTION_COMSO_TITLE", Placeholders.newInstance()
                            .replace("%cosmo%", Group.COSMO.getPrefix())
                            .replace("%cost%", 499))

                    .setTranslatedLore(corePlayer, "DONATE_DESCRIPTION_COMSO_LINES", Placeholders.newInstance()
                            .replace("%cost%", 499))

                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg0MzgwYWQ0ZGMzODhmNTYxMjU0Yzg0MDlmYTcwNGQ0NDc0N2ViNmU1ZmVhY2JhMzQzZTdjMjQzY2ZhYzZhMSJ9fX0=")
                    .setDurability(3)

                    .build());

            addItem(23, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                    .setTranslatedName(corePlayer, "DONATE_DESCRIPTION_GALAXY_TITLE", Placeholders.newInstance()
                            .replace("%galaxy%", Group.GALAXY.getPrefix())
                            .replace("%cost%", 999))

                    .setTranslatedLore(corePlayer, "DONATE_DESCRIPTION_GALAXY_LINES", Placeholders.newInstance()
                            .replace("%cost%", 999))

                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU1MmY3OTYwZmYzY2VjMmY1MTlhNjM1MzY0OGM2ZTMzYmM1MWUxMzFjYzgwOTE3Y2YxMzA4MWRlY2JmZjI0ZCJ9fX0=")
                    .setDurability(3)

                    .build());

            addItem(24, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                    .setTranslatedName(corePlayer, "DONATE_DESCRIPTION_UNIVERSE_TITLE", Placeholders.newInstance()
                            .replace("%universe%", Group.UNIVERSE.getPrefix())
                            .replace("%cost%", 2999))

                    .setTranslatedLore(corePlayer, "DONATE_DESCRIPTION_UNIVERSE_LINES", Placeholders.newInstance()
                            .replace("%cost%", 2999))

                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRiMzJiMTVkN2YzMjcwNGVkNjI2ZmE1MmQwNmZiMmI0MDcxZDMzNmZkYmZlNjFlNmU0MWM2NjlkNmUzN2Y0NyJ9fX0=")
                    .setDurability(3)

                    .build());

            addItem(25, ItemBuilder.newBuilder(Material.SKULL_ITEM)

                    .setTranslatedName(corePlayer, "DONATE_DESCRIPTION_LUXURY_TITLE", Placeholders.newInstance()
                            .replace("%luxury%", Group.LUXURY.getPrefix())
                            .replace("%cost%", 9999))

                    .setTranslatedLore(corePlayer, "DONATE_DESCRIPTION_LUXURY_LINES", Placeholders.newInstance()
                            .replace("%cost%", 9999))

                    .setDurability(3)
                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDc3NGU1ZWYzZDhiY2RlOWVhMjFjMzRiODQ4MjdkMzQ1MzFlNjhmMTExNTEwZjMzODMwNTVlY2FhNzRiZWJjYyJ9fX0=")

                    .build());

            addItem(41, ItemBuilder.newBuilder(Material.SPECTRAL_ARROW)

                            .setTranslatedName(corePlayer, "BACK_TITLE", null)
                            .setTranslatedLore(corePlayer, "BACK_LINES", null)

                            .build(),

                    (player1, event) -> corePlayer.dispatchCommand("mode"));
        }
    }
}
