package net.plazmix.core.connection.player;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.command.CommandSendingType;
import net.plazmix.core.api.event.impl.InventoryCloseEvent;
import net.plazmix.core.api.event.impl.PlayerServerPreRedirectEvent;
import net.plazmix.core.api.inventory.BaseInventory;
import net.plazmix.core.api.sounds.SoundType;
import net.plazmix.core.api.utility.Statistic;
import net.plazmix.core.common.coloredprefix.ColoredPrefixSqlHandler;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.language.LocalizationResource;
import net.plazmix.core.common.pass.SpacePass;
import net.plazmix.core.common.pass.SpacePassSqlHandler;
import net.plazmix.core.connection.player.offline.PlayerOfflineData;
import net.plazmix.core.connection.protocol.server.*;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Log4j2
public class CorePlayer implements CommandSender {

    private final int playerId;
    private final String name;

    private final UUID uniqueId;

    @Setter
    private LanguageType languageType;
    private final Group group;

    @Setter
    private BukkitServer bukkitServer;

    private final BungeeServer bungeeServer;
    private final CommandSendingType commandSendingType = CommandSendingType.PLAYER;

    private final InetSocketAddress inetSocketAddress;
    private final PlayerOfflineData playerOfflineData = new PlayerOfflineData(this);

    private final Table<BukkitServer, Statistic, Integer> playerStatistics
            = HashBasedTable.create();

    @Setter
    private int level, experience, maxExperience, versionId;

    @Setter
    private boolean premiumAccount;


    /**
     * Проверить игрока на существование данных
     * в строках базы данных
     */
    public boolean hasIdentifier() {
        return playerId >= 0;
    }

    public SpacePass getPass() {
        return SpacePassSqlHandler.INSTANCE.getPlayerPass(playerId);
    }

    /**
     * Получить версию игрока
     */
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.getByVersionId(versionId);
    }

    /**
     * Получить парснутую версию игрока
     */
    public String getMinecraftVersionName() {
        return getMinecraftVersion().toClientName();
    }

    public void setStatistic(@NonNull BukkitServer bukkitServer, @NonNull Statistic statistic, int value) {
        playerStatistics.put(bukkitServer, statistic, value);
    }

    public int getStatistic(@NonNull BukkitServer bukkitServer, @NonNull Statistic statistic) {
        return playerStatistics.get(bukkitServer, statistic);
    }

    /**
     * Получить выводимое имя игрока
     * с добавлением префикса и суффикса
     */
    public String getDisplayName() {
        ChatColor prefixColor = ColoredPrefixSqlHandler.INSTANCE.getPrefixColor(playerId);
        String prefix = prefixColor == null ? group.getPrefix() : ColoredPrefixSqlHandler.INSTANCE.format(prefixColor, group);

        return prefix + (group.isDefault() ? "" : " ") + (getPass().isActivated() ? "✬" : "") + name + group.getSuffix();
    }

    @Override
    public void sendMessage(@NonNull ChatMessageType chatMessageType, BaseComponent[] baseComponents) {
        if (!isOnline()) {
            return;
        }

        getBukkitServer().sendPacket(new SPlayerChatPacket(chatMessageType, name, baseComponents));
    }

    public void dispatchCommand(@NonNull String command) {
        String commandLabel = command.replaceFirst("/", "").split(" ")[0];

        if (PlazmixCore.getInstance().getCommandManager().commandIsExists(commandLabel)) {
            PlazmixCore.getInstance().getCommandManager().dispatchCommand(this, command);

        } else {

            getBukkitServer().dispatchCommand(name, command);
        }
    }

    /**
     * Получить количество монет у игрока
     */
    public int getCoins() {
        return EconomyManager.INSTANCE.getPlayerCoins(this);
    }

    public void setCoins(int coins) {
        EconomyManager.INSTANCE.changePlayerCoins(this, coins);
    }

    public void addCoins(int coins) {
        setCoins(getCoins() + coins);
    }

    public void takeCoins(int coins) {
        setCoins(getCoins() - coins);
    }


    /**
     * Получить количество плазмы у игрока
     */
    public int getPlazma() {
        return EconomyManager.INSTANCE.getPlayerPlazma(this);
    }

    public void setPlazma(int plazma) {
        EconomyManager.INSTANCE.changePlayerPlazma(this, plazma);
    }

    public void addPlazma(int plazma) {
        setPlazma(getPlazma() + plazma);
    }

    public void takePlazma(int plazma) {
        setPlazma(getPlazma() - plazma);
    }


    public void dispatchServerCommand(@NonNull String command) {
        getBukkitServer().dispatchCommand(getName(), command);
    }

    public void dispatchCoreCommand(@NonNull String command) {
        PlazmixCore.getInstance().getCommandManager().dispatchCommand(this, command);
    }

    /**
     * Получить текущий сервер игрока
     * (если он не онлай игрок, то последний его сервер)
     */
    public BukkitServer getBukkitServer() {
        return !isOnline() ? (bukkitServer = playerOfflineData.getLastServer()) : bukkitServer;
    }


    /**
     * Установить указанный префикс в тег игрока
     *
     * @param prefix   - префикс
     * @param isPublic - разрешение на публичность
     */
    public void setTagPrefix(@NonNull String prefix, boolean isPublic) {
        getBukkitServer().sendPacket(new SPlayerTagPrefixUpdatePacket(name, prefix, isPublic));
    }

    /**
     * Установить указанный префикс в тег игрока
     *
     * @param prefix   - префикс
     */
    public void setTagPrefix(@NonNull String prefix) {
        setTagPrefix(prefix, true);
    }


    /**
     * Установить указанный суффикс в тег игрока
     *
     * @param suffix   - суффикс
     * @param isPublic - разрешение на публичность
     */
    public void setTagSuffix(@NonNull String suffix, boolean isPublic) {
        getBukkitServer().sendPacket(new SPlayerTagSuffixUpdatePacket(name, suffix, isPublic));
    }

    /**
     * Установить указанный суффикс в тег игрока
     *
     * @param suffix   - суффикс
     */
    public void setTagSuffix(@NonNull String suffix) {
        setTagSuffix(suffix, true);
    }


    /**
     * Подключить игрока на другой сервер
     *
     * @param bukkitServer - сервер
     */
    public void connectToServer(@NonNull BukkitServer bukkitServer) {
        if (!isOnline()) {
            return;
        }

        PlayerServerPreRedirectEvent preRedirectEvent = new PlayerServerPreRedirectEvent(this, this.bukkitServer, bukkitServer);
        PlazmixCore.getInstance().getEventManager().callEvent(preRedirectEvent);

        if (preRedirectEvent.isCancelled()) {
            return;
        }

        if (bungeeServer != null) {
            bungeeServer.sendPacket(new SPlayerServerRedirectPacket(name, bukkitServer.getName()));
        }
    }

    public void connect(@NonNull ServerMode serverMode) {
        if (!isOnline()) {
            return;
        }

        connectToAnyServer(serverMode.getServersPrefix());
    }

    public void connect(@NonNull ServerMode serverMode, @NonNull ServerSubModeType serverSubModeType) {
        if (!isOnline()) {
            return;
        }

        serverMode.getSubModes(serverSubModeType).stream().findFirst()
                .ifPresent(serverSubMode -> connectToAnyServer(serverSubMode.getSubPrefix()));
    }

    /**
     * Подключить игрока на другой сервер
     *
     * @param serverName - сервер
     */
    public void connectToServer(@NonNull String serverName) {
        if (!isOnline()) {
            return;
        }

        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(serverName);
        Objects.requireNonNull(bukkitServer);

        connectToServer(bukkitServer);
    }

    /**
     * Подключить игрока на другой сервер
     *
     * @param serverPrefix - префикс
     */
    public void connectToAnyServer(@NonNull String serverPrefix) {
        if (!isOnline()) {
            return;
        }

        BukkitServer bukkitServer = PlazmixCore.getInstance().getRandomServerByPrefix(serverPrefix);

        if (bukkitServer == null) {
            return;
        }

        connectToServer(bukkitServer);
    }

    /**
     * Выкинуть игрока с сервера по указанной причине
     *
     * @param reasonMessage - причина кика
     */
    public void disconnect(@NonNull String reasonMessage) {
        if (!isOnline()) {
            return;
        }

        bungeeServer.sendPacket(new SPlayerKickPacket(name, reasonMessage));
        log.info("[Player] {} has disconnected. [Reason=\"{}§r\"]", name, reasonMessage);
    }

    /**
     * Открыть инвентарь игроку
     *
     * @param baseInventory - инвентарь
     */
    public void openInventory(@NonNull BaseInventory baseInventory) {
        if (!isOnline()) {
            return;
        }

        baseInventory.openInventory(this);
    }

    /**
     * Закрыть текущий инвентарь игроку
     */
    public void closeInventory() {
        if (!isOnline()) {
            return;
        }

        getBukkitServer().sendPacket(new SInventoryClosePacket(name));

        InventoryCloseEvent inventoryCloseEvent = new InventoryCloseEvent(this, getOpenedInventory());
        PlazmixCore.getInstance().getEventManager().callEvent(inventoryCloseEvent);

        if (getOpenedInventory() != null) {
            PlazmixCore.getInstance().getInventoryManager().removeInventory(this, getOpenedInventory());
        }
    }

    /**
     * Получить открытый инвентарь игрока
     */
    public BaseInventory getOpenedInventory() {
        return PlazmixCore.getInstance().getInventoryManager().getPlayerInventory(this);
    }

    /**
     * Установить новую группу игроку
     *
     * @param group - группа
     */
    public void setGroup(@NonNull Group group) {
        GroupManager.INSTANCE.setGroupToPlayer(this, group);
    }

    /**
     * Проверить игрока на онлайн.
     */
    public boolean isOnline() {
        return PlazmixCore.getInstance().getPlayerManager().isPlayerOnline(playerId);
    }

    /**
     * Воспроизвести звук игроку
     *
     * @param soundType   - звук
     * @param volume - громкость
     * @param pitch - уровень звука
     */
    public void playSound(@NonNull SoundType soundType, int volume, int pitch) {
        getBukkitServer().sendPacket(new SPlayerSoundPacket(name, soundType, volume, pitch));
    }

    @Override
    public void sendLangMessage(@NonNull String messageKey, @NonNull String... placeholders) {
        LocalizationResource localizationResource = languageType.getResource();

        if (!localizationResource.hasMessage(messageKey)) {
            sendMessage(ChatColor.RED + messageKey);
            return;
        }

        String message;

        if (localizationResource.isText(messageKey)) {
            message = localizationResource.getMessage(messageKey);

        } else {

            message = Joiner.on("\n").join(localizationResource.getMessageList(messageKey));
        }

        if (placeholders.length > 0 && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {

                String placeholder = placeholders[i];
                String value = placeholders[i + 1];

                message = message.replace(placeholder, value);
            }
        }

        sendMessage(message);
    }

    @Override
    public String toString() {
        return "CorePlayer@{id=" + playerId + ", name=" + getName() + "}";
    }

}
