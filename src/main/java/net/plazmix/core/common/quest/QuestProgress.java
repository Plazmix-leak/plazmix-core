package net.plazmix.core.common.quest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuestProgress {

    int taskId;
    int maxValue;

    private int initProgressValue(Quest quest, int playerId) {
        return quest.task(taskId).getCachedPlayerData(quest, playerId).getProgress();
    }

    public boolean isCompleted(Quest quest, int playerId) {
        return getPlayerProgress(quest, playerId) >= maxValue;
    }

    public int getPlayerProgress(Quest quest, int playerId) {
        int value = initProgressValue(quest, playerId);
        return Math.min(value, maxValue);
    }

    public void incrementProgress(Quest quest, int playerId) {
        int currentValue = initProgressValue(quest, playerId);

        if (currentValue >= maxValue) {
            return;
        }

        Quest.QuestTaskData data = quest.task(taskId).getCachedPlayerData(quest, playerId);
        data.setProgress(data.getProgress() + 1);
    }
}