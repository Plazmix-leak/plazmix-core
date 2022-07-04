package net.plazmix.ask.inventory;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.question.Question;
import net.plazmix.core.common.question.QuestionManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;

public final class QuestionsMenu extends CorePaginatedInventory {

    public QuestionsMenu() {
        super(5, "Актуальные вопросы игроков");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer corePlayer) {
        Collection<Question> activeQuestionCollection = QuestionManager.INSTANCE.getActiveQuestions();

        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§aОбщая информация")
                .addLore("§7Всего вопросов: §e" + activeQuestionCollection.size())
                .build());

        int questionCounter = 0;
        for (Question question : activeQuestionCollection) {

            CorePlayer offlinePlayer = PlazmixCore.getInstance().getOfflinePlayer(question.getPlayerName());

            addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setPlayerSkull(question.getPlayerName())

                            .setDisplayName("§eВопрос #" + (questionCounter + 1))
                            .addLore("",
                                    "§7Задал: " + offlinePlayer.getDisplayName() + (offlinePlayer.isOnline() ? " §a(В сети)" : " §c(Не в сети)"),
                                    "§7Вопрос: §f" + question.getPlayerQuestion(),
                                    "",
                                    "§7Был задан:",
                                    " §f" + NumberUtil.getTime(System.currentTimeMillis() - question.getQuestionDate()) + " назад",
                                    "",
                                    "§e▸ Нажмите ЛКМ, чтобы ответить на вопрос!",
                                    "§e▸ Нажмите ПКМ, чтобы удалить вопрос!")
                            .build(),

                    (inventory, inventoryClickEvent) -> {
                        corePlayer.closeInventory();

                        switch (inventoryClickEvent.getMouseAction()) {
                            case RIGHT: {
                                QuestionManager.INSTANCE.removeQuestion(question);

                                for (CorePlayer staffOnline : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff()))
                                    staffOnline.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fудалил вопрос игрока " + offlinePlayer.getDisplayName() + " §7(" + question.getPlayerQuestion() + ")");

                                offlinePlayer.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fудалил ваш вопрос! §7(" + question.getPlayerQuestion() + ")");
                                break;
                            }

                            case LEFT: {
                                BaseComponent[] spaces = JsonChatMessage.create("        ").build();
                                BaseComponent[] answerButton = JsonChatMessage.create("§a§l[ОТВЕТИТЬ]")
                                        .addHover(HoverEvent.Action.SHOW_TEXT, "§aОтветить на вопрос " + offlinePlayer.getName())
                                        .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/ans " + offlinePlayer.getName() + " ")
                                        .build();

                                corePlayer.sendMessage("§d§lPlazmix §8:: §fДля ответа на вопрос " + offlinePlayer.getDisplayName(), "§f нажмите на кнопку §e\"Ответить\" §fвнизу:\n");

                                corePlayer.sendMessage(ChatMessageType.CHAT, JsonChatMessage.create("")
                                        .addComponents(spaces)
                                        .addComponents(answerButton)
                                        .addComponents(spaces)
                                        .addText("\n").build());
                                break;
                            }
                        }
                    });

            questionCounter++;
        }

        if (questionCounter == 0) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cУпс, ничего не найдено :c")
                    .build());
        }
    }

}
