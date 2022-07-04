package net.plazmix.commands.inventory.donate;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.impl.CoreSimpleInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class FastMessageCommand extends CommandExecutor {

    public FastMessageCommand() {
        super("fm", "fastmessage", "быстрыесообщения");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        new FastMessageInventory(commandSender.getLanguageType().getResource()).openInventory((CorePlayer) commandSender);
    }

    protected static class FastMessageInventory extends CorePaginatedInventory {

        private final Map<String, String> FAST_MESSAGES = new LinkedHashMap<String, String>() {{
            put("Гоу", "Гоу ┎(ಠ‿ಠ)┚");
            put("Ня", "Ня (=^_^=)");
            put("Засыпаю", "Засыпаю (一ω一) zzZ");
            put("Сдаюсь", "Сдаюсь (◣_◢)");
            put("Мажор", "Мажор $ (ಠ_ಠ) $");
            put("Привет", "Привет (^ω^)ノ");
            put("Пока", "Пока (◣_◢)");
            put("Обнимаю", "Обнимаю ༼ つ ◕_◕ ༽つ");
            put("Огооо", "Огооо (°Ｏ°)ノ");
            put("Чее?", "Чее? ＼(Ｏ_ｏ)／");
            put("Ясно", "Ясно (ಠ_ಠ)");
            put("Не тупи", "Не тупи (-_-)");
            put("Мило", "Мило (●´ω｀●)");
            put("Обидно", "Обидно o(╥﹏╥)o");
            put("ВТФ???", "ВТФ??? ლ(ಠ益ಠლ)");
            put("Дай ресов", "Дай ресов ʕ•ᴥ•ʔ");
            put("Повезло, повезло", "Повезло, повезло (o_O)");
            put("Не повезло, не повезло", "Не повезло, не повезло (╯_╰)");
            put("Кавооо", "Кавооо ٩(͡๏̯͡๏)۶");
            put("Круто", "Круто! (͡° ͜ʖ ͡°)");
            put("Вечер в хату", "Вечер в хату (^０^)");
            put("Я тебя обожаю", "Я тебя обожаю (ﾉ´з｀)");
            put("Изи", "Изи (︶▽︶)");
            put("Я рад", "Я рад (｡◕‿◕｡)");
            put("Ничего себе", "Ничего себе ◉_◉");
            put("Я втюрился", "Я втюрился (´｡• ᵕ •｡`)");
            put("Не знаю", "Не знаю ┌(ಠ_ಠ)┘");
            put("Классно", "Классно (ᵔ.ᵔ)");
            put("Да блин!", "Да блин! ・_・");
            put("Что-то на китайском...", "Что-то на китайском... °益°");
            put("Ха-ха-ха", "Ха-ха-ха =^‥^=");
            put("Чао!", "Чао! >_<");
        }};

        public FastMessageInventory(LocalizationResource localizationResource) {
            super(5, localizationResource.getMessage("FASTMESSAGE_MENU_TITLE"));
        }

        @Override
        public void drawInventory(CorePlayer player) {
            setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

            getInventoryMarkup().addHorizontalRow(2, 1);
            getInventoryMarkup().addHorizontalRow(3, 1);
            getInventoryMarkup().addHorizontalRow(4, 1);


            ChatColor chatColor = player.getGroup().isDefault() ? ChatColor.RED : ChatColor.GREEN;

            FAST_MESSAGES.forEach((messageName, messageText) -> {

                addItemToMarkup(ItemBuilder.newBuilder(player.getGroup().isDefault() ? Material.STAINED_GLASS_PANE : Material.SIGN)
                                .setDurability(player.getGroup().isDefault() ? 14 : 0)
                                .setDisplayName(chatColor + messageName)

                                .addLore("§7Сообщение: §e" + messageText)
                                .addLore("")
                                .addLore(chatColor + "▸ Нажмите, чтобы отправить!")

                                .build(),

                        (player1, event) -> {

                            player.closeInventory();

                            if (player.getGroup().isDefault()) {
                                return;
                            }

                            for (CorePlayer onlineCorePlayer : player.getBukkitServer().getOnlinePlayers()) {
                                onlineCorePlayer.sendMessage(player.getDisplayName() + " §8➥ §e" + messageText);
                            }

                        });
            });
        }
    }
}
