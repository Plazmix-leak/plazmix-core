package net.plazmix.core.common.quest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Quest {

    public static Quest create(int id, Material icon, QuestRequirement questTime) {
        return new Quest(id, icon, questTime);
    }

    @Setter
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class QuestTaskData {

        static QuestTaskData fromQuest(Quest quest, int taskId, int playerId, long timeGetMillis) {
            QuestTask questTask = quest.task(taskId);
            return new QuestTaskData(questTask.getId(), questTask.getProgress().getPlayerProgress(quest, playerId), timeGetMillis);
        }

        static QuestTaskData fromJson(String json) {
            return JsonUtil.fromJson(json, QuestTaskData.class);
        }


        final int taskId;
        int progress;

        final long timeGetMillis;

        public String toJson() {
            return JsonUtil.toJson(this);
        }
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class QuestDisplay {

        String title;
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class QuestReward {

        String title;
        Consumer<CorePlayer> handler;
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class QuestRequirement {

        Group requirementStatus;

        long processingTimeMillis;
        long refreshTimeMillis;
    }

    int id;

    @NonNull
    Material material;

    @NonNull
    QuestRequirement requirement;

    @NonNull
    List<QuestTask> tasks = new ArrayList<>();

    @NonNull
    List<QuestReward> rewards = new ArrayList<>();


    public QuestTask task(int taskId) {
        for (QuestTask questTask : getTasks()) {

            if (questTask.getId() == taskId) {
                return questTask;
            }
        }

        return null;
    }

    public Quest addTask(QuestTask questTask) {
        tasks.add(questTask);
        return this;
    }

    public Quest addReward(QuestReward questReward) {
        rewards.add(questReward);
        return this;
    }

    public void initPlayerData(int playerId) {

        for (QuestTask questTask : getTasks()) {
            questTask.getCachedPlayerData(this, playerId);
        }
    }

    public void updatePlayerData(int playerId) {

        for (QuestTask questTask : getTasks()) {
            questTask.updatePlayerData(this, playerId, questTask.getCachedPlayerData(this, playerId));
        }
    }

    // TODO: Использовать этот метод при каждом открытии гуи с квестами игроку.
    public void cleanupPlayerData(int playerId) {

        for (QuestTask questTask : getTasks()) {
            questTask.getPlayerDataMap().remove(playerId);
        }
    }

    public void acceptRewards(CorePlayer corePlayer) {
        for (QuestReward questReward : rewards) {
            questReward.handler.accept(corePlayer);
        }
    }

    public ItemStack toItemStack(CorePlayer corePlayer) {
        ItemBuilder itemBuilder = ItemBuilder.newBuilder(material);
        itemBuilder.setDisplayName(ChatColor.YELLOW + "Квест #" + id);

        itemBuilder.addLore("");
        itemBuilder.addLore("§fМинимальный статус: " + requirement.requirementStatus.getColouredName());
        itemBuilder.addLore("");
        itemBuilder.addLore("§fВремя на выполнение: §c" + NumberUtil.getTime(requirement.processingTimeMillis));
        itemBuilder.addLore("§fВремя на выполнение: §c" + NumberUtil.getTime(requirement.processingTimeMillis));
        itemBuilder.addLore("");

        itemBuilder.addLore("");
        itemBuilder.addLore("§8Список задач:");

        for (QuestTask questTask : getTasks()) {

            QuestDisplay display = questTask.getDisplay();
            QuestProgress progress = questTask.getProgress();

            itemBuilder.addLore(" §8" + questTask.getId() + ". §f" + display.title + " §b("
                    + progress.getPlayerProgress(this, corePlayer.getPlayerId()) + "/" + progress.getMaxValue() + ")");
        }

        itemBuilder.addLore("");
        itemBuilder.addLore("§8Награда:");

        for (QuestReward questReward : getRewards()) {
            itemBuilder.addLore(" §8■ §e" + questReward.title);
        }

        itemBuilder.addLore("");
        return itemBuilder.build();
    }

}
