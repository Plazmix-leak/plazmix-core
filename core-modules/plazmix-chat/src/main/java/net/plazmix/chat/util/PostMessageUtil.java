package net.plazmix.chat.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PostMessageUtil {

    protected final Collection<String> IGNORE_ALL_COLLECTION        = new ArrayList<>();
    protected final Map<CorePlayer, CorePlayer> REPLY_MESSAGE_MAP   = new HashMap<>();
    protected final Multimap<String, String> IGNORE_MAP             = HashMultimap.create();

    protected static final String POST_MESSAGE_FORMAT               = ("§8[§dЛС§8] §d%s §f» §6%s §f: §f%s");


    public void sendMessage(@NonNull CorePlayer senderPlayer,
                            @NonNull CorePlayer targetPlayer,

                            @NonNull String message) {

        if (senderPlayer.equals(targetPlayer)) {
            senderPlayer.sendLangMessage("CHAT_ERROR_MESSAGE_YOURSELF");
            return;
        }

        if (IGNORE_ALL_COLLECTION.contains(targetPlayer.getName().toLowerCase())) {
            senderPlayer.sendLangMessage("CHAT_ERROR_MESSAGE_OFF");
            return;
        }

        if (IGNORE_MAP.containsEntry(targetPlayer.getName().toLowerCase(), senderPlayer.getName().toLowerCase())) {
            senderPlayer.sendLangMessage("CHAT_ERROR_MESSAGE_IGNORE");
            return;
        }

        if (!targetPlayer.isOnline()) {
            senderPlayer.sendLangMessage("CHAT_ERROR_MESSAGE_PLAYER_OFFLINE");
            return;
        }

        REPLY_MESSAGE_MAP.put(targetPlayer, senderPlayer);

        targetPlayer.sendMessage(String.format(POST_MESSAGE_FORMAT, senderPlayer.getDisplayName(), "Я", message));
        senderPlayer.sendMessage(String.format(POST_MESSAGE_FORMAT, "Я", targetPlayer.getDisplayName(), message));
    }

    public void replyMessage(@NonNull CorePlayer senderPlayer,
                             @NonNull String message) {

        CorePlayer targetPlayer = REPLY_MESSAGE_MAP.get(senderPlayer);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendLangMessage("CHAT_ERROR_MESSAGE_NO_REPLY");
            return;
        }

        sendMessage(senderPlayer, targetPlayer, message);
    }

    public void ignore(@NonNull CorePlayer corePlayer,
                       @NonNull CorePlayer targetPlayer) {

        if (corePlayer.equals(targetPlayer)) {
            corePlayer.sendLangMessage("CHAT_ERROR_IGNORE_YOURSELF");
            return;
        }

        if (IGNORE_MAP.containsEntry(corePlayer.getName().toLowerCase(), targetPlayer.getName().toLowerCase())) {
            IGNORE_MAP.remove(corePlayer.getName().toLowerCase(), targetPlayer.getName().toLowerCase());

            corePlayer.sendMessage("§d§lPlazmix §8:: §fВы добавили игрока §7" + targetPlayer.getDisplayName() + "§e в список игнорируемых. Теперь он не может отправлять вам личные сообщения.");
        } else {
            IGNORE_MAP.put(corePlayer.getName().toLowerCase(), targetPlayer.getName().toLowerCase());

            corePlayer.sendMessage("§d§lPlazmix §8:: §fВы больше не игнорируете игрока §7" + targetPlayer.getDisplayName() + "§c, он может отправлять вам личные сообщения.");
        }
    }

    public void ignoreAll(@NonNull CorePlayer corePlayer) {
        String lowerName = corePlayer.getName().toLowerCase();

        if (IGNORE_ALL_COLLECTION.contains(lowerName)) {
            IGNORE_ALL_COLLECTION.remove(lowerName);

            corePlayer.sendLangMessage("CHAT_SUCCESS_IGNORE_ALL_ON");
        } else {
            IGNORE_ALL_COLLECTION.add(lowerName);

            corePlayer.sendLangMessage("CHAT_SUCCESS_IGNORE_ALL_OFF");
        }
    }

}
