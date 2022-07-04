package net.plazmix.ask.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.question.QuestionManager;
import net.plazmix.core.connection.player.CorePlayer;

public final class QuestionsListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        if (corePlayer.getGroup().isStaff() && !QuestionManager.INSTANCE.getActiveQuestions().isEmpty()) {

            corePlayer.sendMessage("\n§d§lPlazmix §8:: §fСейчас на сервере актуально §a"
                    + NumberUtil.formatting(QuestionManager.INSTANCE.getActiveQuestions().size(), "§fвопрос", "§fвопроса", "§fвопросов") + " от игроков!");

            corePlayer.sendMessage(" §cПостарайтесь ответить на них в ближайшее время!");
            corePlayer.sendMessage(" §7Открыть список вопросов - §e/ans\n");
        }
    }

}
