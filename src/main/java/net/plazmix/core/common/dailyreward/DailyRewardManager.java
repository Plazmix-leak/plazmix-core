package net.plazmix.core.common.dailyreward;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;

public final class DailyRewardManager {

    public static final DailyRewardManager INSTANCE = new DailyRewardManager();

    @Getter private final LinkedList<DailyRewardInfo> dailyRewardInfos  = new LinkedList<>();
    @Getter private final TIntIntMap playerLastRewardsMap               = new TIntIntHashMap();


    public void addDailyReward(int dayIndex, DailyReward... dailyRewards) {
        DailyRewardInfo dailyRewardInfo = new DailyRewardInfo(dayIndex);
        dailyRewardInfo.getDailyRewards().addAll(Arrays.asList(dailyRewards));

        dailyRewardInfos.add(dailyRewardInfo);
    }

}
