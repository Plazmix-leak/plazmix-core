package net.plazmix.coreconnector.core.economy;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.coreconnector.module.type.economy.service.PlazmaEconomyService;

@Deprecated
@Getter
public final class EconomyManager {

    public static final EconomyManager INSTANCE = new EconomyManager();

    /**
     * Изменить баланс коинов игроку
     *
     * @param playerName - ник игрока
     * @param newCoinsBalance - новый баланс коинов игрока
     */
    @Deprecated
    public void changePlayerCoins(@NonNull String playerName, int newCoinsBalance) {
    }

    /**
     * Изменить баланс коинов игроку
     *
     * @param playerName - ник игрока
     * @param newGoldsBalance - новый баланс коинов игрока
     */
    @Deprecated
    public void changePlayerGolds(@NonNull String playerName, int newGoldsBalance) {
        PlazmaEconomyService.getInstance().set(playerName, newGoldsBalance);
    }

    /**
     * Получить количество коинов игрока
     *
     * @param playerName - ник игрока
     */
    @Deprecated
    public int getPlayerCoins(@NonNull String playerName) {
        return -1;
    }

    /**
     * Получить количество голды игрока
     *
     * @param playerName - ник игрока
     */
    @Deprecated
    public int getPlayerGolds(@NonNull String playerName) {
        return PlazmaEconomyService.getInstance().get(playerName);
    }
}
