package net.plazmix.friends.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.friend.FriendRequestManager;
import net.plazmix.core.common.friend.CoreFriend;
import net.plazmix.friends.inventory.FriendsListInventory;

public class FriendCommand extends CommandExecutor {

    public FriendCommand() {
        super("f", "friend", "friends", "друг", "друзья");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            sendHelpMessage(commandSender);
            return;
        }

        switch (args[0].toLowerCase()) {

            case "-n":
            case "a":
            case "add": {
                if (args.length < 2) {
                    commandSender.sendLangMessage("FRIENDS_ADD_FORMAT");
                    break;
                }

                CoreFriend friend = CoreFriend.of(args[1]);

                if (friend.getOfflinePlayer() == null || !friend.getOfflinePlayer().hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    break;
                }

                // Если указанный игрок уже запрашивал дружбу, то просто принимаем ее
                if (FriendRequestManager.INSTANCE.hasFriendRequest(friend.getPlayerId(), corePlayer.getPlayerId())) {

                    commandSender.sendLangMessage("FRIEND_ADD_ERROR_DUPLICATE_REQUEST");
                    PlazmixCore.getInstance().getCommandManager().dispatchCommand(commandSender, "f accept " + friend.getName());
                    break;
                }

                // Если отправитель команды долбаеб, и уже сам запрашивал дружбу у указанного игрока
                if (FriendRequestManager.INSTANCE.hasFriendRequest(corePlayer.getPlayerId(), friend.getPlayerId())) {
                    commandSender.sendLangMessage("FRIEND_ADD_ERROR_HAS_REQUEST");
                    break;
                }

                if (friend.hasFriend(corePlayer.getName())) {
                    commandSender.sendLangMessage("FRIEND_ADD_ERROR_IS_FRIEND");
                    break;
                }

                if (friend.getName().equalsIgnoreCase(commandSender.getName())) {
                    commandSender.sendLangMessage("FRIEND_ADD_ERROR_YOURSELF_AS_FRIEND");
                    break;
                }

                CorePlayer friendOfflinePlayer = friend.getOfflinePlayer();
                FriendRequestManager.INSTANCE.addFriendRequest(corePlayer.getPlayerId(), friend.getPlayerId());

                if (friendOfflinePlayer.isOnline()) {

                    friendOfflinePlayer.sendMessage("§r          ");
                    friendOfflinePlayer.sendMessage("§d§lДрузья §8:: §fВам было отправлено приглашение на дружбу от " + commandSender.getDisplayName());
                    friendOfflinePlayer.sendMessage("§r          ");

                    // Создаем кнопки
                    BaseComponent[] acceptButton = JsonChatMessage.create("§a§l[ПРИНЯТЬ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §aпринять")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/f accept " + commandSender.getName())
                            .build();

                    BaseComponent[] denyButton = JsonChatMessage.create("§c§l[ОТКЛОНИТЬ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §cотклонить")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/f deny " + commandSender.getName())
                            .build();

                    // И объединяем все это говно
                    JsonChatMessage.create()

                            .addComponents(TextComponent.fromLegacyText("          "))
                            .addComponents(acceptButton)

                            .addComponents(TextComponent.fromLegacyText("          "))
                            .addComponents(denyButton)

                            .addText("\n\n")
                            .sendMessage(friendOfflinePlayer);
                }

                commandSender.sendLangMessage("FRIEND_ADD_SUCCESS_SEND", "%player%", friendOfflinePlayer.getDisplayName());
                break;
            }

            case "-rm":
            case "rm":
            case "remove": {
                if (args.length < 2) {
                    commandSender.sendLangMessage("FRIENDS_REMOVE_FORMAT");
                    break;
                }

                CoreFriend friend = CoreFriend.of(args[1]);

                if (friend.getOfflinePlayer() == null || !friend.getOfflinePlayer().hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    break;
                }

                if (!friend.hasFriend(corePlayer.getName())) {
                    commandSender.sendLangMessage("FRIEND_REMOVE_ERROR_HAS_FRIEND");
                    break;
                }

                commandSender.sendLangMessage("FRIEND_SUCCESS_REMOVE_FRIEND",
                        "%player%", friend.getOfflinePlayer().getDisplayName());
                friend.removeFriend(corePlayer.getPlayerId());
                break;
            }

            case "-l":
            case "-ls":
            case "l":
            case "list": {
                CoreFriend sender = CoreFriend.of(corePlayer);

                if (sender.getFriendsCount() <= 0) {
                    commandSender.sendLangMessage("FRIEND_LIST_NO_FRIENDS");

                    return;
                }

                new FriendsListInventory().openInventory(corePlayer);
                break;
            }

            case "-r":
            case "r":
            case "request":
            case "requests": {
                int requestsCount = FriendRequestManager.INSTANCE.getFriendsRequestsIds(corePlayer.getPlayerId()).size();

                if (requestsCount <= 0) {
                    commandSender.sendLangMessage("FRIENDS_DONT_HAVE_INVITES");

                    return;
                }

                commandSender.sendLangMessage("FRIEND_LIST_REQUESTS",
                        "%count%", NumberUtil.spaced(requestsCount));

                int friendCounter = 1;
                for (CorePlayer offlinePlayer : FriendRequestManager.INSTANCE.getOfflineRequestsIds(corePlayer.getPlayerId())) {

                    JsonChatMessage.create(" §e" + friendCounter + ". " + offlinePlayer.getDisplayName())
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §aпринять")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/f accept " + offlinePlayer.getName())

                            .sendMessage(corePlayer);

                    friendCounter++;
                }

                break;
            }

            case "-a":
            case "accept": {
                if (args.length < 2) {
                    commandSender.sendLangMessage("FRIENDS_ACCEPT_FORMAT");
                    break;
                }

                CoreFriend friend = CoreFriend.of(args[1]);

                if (friend.getOfflinePlayer() == null || !friend.getOfflinePlayer().hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    break;
                }

                if (!FriendRequestManager.INSTANCE.hasFriendRequest(friend.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendMessage("§cОшибка, данный игрок не отправлял Вам приглашение на дружбу!");
                    break;
                }

                if (friend.hasFriend(corePlayer.getName())) {
                    commandSender.sendLangMessage("FRIEND_ADD_ERROR_IS_FRIEND");
                    break;
                }

                CorePlayer friendOfflinePlayer = friend.getOfflinePlayer();
                commandSender.sendLangMessage("FRIENDS_NEW_FRIEND",
                        "%player%", friendOfflinePlayer.getDisplayName());

                if (friendOfflinePlayer.isOnline()) {
                    friendOfflinePlayer.sendLangMessage("FRIENDS_NEW_FRIEND",
                            "%player%", commandSender.getDisplayName());
                }

                friend.addFriend(corePlayer.getPlayerId());

                FriendRequestManager.INSTANCE.removeFriendRequest(friend.getPlayerId(), corePlayer.getPlayerId());

                // На всякий случай
                FriendRequestManager.INSTANCE.removeFriendRequest(corePlayer.getPlayerId(), friend.getPlayerId());
                break;
            }

            case "-t":
            case "tp":
            case "teleport": {
                if (args.length < 2) {
                    commandSender.sendLangMessage("FRIENDS_TP_FORMAT");
                    break;
                }

                CoreFriend friend = CoreFriend.of(args[1]);

                if (friend.getOfflinePlayer() == null || !friend.getOfflinePlayer().hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    break;
                }

                if (!friend.hasFriend(corePlayer.getName())) {
                    commandSender.sendLangMessage("FRIEND_REMOVE_ERROR_HAS_FRIEND");
                    break;
                }

                if (!friend.getOfflinePlayer().isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    return;
                }

                corePlayer.connectToServer(friend.getOfflinePlayer().getBukkitServer());
                break;
            }

            case "-d":
            case "deny": {
                if (args.length < 2) {
                    commandSender.sendLangMessage("FRIENDS_DENY_FORMAT");
                    break;
                }

                CoreFriend friend = CoreFriend.of(args[1]);

                if (friend.getOfflinePlayer() == null || !friend.getOfflinePlayer().hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    break;
                }

                // Если указанный игрок уже запрашивал дружбу, то просто принимаем ее
                if (!FriendRequestManager.INSTANCE.hasFriendRequest(friend.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendLangMessage("FRIENDS_DONT_HAVE_INVITES_FROM");
                    break;
                }

                if (friend.hasFriend(corePlayer.getName())) {
                    commandSender.sendLangMessage("FRIENDS_ALREADY_FRIENDS");
                    break;
                }

                CorePlayer friendOfflinePlayer = friend.getOfflinePlayer();
                commandSender.sendLangMessage("FRIENDS_YOU_REJECTED_REQUEST",
                        "%player%", friendOfflinePlayer.getDisplayName());

                if (friendOfflinePlayer.isOnline()) {
                    friendOfflinePlayer.sendLangMessage("FRIENDS_REQUEST_REJECTED",
                            "%player%", commandSender.getDisplayName());
                }


                FriendRequestManager.INSTANCE.removeFriendRequest(friend.getPlayerId(), corePlayer.getPlayerId());

                // На всякий случай
                FriendRequestManager.INSTANCE.removeFriendRequest(corePlayer.getPlayerId(), friend.getPlayerId());
                break;
            }

            default:
                sendHelpMessage(commandSender);
        }
    }

    private void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendLangMessage("FRIEND_HELP");
    }

}
