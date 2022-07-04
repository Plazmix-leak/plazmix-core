package net.plazmix.core.connection.server.impl;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.api.command.sender.ConsoleCommandSender;
import net.plazmix.core.api.event.impl.InventoryClickEvent;
import net.plazmix.core.api.event.impl.InventoryCloseEvent;
import net.plazmix.core.api.event.impl.PlayerChatEvent;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.inventory.BaseInventoryItem;
import net.plazmix.core.common.achievement.Achievement;
import net.plazmix.core.common.achievement.AchievementManager;
import net.plazmix.core.common.achievement.AchievementTask;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.common.economy.service.PlazmaEconomyService;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.client.*;
import net.plazmix.core.connection.protocol.server.SBukkitCommandsPacket;
import net.plazmix.core.connection.protocol.server.SBungeeServerCreatePacket;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.core.protocol.ChannelWrapper;

import java.net.InetSocketAddress;
import java.util.Collection;

@Log4j2
public class BukkitServer extends AbstractServer {

    public BukkitServer(@NonNull String serverName,
                        @NonNull String motd,
                        @NonNull InetSocketAddress socketAddress,

                        @NonNull MinecraftVersion minecraftVersion,
                        @NonNull ChannelWrapper channelWrapper) {

        super(serverName, motd, socketAddress, minecraftVersion, channelWrapper);

        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();
        serverManager.addBukkit(this);
    }

    @Override
    public Collection<CorePlayer> getOnlinePlayers() {
        return PlazmixCore.getInstance().getOnlinePlayers(
                corePlayer -> corePlayer.getBukkitServer() != null && corePlayer.getBukkitServer().getName().equalsIgnoreCase(name)
        );
    }


    // <--------------------------------------------------> // HANDLE PROTOCOL // <--------------------------------------------------> //

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        super.channelActive(wrapper);

        log.info("[Bukkit] {} (v{}) was successfully connected", name, getMinecraftVersionName());

        //wrapper.setCompression(256);


        wrapper.write(new SBukkitCommandsPacket());

        PlazmixCore.getInstance().broadcastBungeePacket(new SBungeeServerCreatePacket(getName(), getServerHost(), getServerPort()));
        sendOnlineUpdatePacket();
    }


    public void handle(@NonNull CAchievementRegisterPacket packet) {
        Achievement achievement = Achievement.create(packet.getId(), packet.getIcon());

        for (String rewardTitle : packet.getRewardsTitles()) {
            achievement.addReward(Achievement.AchievementReward.of(rewardTitle, null));
        }

        for (AchievementTask achievementTask : packet.getTasks()) {
            achievement.addTask(achievementTask);
        }

        AchievementManager.INSTANCE.registerAchievement(achievement);
    }

    public void handle(@NonNull CPlayerChatPacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(packet.getPlayerName());

        PlayerChatEvent playerChatEvent = new PlayerChatEvent(corePlayer, packet.getChatMessageType(), TextComponent.toLegacyText(packet.getBaseComponents()), this);
        PlazmixCore.getInstance().getEventManager().callEvent(playerChatEvent);
    }

    public void handle(@NonNull CPlayerCommandPacket packet) {
        String playerName = packet.getPlayerName();
        String command = packet.getCommand();

        if (playerName.equalsIgnoreCase("%console%")) {
            PlazmixCore.getInstance().getCommandManager().dispatchCommand(
                    ConsoleCommandSender.getInstance(), command
            );

            return;
        }

        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(playerName);

        if (corePlayer == null) {
            return;
        }

        PlazmixCore.getInstance().getCommandManager().dispatchCommand(corePlayer, command);
    }

    public void handle(@NonNull CInventoryClosePacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(packet.getPlayerName());

        if (corePlayer == null) {
            return;
        }

        BaseInventory baseInventory = corePlayer.getOpenedInventory();

        if (baseInventory == null) {
            return;
        }

        PlazmixCore.getInstance().getEventManager().callEvent(new InventoryCloseEvent(corePlayer, baseInventory));
    }

    public void handle(@NonNull CInventoryInteractPacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(packet.getPlayerName());
        BaseInventory baseInventory = corePlayer.getOpenedInventory();

        if (baseInventory == null) {
            return;
        }

        BaseInventoryItem baseInventoryItem = baseInventory.getInventoryInfo().getItem(packet.getInventorySlot());
        if (baseInventoryItem != null) {

            InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(corePlayer, baseInventory, packet.getMouseAction(), packet.getInventorySlot());
            PlazmixCore.getInstance().getEventManager().callEvent(inventoryClickEvent);
        }
    }

    public void handle(@NonNull CPlayerEconomyUpdatePacket packet) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(packet.getPlayerName());

        PlazmaEconomyService.getInstance().updateCacheData(playerId, packet.getGolds());
    }

    public void handle(@NonNull CPlayerLevelUpdatePacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(packet.getPlayerName());

        corePlayer.setLevel(packet.getLevel());
        corePlayer.setExperience(packet.getExperience());
        corePlayer.setMaxExperience(packet.getMaxExperience());
    }

    public void handle(CPlayerStatisticPacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(packet.getPlayerName());
        corePlayer.setStatistic(this, packet.getStatistic(), packet.getValue());
    }

}
