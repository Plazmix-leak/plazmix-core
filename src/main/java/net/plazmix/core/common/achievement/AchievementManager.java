package net.plazmix.core.common.achievement;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

public final class AchievementManager {

    public static final AchievementManager INSTANCE = new AchievementManager();

    private final TIntObjectMap<Achievement> questsMap = new TIntObjectHashMap<>();


    public Collection<Achievement> getRegisteredAchievements() {
        return questsMap.valueCollection();
    }

    public void registerAchievement(Achievement achievement) {
        questsMap.put(achievement.getId(), achievement);
    }

    public Achievement achievement(int achievementID) {
        return questsMap.get(achievementID);
    }

}
