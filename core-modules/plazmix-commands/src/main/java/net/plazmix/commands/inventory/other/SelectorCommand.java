package net.plazmix.commands.inventory.other;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventorySimpleMarkup;
import net.plazmix.core.common.friend.CoreFriend;
import net.plazmix.core.common.party.Party;
import net.plazmix.core.common.party.PartyManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SelectorCommand extends CommandExecutor {

    public SelectorCommand() {
        super("selector");

        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(commandSender.getName());
        BukkitServer bukkitServer = corePlayer.getBukkitServer();

        //FIXME
        //if (!ServerMode.isMain(bukkitServer.getName()) || !ServerMode.isGameLobby(bukkitServer.getName())) {
        //    corePlayer.sendMessage("§d§lPlazmix §8:: §cОшибка, данная команда не доступна на данном типе серверов!");
        //    return;
        //}

        new SelectorInventory().openInventory((CorePlayer) commandSender);

    }

    protected static class SelectorInventory extends CorePaginatedInventory {
        public SelectorInventory() {
            super(6, "Выбор лобби");
        }

        @Override
        public void drawInventory(@NonNull CorePlayer corePlayer) {
            BaseInventoryMarkup inventoryMarkup = new BaseInventorySimpleMarkup(inventoryRows);

            inventoryMarkup.addHorizontalRow(3, 2);
            inventoryMarkup.addHorizontalRow(4, 2);

            setInventoryMarkup(inventoryMarkup);

            int lobbyIndex = 1;
            for (BukkitServer lobbyServer : ServerMode.getServers(ServerMode.getMode(corePlayer.getBukkitServer()))) {

                if (ServerMode.isGameArena(lobbyServer.getName())) {
                    return;
                }

                addItemToMarkup(getLobbyItem(corePlayer, lobbyServer, lobbyIndex++).build(), (baseInventory, inventoryClickEvent) -> {
                    if (lobbyServer.getName().contains(corePlayer.getBukkitServer().getName())) {
                        return;
                    }

                    corePlayer.connectToServer(lobbyServer);
                });
            }
        }

        public ItemBuilder getLobbyItem(CorePlayer corePlayer, BukkitServer lobbyServer, int lobbyIndex) {
            LobbyState lobbyState = LobbyState.of(corePlayer, lobbyServer);

            ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(lobbyState.getSkull())

                    .setDisplayName(String.format("§d§lЛобби §2#%s", lobbyIndex))

                    .addLore("§fНазвание: §d" + lobbyServer.getName())
                    .addLore("")
                    .addLore("§fСостояние: " + lobbyState.getChatColor() + lobbyState.getStateName())
                    .addLore("§fОнлайн: §a" + lobbyServer.getOnlineCount() + "§7/§c150");

            ArrayList<CorePlayer> friendOnServer = new ArrayList<>();

            CoreFriend coreFriends = CoreFriend.of(corePlayer);
            for (CoreFriend coreFriend : coreFriends.getCoreFriends()) {
                CorePlayer friend = PlazmixCore.getInstance().getOfflinePlayer(coreFriend.getName());

                if (!friend.isOnline()) {
                    continue;
                }

                if (!friend.getBukkitServer().getName().equals(lobbyServer.getName())) continue;

                friendOnServer.add(friend);
            }

            ArrayList<CorePlayer> partyMemberOnServer = new ArrayList<>();
            Party coreParty = PartyManager.INSTANCE.getParty(corePlayer);

            if (coreParty != null) {
                for (String partyMemberName : coreParty.getMembers()) {
                    CorePlayer partyMember = PlazmixCore.getInstance().getOfflinePlayer(partyMemberName);

                    if (!partyMember.isOnline()) {
                        continue;
                    }

                    if (partyMember.getName().equals(corePlayer.getName())) {
                        continue;
                    }

                    if (!partyMember.getBukkitServer().getName().equals(lobbyServer.getName())) continue;

                    partyMemberOnServer.add(partyMember);
                }
            }

            addUserBlock(itemBuilder, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMzODQ0ZjAzYzkyZDgyNTAzYzljZDU2N2E1MWZkYTQ3NDA0ZmE1M2YyMTZmYWQ0NzNmMTZhYzgzYTk3MzE0ZiJ9fX0=", "§fДрузья на сервере:", friendOnServer);
            addUserBlock(itemBuilder, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzZDk3NDBkZjIyMjE3N2I2NzhmNjgxZmY3OTQ5NTNhZTQzMjU1ZDhjNjU3NzIzNjgxZjNlNmY5ZThlNzMxIn19fQ==", "§fУчастники компании на сервере:", partyMemberOnServer);

            itemBuilder.addLore("");

            if (lobbyState == LobbyState.CURRENT) {
                itemBuilder.addLore("§e▸ Вы находитесь на этом сервере!");

            } else {
                itemBuilder.addLore("§e▸ Нажмите чтобы подключиться");
            }

            return itemBuilder;
        }

        public void addUserBlock(ItemBuilder itemBuilder, String newSkull, String blockName, ArrayList<CorePlayer> userList) {
            if (userList.isEmpty()) {
                return;
            }

            itemBuilder.setPlayerSkull(newSkull);
            itemBuilder.addLore("");
            itemBuilder.addLore(blockName);

            for (CorePlayer member : userList.stream().limit(3).collect(Collectors.toList())) {
                itemBuilder.addLore(member.getDisplayName());
            }

            if (userList.size() > 3) {
                itemBuilder.addLore(String.format("и ещё %d...", userList.size() - 3));
            }

        }

        @Getter
        protected enum LobbyState {

            FREE(ChatColor.GREEN, "Свободный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE1ZjAyZWFjNjAyNmI4ZTg3MTJjYTRkNzgxYjc5MWJiYmI3YjQ3NTVhYmRhMjdmNDYyMTg5YjkwZmVkNjZhMSJ9fX0=", 0, 50),
            AVERAGE_LOAD(ChatColor.YELLOW, "Средне заполненный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4YzZlZDk5N2JmYjA3ODQ3NDg3NmI3ZGVlNmZhM2ExYTljYzYxYjZhYTI3OWUxNTc5ODNlZGM5Y2RmMjJmZSJ9fX0=", 51, 80),
            PEAK(ChatColor.RED, "Заполненный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU1MWFhNTA4OTQ4MWEwMGJmYWEzODE3MDVlNDIzNjdjZTkzZjYxMmVkYTBlNTJiNzM5M2JiNGExMjliZWQ4NSJ9fX0=", 81, 90),
            FULL(ChatColor.BLACK, "Полный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFiMTE3MjcyZmFiZmU0YzAxYTgxNGNhNGNhMjRhODU2Y2U3OTQ5N2ZmZjg2MWJhYWUzN2Q3OTJiYWY1NDM5MSJ9fX0=", 99, 100),
            CURRENT(ChatColor.DARK_PURPLE, "Текущий", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3MjlkODMwZTM5MDcxMTQ0MTI4NTQyZjZmMzNiNWRhODMyZDI2YjZjNmJiOWQ0Mjk5ZDI3YjA0N2FiNGQzYiJ9fX0=", 0, 0),
            UNKNOWN(ChatColor.GRAY, "Неизвестный", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVmZGRlYzM2NjU1ZDNiMzcxYzc5ZDYxMTMzNTQ4Nzc1NzcwODljMWZjYjFiM2Q4ZTAwYWYzMjYxMmYyNmYyOCJ9fX0=", 0, 0);

            private final ChatColor chatColor;
            private final String stateName;
            private final String skull;
            private final int fromPercent;
            private final int toPercent;

            LobbyState(ChatColor chatColor, String stateName, String skull, int fromPercent, int toPercent) {
                this.chatColor = chatColor;
                this.stateName = stateName;
                this.skull = skull;
                this.fromPercent = fromPercent;
                this.toPercent = toPercent;
            }

            public static LobbyState of(@NonNull CorePlayer corePlayer, @NonNull BukkitServer lobbyServer) {
                if (corePlayer.getBukkitServer().getName().equals(lobbyServer.getName())) {
                    return CURRENT;
                }

                double loadFactor = ((double) lobbyServer.getOnlineCount() / 150.0) * 100.0;

                for (LobbyState currentState : LobbyState.values()) {

                    if (currentState.getFromPercent() <= loadFactor && loadFactor <= currentState.getToPercent()) {
                        return currentState;
                    }
                }

                return UNKNOWN;
            }

        }

    }

}
