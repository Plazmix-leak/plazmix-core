package net.plazmix.core.common.achievement;

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
public class Achievement {

    public static Achievement create(int id, Material icon) {
        return new Achievement(id, icon);
    }

    @Setter
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AchievementTaskData {

        static AchievementTaskData fromAchievement(Achievement achievement, int taskId, int playerId) {
            AchievementTask achievementTask = achievement.task(taskId);
            return new AchievementTaskData(achievementTask.getId(), achievementTask.getProgress().getPlayerProgress(achievement, playerId));
        }

        static AchievementTaskData fromJson(String json) {
            return JsonUtil.fromJson(json, AchievementTaskData.class);
        }


        final int taskId;
        int progress;

        public String toJson() {
            return JsonUtil.toJson(this);
        }
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class AchievementDisplay {

        String title;
    }

    @Getter
    @Value(staticConstructor = "of")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class AchievementReward {

        String title;
        Consumer<CorePlayer> handler;
    }

    int id;

    @NonNull
    Material material;

    @NonNull
    List<AchievementTask> tasks = new ArrayList<>();

    @NonNull
    List<AchievementReward> rewards = new ArrayList<>();


    public AchievementTask task(int taskId) {
        for (AchievementTask achievementTask : getTasks()) {

            if (achievementTask.getId() == taskId) {
                return achievementTask;
            }
        }

        return null;
    }

    public Achievement addTask(AchievementTask achievementTask) {
        tasks.add(achievementTask);
        return this;
    }

    public Achievement addReward(AchievementReward achievementReward) {
        rewards.add(achievementReward);
        return this;
    }

    public void initPlayerData(int playerId) {

        for (AchievementTask achievementTask : getTasks()) {
            achievementTask.getCachedPlayerData(this, playerId);
        }
    }

    public void updatePlayerData(int playerId) {

        for (AchievementTask achievementTask : getTasks()) {
            achievementTask.updatePlayerData(this, playerId, achievementTask.getCachedPlayerData(this, playerId));
        }
    }

    // TODO: Использовать этот метод при каждом открытии гуи с ачивками игроку.
    public void cleanupPlayerData(int playerId) {

        for (AchievementTask achievementTask : getTasks()) {
            achievementTask.getPlayerDataMap().remove(playerId);
        }
    }

    public void acceptRewards(CorePlayer corePlayer) {
        for (AchievementReward achievementReward : rewards) {

            if (achievementReward.handler != null) {
                achievementReward.handler.accept(corePlayer);
            }
        }
    }

    public ItemStack toItemStack(CorePlayer corePlayer) {
        ItemBuilder itemBuilder = ItemBuilder.newBuilder(material);
        itemBuilder.setDisplayName(ChatColor.YELLOW + "Ачивка #" + id);

        itemBuilder.addLore("");
        itemBuilder.addLore("§8Список задач:");

        for (AchievementTask achievementTask : getTasks()) {

            AchievementDisplay display = achievementTask.getDisplay();
            AchievementProgress progress = achievementTask.getProgress();

            itemBuilder.addLore(" §8" + achievementTask.getId() + ". §f" + display.title + " §b("
                    + progress.getPlayerProgress(this, corePlayer.getPlayerId()) + "/" + progress.getMaxValue() + ")");
        }

        itemBuilder.addLore("");
        itemBuilder.addLore("§8Награда:");

        for (AchievementReward achievementReward : getRewards()) {
            itemBuilder.addLore(" §8■ §e" + achievementReward.title);
        }

        itemBuilder.addLore("");
        return itemBuilder.build();
    }

}
