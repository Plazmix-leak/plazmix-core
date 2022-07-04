package net.plazmix.coreconnector.module.type.skin;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import net.plazmix.coreconnector.direction.bungee.BungeeConnectorPlugin;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public final class SkinsModule extends BaseServerModule {

    public static final String SAVE_SKIN_QUERY      = "INSERT INTO `PlayerSkins` VALUES (?, ?, ?)";
    public static final String LOAD_SKINS_QUERY     = "SELECT * FROM `PlayerSkins` WHERE `Id`=?";
    public static final String DELETE_SKINS_QUERY   = "DELETE FROM `PlayerSkins` WHERE `Id`=? AND `Skin`=?";


    public final TIntObjectMap<LinkedList<PlayerSkin>> playerSkinsHistoryMap = new TIntObjectHashMap<>();


    public SkinsModule() {
        super("skins");
    }

    public PlayerSkin generate(@NonNull String playerName) {
        PlayerSkin playerSkin = getCurrentPlayerSkin(playerName);

        // Если у чела не стоит скин через нашу систему
        if (playerSkin == null || playerSkin.getSkinName().equalsIgnoreCase(playerName)) {

            // -> Проверяем ник на лицензию
            // Если да, то берем из моджанг апи его текстуру и устанавливаем
            if (MojangApi.getUserUUID(playerName) != null) {
                playerSkin = PlayerSkin.create(playerName);
            }

            // если нет, ставим дефолт скин
            else {
                playerSkin = PlayerSkin.create(MojangApi.createSkinObject(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYyOTY0MzIwNzQ3OCwKICAicHJvZmlsZUlkIiA6ICJmMjU5MTFiOTZkZDU0MjJhYTcwNzNiOTBmOGI4MTUyMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJmYXJsb3VjaDEwMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMmUxZjJjMWE5ZTQyOTMzOGJlODcyMzY4ZTZiZmJlNmZiOGM3ZTViMjhlOTU5ZmY0ZWViNDNhNTU3OGZiNTM0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                        "LICM9kZ4J5QLYNwO3rDiKnZE8STKiy9oJo7JZZuLpbvZKpu4DEsXdXYUYYVjTr1e41bIRuNqPcBgdsUVGBK5mjSDkqiSbk+mgHXa3SAxLaUVXchHuq/Jo4HsmAZv37RWBphywwYCTRIjpoiF32SyFmc7TuUd1x7gjPBDEaWGrzgF9TVNcMYR6eZFhPR+b2eOJXRXHe5dktO8XiGDajcrq4M6N/4pYVMSUsWGrzNknxddwdYdm1VHy1K58Z4IkCBqSFIVCuSXfQA4on2GKa+ndvVtjAqAO1WZvpyCyIpbWvX+WlyLjqPM33zb3mvRQCUZsrusnmK4GlcCXTzv+N9vOXJh+aEZ4x13f2mq8yr/6fOHyhie+ZYxsJerfSiwYQWUqwYD/Y8OoX0UZPhV77QelY2wrNi4+Bd/rk/RbyJq38R2lbvqAoaHojNi5oi0+p4MCcsGgX3JxBuL6+aMW9qPOgJ0En2COGczPInV0gV5snyllxdrDXpHOpqD22l7G37KbAeQJMYDppPxYfIqnxJFhI4BZGE1J4vvAibbL3C3u13KSitWe9Qv0WogbxCga9CyduFgS73siKjP9V/IJa/iWbLlqyU4TX53tXdveR1DwqpcjaqLD9FBIdbIJyMBm+sLJ1pwAXX9+Pm5lclYJJzkU1TVs8qADTWxFpz3z7GKdbg="
                ));
            }
        }

        return playerSkin;
    }

    public void updateSkin(@NonNull String playerName) {
        PlayerSkin playerSkin = getCurrentPlayerSkin(playerName);

        if (playerSkin == null) {
            return;
        }

        if (CoreConnector.getInstance().isBungee()) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(ProxyServer.getInstance().getPlayer(playerName), playerSkin);

        } else {

            BukkitConnectorPlugin.SKIN_SETTER.updateSkin(Objects.requireNonNull(Bukkit.getPlayer(playerName)), playerSkin);
        }
    }

    public void updateSkin(@NonNull String playerName, @NonNull PlayerSkin playerSkin) {
        if (CoreConnector.getInstance().isBungee()) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(ProxyServer.getInstance().getPlayer(playerName), playerSkin);

        } else {

            BukkitConnectorPlugin.SKIN_SETTER.updateSkin(Objects.requireNonNull(Bukkit.getPlayer(playerName)), playerSkin);
        }
    }

    public void updateSkin(@NonNull String playerName, @NonNull MojangSkin mojangSkin) {
        if (CoreConnector.getInstance().isBungee()) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(ProxyServer.getInstance().getPlayer(playerName), mojangSkin);

        } else {

            BukkitConnectorPlugin.SKIN_SETTER.updateSkin(Objects.requireNonNull(Bukkit.getPlayer(playerName)), mojangSkin);
        }
    }

    public void updateSkin(@NonNull String playerName, @NonNull String skin) {
        if (CoreConnector.getInstance().isBungee()) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(ProxyServer.getInstance().getPlayer(playerName), skin);

        } else {

            BukkitConnectorPlugin.SKIN_SETTER.updateSkin(Objects.requireNonNull(Bukkit.getPlayer(playerName)), skin);
        }
    }

    public void updateSkin(@NonNull String playerName, @NonNull String value, @NonNull String signature) {
        if (CoreConnector.getInstance().isBungee()) {
            BungeeConnectorPlugin.SKIN_SETTER.updateSkin(ProxyServer.getInstance().getPlayer(playerName), value, signature);

        } else {

            BukkitConnectorPlugin.SKIN_SETTER.updateSkin(Objects.requireNonNull(Bukkit.getPlayer(playerName)), value, signature);
        }
    }

    public void saveSkinHistory(@NonNull String playerName, @NonNull String skinName) {
        int playerId = NetworkModule.getInstance().getPlayerId(playerName);

        LinkedList<PlayerSkin> skinsHistory = playerSkinsHistoryMap.get(playerId);

        if (skinsHistory == null) {
            skinsHistory = new LinkedList<>();
        }

        for (PlayerSkin playerSkin : new ArrayList<>(skinsHistory)) {

            if (playerSkin.getSkinName().equalsIgnoreCase(skinName)) {
                CoreConnector.getInstance().getMysqlConnection().execute(true, DELETE_SKINS_QUERY, playerId, playerSkin.getSkinName());

                skinsHistory.remove(playerSkin);
            }
        }

        PlayerSkin playerSkin = PlayerSkin.create(skinName);

        skinsHistory.add(playerSkin);
        playerSkinsHistoryMap.put(playerId, skinsHistory);

        CoreConnector.getInstance().getMysqlConnection().execute(true, SAVE_SKIN_QUERY, playerId, skinName, playerSkin.getDate());
    }

    public LinkedList<PlayerSkin> getSkinsHistory(@NonNull String playerName) {
        int playerId = NetworkModule.getInstance().getPlayerId(playerName);

        LinkedList<PlayerSkin> skinsHistory = playerSkinsHistoryMap.get(playerId);

        if (skinsHistory == null) {
            skinsHistory = new LinkedList<>();

            playerSkinsHistoryMap.put(playerId, skinsHistory);
        }

        if (skinsHistory.isEmpty()) {
            LinkedList<PlayerSkin> finalSkinsHistory = skinsHistory;

            CoreConnector.getInstance().getMysqlConnection().executeQuery(false, LOAD_SKINS_QUERY, resultSet -> {

                        while (resultSet.next()) {
                            String skin = resultSet.getString("Skin");

                            PlayerSkin playerSkin = PlayerSkin.create(skin);
                            playerSkin.setDate(resultSet.getTimestamp("Date"));

                            finalSkinsHistory.add(playerSkin);
                        }

                        return null;
                    }, playerId);

            playerSkinsHistoryMap.put(playerId, skinsHistory);
        }

        return skinsHistory;
    }

    public PlayerSkin getCurrentPlayerSkin(@NonNull String playerName) {
        LinkedList<PlayerSkin> skinsHistory = getSkinsHistory(playerName);

        if (skinsHistory.isEmpty()) {
            return null;
        }

        return skinsHistory.stream().max(Comparator.comparingLong(value -> value.getDate().getTime())).orElse(null);
    }

    public boolean hasSkinHistory(@NonNull String playerName, @NonNull String skinName) {
        return getSkinsHistory(playerName)
                .stream()
                .anyMatch(playerSkinInfo -> playerSkinInfo.getSkinName().equalsIgnoreCase(skinName));
    }

}
