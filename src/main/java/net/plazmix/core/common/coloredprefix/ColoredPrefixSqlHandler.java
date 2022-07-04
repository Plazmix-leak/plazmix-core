package net.plazmix.core.common.coloredprefix;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.network.NetworkManager;

public final class ColoredPrefixSqlHandler {

    public static final String INSERT_QUERY = "INSERT INTO `PlayerColoredPrefix` VALUES (?, ?) ON DUPLICATE KEY UPDATE `Code`=?";
    public static final String SELECT_QUERY = "SELECT * FROM `PlayerColoredPrefix` WHERE `Id`=?";
    public static final String DELETE_QUERY = "DELETE FROM `PlayerColoredPrefix` WHERE `Id`=?";

    public static final ColoredPrefixSqlHandler INSTANCE = new ColoredPrefixSqlHandler();

    @Getter
    private final TIntObjectMap<ChatColor> playerPrefixColorsMap = new TIntObjectHashMap<>();


    public void setPrefixColor(int playerId, ChatColor chatColor) {
        if (playerId <= 0) {
            return;
        }

        if (chatColor == null) {
            playerPrefixColorsMap.remove(playerId);

            PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.DELETE, "ColoredPrefix", "PREFIX_COLOR_" + playerId, null);
            PlazmixCore.getInstance().getMysqlConnection().execute(true, DELETE_QUERY, playerId);
            return;
        }

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "ColoredPrefix", "PREFIX_COLOR_" + playerId, chatColor);

        String colorCode = Character.toString(chatColor.getCode());
        PlazmixCore.getInstance().getMysqlConnection().execute(true, INSERT_QUERY, playerId, colorCode, colorCode);

        playerPrefixColorsMap.put(playerId, chatColor);
    }

    public void setPrefixColor(String playerName, ChatColor chatColor) {
        setPrefixColor(NetworkManager.INSTANCE.getPlayerId(playerName), chatColor);
    }

    public ChatColor getPrefixColor(int playerId) {
        ChatColor chatColor = playerPrefixColorsMap.get(playerId);

        if (chatColor == null) {
            chatColor = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, SELECT_QUERY, result -> {

                if (!result.next()) {
                    return null;
                }

                return ChatColor.getByChar(result.getString("Code").charAt(0));

            }, playerId);

            playerPrefixColorsMap.put(playerId, chatColor);
        }

        return chatColor;
    }

    public ChatColor getPrefixColor(@NonNull String playerName) {
        return getPrefixColor(NetworkManager.INSTANCE.getPlayerId(playerName));
    }


    public String format(ChatColor chatColor, Group group) {
        return chatColor + ChatColor.BOLD.toString() + ChatColor.stripColor(group.getPrefix()) + chatColor;
    }

    public String format(int playerId, Group group) {
        return format(getPrefixColor(playerId), group);
    }

    public String format(String playerName, Group group) {
        return format(getPrefixColor(playerName), group);
    }

}
