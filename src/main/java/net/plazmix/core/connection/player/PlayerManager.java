package net.plazmix.core.connection.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.utility.map.MultikeyHashMap;
import net.plazmix.core.api.utility.map.MultikeyMap;
import net.plazmix.core.api.utility.query.AsyncUtil;
import net.plazmix.core.common.language.LanguageManager;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.offline.PlayerOfflineData;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@Log4j2
public final class PlayerManager {

    private final Multimap<String, Supplier<String>> offlineMessageMap      = HashMultimap.create();

    private final Cache<String, CorePlayer> offlinePlayerCache              = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    private final MultikeyMap<CorePlayer> corePlayerMap = new MultikeyHashMap<CorePlayer>()

            .register(String.class, corePlayer -> corePlayer.getName().toLowerCase())
            .register(Integer.class, CorePlayer::getPlayerId);



    /**
     * Подключить игрока к кору, добавив в кеш
     *
     * @param corePlayer - игрок
     */
    public void playerConnect(@NonNull CorePlayer corePlayer) {
        synchronized (corePlayerMap) {

            corePlayer.getPlayerOfflineData().load(null);
            corePlayerMap.put(corePlayer);
        }
    }

    /**
     * Отключить игрока от кора, удалив его из кеша
     *
     * @param corePlayer - игрок
     */
    public void playerDisconnect(@NonNull CorePlayer corePlayer) {
        synchronized (corePlayerMap) {

            corePlayer.getPlayerOfflineData().save();
            corePlayerMap.delete(corePlayer);
        }
    }

    /**
     * Получить кешированного игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public CorePlayer getPlayer(@NonNull String playerName) {
        return corePlayerMap.get(String.class, playerName.toLowerCase());
    }

    /**
     * Получить кешированного игрока по его номеру
     *
     * @param playerId - номер игрока
     */
    public CorePlayer getPlayer(int playerId) {
        return corePlayerMap.get(Integer.class, playerId);
    }

    /**
     * Получить offline данные игрока
     *
     * @param playerName - ник игрокау
     */
    @SneakyThrows
    public CorePlayer getOfflinePlayer(@NonNull String playerName) {
        synchronized (offlinePlayerCache) {
            offlinePlayerCache.cleanUp();

            if (isPlayerOnline(playerName)) {
                return getPlayer(playerName);
            }

            CorePlayer offlinePlayer = offlinePlayerCache.asMap().get(playerName.toLowerCase());

            if (offlinePlayer == null) {
                offlinePlayer = AsyncUtil.supplyAsyncFuture(() -> {

                    CorePlayer corePlayer = new CorePlayer(NetworkManager.INSTANCE.getPlayerId(playerName), playerName, null,
                            NetworkManager.INSTANCE.getPlayerGroup(playerName), PlazmixCore.getInstance().getBestBungee(), null);

                    corePlayer.setLanguageType(LanguageManager.INSTANCE.getPlayerLanguage(playerName));

                    PlayerOfflineData playerOfflineData = corePlayer.getPlayerOfflineData();
                    playerOfflineData.load(offlineData -> corePlayer.setBukkitServer(offlineData.getLastServer()));

                    return corePlayer;
                });

                offlinePlayerCache.put(playerName.toLowerCase(), offlinePlayer);
            }

            return offlinePlayer;
        }
    }

    /**
     * Проверить игровой статус игрока
     *
     * @param playerId - номер игрока
     */
    public boolean isPlayerOnline(int playerId) {
        return corePlayerMap.contains(Integer.class, playerId);
    }

    /**
     * Проверить игровой статус игрока
     *
     * @param playerName - имя игрока
     */
    public boolean isPlayerOnline(@NonNull String playerName) {
        return corePlayerMap.contains(String.class, playerName.toLowerCase());
    }

    /**
     * Отправить offline сообщения
     *
     * @param playerName      - оффлайн игрок
     * @param messageSupplier - обработчик сообщения
     */
    public void sendOfflineMessage(@NonNull String playerName, Supplier<String> messageSupplier) {
        synchronized (offlineMessageMap) {
            CorePlayer onlinePlayer = getPlayer(playerName);

            if (onlinePlayer != null && messageSupplier.get() != null) {
                onlinePlayer.sendMessage(messageSupplier.get());

                return;
            }

            offlineMessageMap.put(playerName.toLowerCase(), messageSupplier);
        }
    }

    /**
     * Получить отфлитрованный список онлайн игроков
     * по какому-то условию
     *
     * @param playerResponseHandler - условие фильтрования онлайн игроков
     */
    public Collection<CorePlayer> getOnlinePlayers(@NonNull PlayerResponseHandler playerResponseHandler) {
        return corePlayerMap.valueCollection().stream().filter(playerResponseHandler::handle)
                .collect(Collectors.toSet());
    }

    public interface PlayerResponseHandler {

        /**
         * Обработать условие фильтрования
         *
         * @param corePlayer - игрок
         */
        boolean handle(@NonNull CorePlayer corePlayer);
    }

}
