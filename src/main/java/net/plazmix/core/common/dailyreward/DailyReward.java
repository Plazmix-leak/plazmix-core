package net.plazmix.core.common.dailyreward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class DailyReward {

    private final String display;
    private final Consumer<CorePlayer> playerConsumer;
}