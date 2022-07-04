package net.plazmix.guilds.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.guild.CoreGuild;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public enum GuildsListSort {

    BY_MONEY("По монетам", coins -> "§7Монет: §a" + NumberUtil.spaced(coins), guild -> guild.getEconomy().getCoins()),
    BY_GOLDS("По плазме", golds -> "§7Плазмы: §e" + NumberUtil.spaced(golds), guild -> guild.getEconomy().getGolds()),
    BY_EXP("По опыту", experience -> "§7Опыта: §d" + NumberUtil.spaced(experience), guild -> guild.getEconomy().getExperience()),
    BY_MEMBERS("По количеству участников", members -> "§7Количество участников: §f" + NumberUtil.spaced(members), guild -> guild.getMemberIdsMap().size()),
    ;

    private final String sortingName;

    private final Function<Integer, String> loreFunction;
    private final Function<CoreGuild, Integer> sortFunction;


    public GuildsListSort next() {
        if (ordinal() == values().length - 1)
            return values()[0];

        return values()[ordinal() + 1];
    }

    public GuildsListSort back() {
        if (ordinal() <= 0)
            return values()[values().length - 1];

        return values()[ordinal() - 1];
    }
}
