package net.plazmix.coreconnector.module.type.coloredprefix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.module.BaseModuleManager;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.module.ModuleExecuteType;
import net.plazmix.coreconnector.module.type.NetworkModule;

public final class ColoredPrefixModule extends BaseServerModule {

    public static final String PREFIX_COLOR_KEY     = "PREFIX_COLOR";

    public ColoredPrefixModule() {
        super("ColoredPrefix");

        container.setOnReadPacketKey(PREFIX_COLOR_KEY, ChatColor.class);
    }

    @Override
    public void onValueRead(ModuleExecuteType executeType, String key, Object value) {
        int playerId = Integer.parseInt(key.substring(PREFIX_COLOR_KEY.length() + 1));

        switch (executeType) {
            case INSERT: {

                if (value == null) {
                    return;
                }

                Bukkit.getPluginManager().callEvent(new PlayerPrefixColorChangeEvent(
                        NetworkModule.getInstance().getPlayerName(playerId), getPrefixColor(playerId)
                ));

                break;
            }

            case DELETE: {

                Bukkit.getPluginManager().callEvent(new PlayerPrefixColorResetEvent(NetworkModule.getInstance().getPlayerName(playerId)));
                break;
            }
        }
    }

    public ChatColor getPrefixColor(int playerId) {
        if (playerId <= 0) {
            return null;
        }

        return container.read(ChatColor.class, PREFIX_COLOR_KEY + "_" + playerId);
    }

    public ChatColor getPrefixColor(String playerName) {
        int playerId = BaseModuleManager.INSTANCE.find(NetworkModule.class).getPlayerId(playerName);
        return getPrefixColor(playerId);
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
