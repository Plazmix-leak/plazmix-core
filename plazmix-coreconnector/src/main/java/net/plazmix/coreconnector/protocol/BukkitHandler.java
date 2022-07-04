package net.plazmix.coreconnector.protocol;

import lombok.NonNull;
import net.plazmix.coreconnector.direction.bukkit.inventory.ShapedCoreInventory;
import net.plazmix.coreconnector.module.type.economy.service.PlazmaEconomyService;
import net.plazmix.coreconnector.utility.leveling.LevelingSqlHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.auth.AuthManager;
import net.plazmix.coreconnector.core.auth.AuthPlayer;
import net.plazmix.coreconnector.core.economy.EconomyManager;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import net.plazmix.coreconnector.direction.bukkit.event.PlayerAuthCompleteEvent;
import net.plazmix.coreconnector.direction.bukkit.event.PlayerLanguageChangeEvent;
import net.plazmix.coreconnector.protocol.client.*;
import net.plazmix.coreconnector.protocol.server.SInventoryInteractPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerLevelUpdatePacket;
import net.plazmix.coreconnector.utility.StringUtils;

public class BukkitHandler extends AbstractServerHandler {

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        Bukkit.getLogger().info(ChatColor.GREEN + "[Core] Success connected as " + CoreConnector.getInstance().getServerName());

        super.channelActive(wrapper);
    }

    @Override
    public void channelInactive() {
        Bukkit.getLogger().info(ChatColor.RED + "[Core] Connection refused: DISCONNECTED");

        super.channelInactive();
    }


    public void handle(@NonNull CBukkitCommandsPacket packet) {

        BukkitConnectorPlugin.setCommandList(packet.getCommandCollection());

        for (String command : packet.getCommandCollection()) {
            Bukkit.getLogger().info("[Core] Command success registered: " + command);
        }
    }

    public void handle(@NonNull CInventoryClosePacket packet) {
        Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> {
            Player player = Bukkit.getPlayer(packet.getPlayerName());

            if (player != null) {
                player.closeInventory();
            }
        });
    }

    public void handle(@NonNull CInventorySetItemPacket packet) {
        Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> {
            Player player = Bukkit.getPlayer(packet.getPlayerName());

            if (player != null) {
                ShapedCoreInventory shapedCoreInventory = ShapedCoreInventory.getInventory(player);

                if (shapedCoreInventory != null) {
                    shapedCoreInventory.setPacketItem(packet);
                }
            }
        });
    }

    public void handle(@NonNull CInventoryOpenPacket packet) {
        ShapedCoreInventory.openInventory(packet);
    }

    public void handle(@NonNull CInventoryClearPacket packet) {
        Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> {
            Player player = Bukkit.getPlayer(packet.getPlayerName());

            if (player != null && player.isOnline()) {
                ShapedCoreInventory shapedCoreInventory = ShapedCoreInventory.getInventory(player);

                if (shapedCoreInventory != null) {

                    shapedCoreInventory.getButtonActionsMap().clear();
                    shapedCoreInventory.getItemContainer().clear();
                }
            }
        });
    }

    public void handle(@NonNull CPlayerSoundPacket packet) {
        Player player = Bukkit.getPlayer(packet.getPlayerName());

        if (player != null) {
            player.playSound(player.getLocation(), packet.getSoundType(), packet.getVolume(), packet.getPitch());
        }
    }

    public void handle(@NonNull CPlayerAuthCompletePacket packet) {
        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(packet.getPlayerName());

        PlayerAuthCompleteEvent authCompleteEvent = new PlayerAuthCompleteEvent(authPlayer);
        Bukkit.getPluginManager().callEvent(authCompleteEvent);
    }

    public void handle(@NonNull CPlayerChatPacket packet) {
        Player player = Bukkit.getPlayer(packet.getPlayerName());

        if (player != null && player.isOnline()) {
            switch (packet.getChatMessageType()) {

                case CHAT:
                case SYSTEM: {
                    player.spigot().sendMessage(packet.getBaseComponents());
                    break;
                }

                case ACTION_BAR: {
                    // player.spigot().sendMessage(packet.getChatMessageType(), packet.getBaseComponents());
                    break;
                }
            }
        }
    }

    public void handle(@NonNull CPlayerCommandPacket packet) {
        String playerName = packet.getPlayerName();

        if (!playerName.equals("%console%")) {

            if (Bukkit.getPlayer(playerName) != null) {
                Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getPlayer(playerName), packet.getCommand()));
            }

        } else {

            Bukkit.getScheduler().runTask(BukkitConnectorPlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), packet.getCommand()));
        }
    }

    public void handle(@NonNull CPlayerEconomyUpdatePacket packet) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName());

        PlazmaEconomyService.getInstance().updateCacheData(playerId, packet.getGolds());
    }

    public void handle(@NonNull CPlayerGroupUpdatePacket packet) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName());

        NetworkManager.INSTANCE.getPlayerGroupMap().put(playerId, packet.getPlayerGroup());
    }

    public void handle(@NonNull CPlayerLevelUpdatePacket packet) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName());

        LevelingSqlHandler.INSTANCE.playerExperienceMap.put(playerId, packet.getExperience());
    }

    @Override
    public void handle(@NonNull CPlayerLocaleUpdatePacket packet) {

        LanguageType languageFrom = LanguageManager.INSTANCE.getPlayerLanguage(packet.getPlayerName());
        LanguageType languageTo = LanguageType.VALUES[packet.getLanguageIndex()];

        if (languageTo.equals(languageFrom)) {
            return;
        }

        // Update player language.
        LanguageManager.INSTANCE.getPlayerLanguageMap().put(NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName()), languageTo);

        // Call the event.
        PlayerLanguageChangeEvent event = new PlayerLanguageChangeEvent(packet.getPlayerName(), languageFrom, languageTo);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void handle(@NonNull CRestartServerPacket packet) {
        Bukkit.shutdown();
    }

    public void handle(CPlayerTagPrefixUpdatePacket packet) {
       //Player player = Bukkit.getPlayer(packet.getPlayerName());

       //if (player != null) {
       //    String teamName = player.getName();
       //    ProtocolTeam protocolTeam = ProtocolTeam.findEntry(player);

       //    if (protocolTeam != null) {
       //        teamName = protocolTeam.getName() + teamName;

       //        protocolTeam.removePlayerEntry(player);
       //    }

       //    protocolTeam = ProtocolTeam.get( StringUtils.fixLength(16, teamName) );
       //    protocolTeam.setPrefix(packet.getPrefix());
       //}
    }

    public void handle(CPlayerTagSuffixUpdatePacket packet) {
       //Player player = Bukkit.getPlayer(packet.getPlayerName());

       //if (player != null) {
       //    String teamName = player.getName();
       //    ProtocolTeam protocolTeam = ProtocolTeam.findEntry(player);

       //    if (protocolTeam != null) {
       //        teamName = protocolTeam.getName() + teamName;

       //        protocolTeam.removePlayerEntry(player);
       //    }

       //    protocolTeam = ProtocolTeam.get( StringUtils.fixLength(16, teamName) );
       //    protocolTeam.setSuffix(packet.getSuffix());
       //}
    }

}
