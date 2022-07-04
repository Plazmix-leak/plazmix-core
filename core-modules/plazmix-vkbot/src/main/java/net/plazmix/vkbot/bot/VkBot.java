package net.plazmix.vkbot.bot;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.plazmix.vkbot.api.VkApi;
import net.plazmix.vkbot.api.callback.ResponseCallback;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.handler.BotCallbackApiHandler;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class VkBot extends VkApi {

    public static final VkBot INSTANCE = new VkBot();


    /**
     * Зарегистрированные команды
     */
    private final Map<String, VkCommand> commandMap = new HashMap<>();
    private final JsonParser jsonParser = new JsonParser();

    public VkBot() {
        addCallbackApiHandler(new BotCallbackApiHandler(this));
    }

    public void printMessage(int peerId, @NonNull String message) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("peer_id", peerId);
        parameters.addProperty("message", message);

        call("messages.send", parameters);
    }

    public void printAndDeleteMessage(int peerId, @NonNull String message) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("peer_id", peerId);
        parameters.addProperty("message", message);

        call("messages.send", parameters, new ResponseCallback() {
            @Override
            public void onResponse(String result) {
                JsonObject object = jsonParser.parse(result).getAsJsonObject();

                deleteMessages(object.get("response").getAsInt());
            }

            @Override
            public void onException(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void deleteMessages(Integer... messageIds) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("message_ids", Joiner.on(",").join(messageIds));

        call("messages.delete", parameters);
    }

    public void kick(int chatId, int userId) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("chat_id", chatId);
        parameters.addProperty("user_id", userId);

        call("messages.removeChatUser", parameters);
    }

    public void editTitle(int chatId, @NonNull String title) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("chat_id", chatId);
        parameters.addProperty("title", title);

        call("messages.editChat", parameters);
    }

    public void registerCommand(@NonNull VkCommand vkCommand) {
        //регаем все алиасы
        for (String alias : vkCommand.getAliases()) {
            commandMap.put(alias.toLowerCase(), vkCommand);
        }

        log.info("Registered VK Bot command -> " + vkCommand.getAliases()[0]);
    }

    public VkCommand getCommand(@NonNull String commandAlias) {
        return commandMap.get(commandAlias.toLowerCase());
    }

}
