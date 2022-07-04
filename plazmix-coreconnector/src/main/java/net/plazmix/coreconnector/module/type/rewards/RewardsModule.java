package net.plazmix.coreconnector.module.type.rewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.module.ModuleExecuteType;
import net.plazmix.coreconnector.module.type.NetworkModule;

public final class RewardsModule
        extends BaseServerModule {

    public static final String PASS_REWARD_KEY = "PASS_REWARD";

    public RewardsModule() {
        super("TynixRewards");

        container.setOnReadPacketKey(PASS_REWARD_KEY, int.class);
    }

    @Override
    public void onValueRead(ModuleExecuteType executeType, String key, Object value) {
        if (key.equalsIgnoreCase(PASS_REWARD_KEY)) {

            String playerName = NetworkModule.getInstance().getPlayerName((int) value);
            Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                Bukkit.getPluginManager().callEvent(new CoreRewardsPassEvent(player));
            }
        }
    }

}
