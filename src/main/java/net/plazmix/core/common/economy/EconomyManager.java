package net.plazmix.core.common.economy;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.common.economy.service.PlazmaEconomyService;
import net.plazmix.core.connection.player.CorePlayer;

@Getter
@Deprecated
public final class EconomyManager {

    public static final EconomyManager INSTANCE = new EconomyManager();

    /**
     * Изменить баланс коинов игроку
     *
     * @param corePlayer - игрок
     * @param newCoinsBalance - новый баланс коинов игрока
     */
    @Deprecated
    public void changePlayerCoins(@NonNull CorePlayer corePlayer, int newCoinsBalance) {
    }

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
     * @param corePlayer - игрок
     * @param newPlazmaBalance - новый баланс коинов игрока
     */
    public void changePlayerPlazma(@NonNull CorePlayer corePlayer, int newPlazmaBalance) {
        PlazmaEconomyService.getInstance().set(corePlayer.getName(), newPlazmaBalance);
    }

    /**
     * Изменить баланс коинов игроку
     *
     * @param playerName - ник игрока
     * @param newPlazmaBalance - новый баланс коинов игрока
     */
    public void changePlayerPlazma(@NonNull String playerName, int newPlazmaBalance) {
        PlazmaEconomyService.getInstance().set(playerName, newPlazmaBalance);
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
     * Получить количество коинов игрока
     *
     * @param corePlayer - игрок
     */
    @Deprecated
    public int getPlayerCoins(@NonNull CorePlayer corePlayer) {
        return -1;
    }

    /**
     * Получить количество голды игрока
     *
     * @param playerName - ник игрока
     */
    public int getPlayerPlazma(@NonNull String playerName) {
        return PlazmaEconomyService.getInstance().get(playerName);
    }

    /**
     * Получить количество голды игрока
     *
     * @param corePlayer - игрок
     */
    public int getPlayerPlazma(@NonNull CorePlayer corePlayer) {
        return this.getPlayerPlazma(corePlayer.getName());
    }

}
