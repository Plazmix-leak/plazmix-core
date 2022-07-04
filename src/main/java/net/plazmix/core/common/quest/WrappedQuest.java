package net.plazmix.core.common.quest;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.core.api.inventory.itemstack.Material;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class WrappedQuest {

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    protected static class TaskAppender {

        List<QuestTask> tasks = new ArrayList<>();

        public void add(QuestTask questTask) {
            tasks.add(questTask);
        }
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    protected static class RewardAppender {

        List<Quest.QuestReward> rewards = new ArrayList<>();

        public void add(Quest.QuestReward questReward) {
            rewards.add(questReward);
        }
    }

    int id;

    @NonNull
    Material material;

    @NonNull
    Quest.QuestRequirement requirement;


    protected abstract void addTasks(@NonNull TaskAppender appender);

    protected abstract void addRewards(@NonNull RewardAppender appender);

    public Quest newQuestInstance() {
        Quest quest = Quest.create(id, material, requirement);

        // Quest Tasks.
        TaskAppender taskAppender = new TaskAppender();
        addTasks(taskAppender);

        for (QuestTask questTask : taskAppender.tasks) {
            quest.addTask(questTask);
        }

        // Quest Rewards.
        RewardAppender rewardAppender = new RewardAppender();
        addRewards(rewardAppender);

        for (Quest.QuestReward questReward : rewardAppender.rewards) {
            quest.addReward(questReward);
        }

        // Return a new quest instance result.
        return quest;
    }
}
