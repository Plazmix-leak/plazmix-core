package net.plazmix.economy.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

public class ConvertCommand extends CommandExecutor {

    public ConvertCommand() {
        super("convert", "convertplazma", "конвертировать");

        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        int plazmaCount = PlazmixCore.getInstance().getPlayer(commandSender.getName()).getPlazma();

        new ConvertInventory(commandSender.getLanguageType().getResource(), plazmaCount).openInventory((CorePlayer) commandSender);
    }

    protected static class ConvertInventory extends CoreSimpleInventory {
        private int plazmaCount = 0;

        public ConvertInventory(LocalizationResource localizationResource, int plazmaCount) {
            super(5, localizationResource.getMessage("DONATE_TITLE"));

            this.plazmaCount = plazmaCount;
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {

            // Inventory frames.
            ItemStack frameItem = ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(15)

                    .setDisplayName(ChatColor.RESET.toString())
                    .build();

            for (int slot = 1; slot <= 9; slot++)
                addItem(slot, frameItem);

            addItem(13, frameItem);
            addItem(14, frameItem);
            addItem(15, frameItem);

            // Information items.
            addItem(5, ItemBuilder.newBuilder(Material.EMERALD)
                            .setDisplayName("§aКонвертация плазмы")

                            .addLore("")
                            .addLore("§7Баланс: §a" + NumberUtil.formatting(corePlayer.getCoins(), "коин", "коина", "коинов"))
                            .addLore("§7Плазма: §d" + NumberUtil.formatting(corePlayer.getPlazma(), "плазма", "плазмы", "плазмы"))
                            .addLore("")
                            .addLore("§8Конвертация:")
                            .addLore(" §7Вы отдаете §b" + NumberUtil.formatting(plazmaCount, "плазма", "плазмы", "плазмы") + " §7, получая")
                            .addLore(" §7взамен §e" + NumberUtil.formatting(getConvertedCoinsByPlazma(), "коин", "коина", "коинов") + " §7на свой баланс")
                            .addLore("")
                            .addLore("§a▸ Нажмите, чтобы конвертировать")

                            .build(),

                    (player1, event) -> {

                        // Add coins.
                        corePlayer.addCoins(getConvertedCoinsByPlazma());

                        // Change plazma.
                        corePlayer.takePlazma(plazmaCount);

                        // Announce.
                        corePlayer.sendMessage("§d§lConvert §8:: §fВы успешно конвертировали §b" + plazmaCount + " плазмы §fв §e"
                                + NumberUtil.formatting(getConvertedCoinsByPlazma(), "коин", "коина", "коинов"));

                        corePlayer.closeInventory();
                    });

            addItem(32, ItemBuilder.newBuilder(Material.EXP_BOTTLE)
                    .setAmount(plazmaCount)

                    .setDisplayName("§e" + NumberUtil.formatting(plazmaCount, "плазма", "плазмы", "плазмы") + " §f-> §e" + NumberUtil.formatting(getConvertedCoinsByPlazma(), "коин", "коина", "коинов"))
                    .build());

            // Change level items.
            if (plazmaCount + 1 <= corePlayer.getPlazma()) {
                addItem(31, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(1)

                                .setDisplayName("§a+1")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount++;

                            updateInventory(corePlayer);
                        });
            }

            if (plazmaCount + 5 <= corePlayer.getPlazma()) {
                addItem(30, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(5)

                                .setDisplayName("§a+5")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount += 5;

                            updateInventory(corePlayer);
                        });
            }

            if (plazmaCount + 10 <= corePlayer.getPlazma()) {
                addItem(29, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(10)

                                .setDisplayName("§a+10")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount += 10;

                            updateInventory(corePlayer);
                        });
            }

            // minus to item amount
            if (plazmaCount - 1 > 0) {
                addItem(33, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(1)

                                .setDisplayName("§c-1")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount--;

                            updateInventory(corePlayer);
                        });
            }

            if (plazmaCount - 5 > 0) {
                addItem(34, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(5)

                                .setDisplayName("§c-5")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount -= 5;

                            updateInventory(corePlayer);
                        });
            }

            if (plazmaCount - 10 > 0) {
                addItem(35, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(10)

                                .setDisplayName("§c-10")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            plazmaCount -= 10;

                            updateInventory(corePlayer);
                        });
            }
        }

        private int getConvertedCoinsByPlazma() {
            return (plazmaCount * 100);
        }
    }
}
