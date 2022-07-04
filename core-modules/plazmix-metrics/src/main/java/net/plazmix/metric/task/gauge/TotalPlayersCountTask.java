package net.plazmix.metric.task.gauge;

import net.plazmix.core.PlazmixCore;
import net.plazmix.metric.task.GaugeMetricTask;

public final class TotalPlayersCountTask extends GaugeMetricTask {

    public static final String SELECT_QUERY = "SELECT COUNT(*) AS `count` FROM `PlayerAuth`;";

    public TotalPlayersCountTask() {
        super("total_players_count");
    }

    @Override
    public int getValue() {
        return PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, SELECT_QUERY,
                resultSet -> {

            resultSet.next();
            return resultSet.getInt("count");
        });
    }

}
