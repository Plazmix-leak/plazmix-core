package net.plazmix.core.common.dailyreward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@RequiredArgsConstructor
@Getter
public class DailyRewardInfo {

    private final int dayIndex;
    private final LinkedList<DailyReward> dailyRewards = new LinkedList<>();
}
