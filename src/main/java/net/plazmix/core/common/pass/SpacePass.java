package net.plazmix.core.common.pass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.plazmix.core.api.utility.DateUtil;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class SpacePass {

    public static final String SEASON_TITLE         = ("§b§lСЕЗОН 1: НОВОГОДНИЙ СЕЗОН");

    public static final long START_SEASON_MILLIS    = DateUtil.parsePatternToMillis("dd.MM.yy", "01.01.22");
    public static final long END_SEASON_MILLIS      = DateUtil.parsePatternToMillis("dd.MM.yy", "28.02.22");


    private final int playerId;

    private final Timestamp purchaseDate;

    private int experience;
    private boolean activated;


    public void purchaseActivation() {
        this.activated = true;

        SpacePassSqlHandler.INSTANCE.purchaseActivation(playerId);
    }


}
