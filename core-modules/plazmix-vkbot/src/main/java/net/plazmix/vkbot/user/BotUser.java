package net.plazmix.vkbot.user;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.vkbot.api.handler.SessionMessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class BotUser {

    /**
     * Пустой юзер, используется для тех кто еще не зареган, чтобы не возвращать NULL
     */
    public static final BotUser EMPTY_BOT_USER = new BotUser(-1, null);

    @Getter
    private final int vkId;

    /**
     * Имя основного аккаунта, используемоего на сервере
     */
    @Getter
    private String primaryAccountName;

    /**
     * Добавить новый привязанный аккаунт к VK
     *
     * @param playerName - ник игрока
     */
    public void addLinkedAccount(@NonNull String playerName) {
        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `VkId`=? WHERE `Id`=?",
                vkId, NetworkManager.INSTANCE.getPlayerId(playerName));

        this.primaryAccountName = playerName;
    }

    /**
     * Удалить привязанный к VK аккаунт
     */
    public void removeLinkedAccount() {
        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `VkId`=? WHERE `Id`=?",
                -1, NetworkManager.INSTANCE.getPlayerId(primaryAccountName));

        this.primaryAccountName = null;
    }

    /**
     * Проверить, привязан ли к данному пользователю VK данный аккаунт
     *
     * @param playerName - ник игрока
     * @return - true, если аккаунт привязан
     * false, если нет
     */
    public boolean hasLinkedAccount(@NonNull String playerName) {
        return hasPrimaryAccount() && primaryAccountName.equalsIgnoreCase(playerName);
    }

    /**
     * Проверить, установлен ли у игрока основной аккаунт
     *
     * @return - установлен ли у игрока основной аккаунт
     */
    public boolean hasPrimaryAccount() {
        return primaryAccountName != null;
    }


    public static final TIntObjectMap<BotUser> VK_USER_MAP = new TIntObjectHashMap<>();

    private static BotUser getVkUser(String playerName, int vkId) {
        BotUser botUser = VK_USER_MAP.get(vkId);

        if (botUser == null) {
            VK_USER_MAP.put(vkId, (botUser = new BotUser(vkId, null)));
        }

        if (playerName != null && !botUser.hasPrimaryAccount()) {
            botUser.addLinkedAccount(playerName);
        }

        return botUser;
    }

    public static BotUser getVkUser(int vkId) {
        if (VK_USER_MAP.containsKey(vkId))
            return VK_USER_MAP.get(vkId);

        String playerName = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerAuth` WHERE `VkId`=?", resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            return NetworkManager.INSTANCE.getPlayerName(resultSet.getInt("Id"));
        }, vkId);

        return getVkUser(playerName, vkId);
    }

    private final Map<String, SessionMessageHandler> sessionMessageHandlerMap = new ConcurrentHashMap<>();

    public boolean hasMessageHandlers() {
        return !sessionMessageHandlerMap.isEmpty();
    }

    public void handleMessageHandlers(@NonNull String messageBody) {
        for (SessionMessageHandler sessionMessageHandler : sessionMessageHandlerMap.values()) {
            sessionMessageHandler.onMessage(messageBody);
        }
    }

    public void createSessionMessageHandler(@NonNull String handlerTempName, @NonNull SessionMessageHandler sessionMessageHandler) {
        sessionMessageHandlerMap.put(handlerTempName.toLowerCase(), sessionMessageHandler);
    }

    public void closeSessionMessageHandler(@NonNull String handlerTempName) {
        sessionMessageHandlerMap.remove(handlerTempName.toLowerCase());
    }

}

