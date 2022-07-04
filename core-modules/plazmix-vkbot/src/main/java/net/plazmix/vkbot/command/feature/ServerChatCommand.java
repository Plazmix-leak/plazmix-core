package net.plazmix.vkbot.command.feature;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.api.utility.CooldownUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.event.impl.PlayerChatEvent;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.connection.server.mode.ServerSubModeType;
import net.plazmix.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import net.plazmix.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.VkCommand;
import net.plazmix.vkbot.user.BotUser;

import java.util.function.Consumer;

public class ServerChatCommand extends VkCommand {

    public ServerChatCommand() {
        super("chat", "чат", "playerchat");

        setMinimalGroup(Group.LUXURY); // пусть донат покупают, бомжи ебаные

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(BotUser botUser, @NonNull Message message, @NonNull String[] args, @NonNull VkBot vkBot) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(botUser.getPrimaryAccountName());
        BukkitServer bukkitServer;

        if (corePlayer == null) {
            if (args.length == 0) {
                vkBot.printMessage(message.getPeerId(), "Ошибка в синтаксисе, используйте - !playerchat <сервер>");

                return;
            }

            bukkitServer = PlazmixCore.getInstance().getBukkitServer(args[0]);

            if (bukkitServer == null) {
                vkBot.printMessage(message.getPeerId(), "Ошибка, данный сервер не существует или не подключен к Core!");

                return;
            }

        } else {

            bukkitServer = corePlayer.getBukkitServer();
        }

        openPlayerChat(message.getPeerId(), botUser, botUser.getPrimaryAccountName(), vkBot, bukkitServer,
                playerMessage -> vkBot.printMessage(message.getPeerId(), playerMessage));
    }


    private static final String SESSION_HANDLER_NAME = "server_player_chat";
    private static final TIntObjectMap<PlayerChatIntegration> CHAT_INTEGRATION_MAP = new TIntObjectHashMap<>();

    static {
        PlazmixCore.getInstance().getEventManager().registerListener(new PlayerChatListener());
    }

    /**
     * Открыть чат игроков указанного сервера
     *
     * @param bukkitServer        - сервер
     * @param chatMessageConsumer - обработчик пришедшего сообщения от игрока
     */
    private void openPlayerChat(int peerId,

                                @NonNull BotUser botUser,
                                @NonNull String playerName,
                                @NonNull VkBot vkBot,

                                @NonNull BukkitServer bukkitServer,
                                @NonNull Consumer<String> chatMessageConsumer) {

        if (ServerMode.isTyped(bukkitServer, ServerSubModeType.GAME_ARENA)
                || ServerMode.isTyped(bukkitServer, ServerMode.AUTH)
                || ServerMode.isTyped(bukkitServer, ServerMode.LIMBO)) {

            vkBot.printMessage(peerId, "Ошибка, данный вид сервера не предназначен для интеграции!");
            return;
        }

        PlayerChatIntegration playerChatIntegration = new PlayerChatIntegration(botUser, botUser.getPrimaryAccountName(), peerId, bukkitServer, chatMessageConsumer);
        CHAT_INTEGRATION_MAP.put(peerId, playerChatIntegration);

        new Message()
                .peerId(peerId)
                .body("✅ Интеграция игрового чата от сервера " + bukkitServer.getName() + " была успешно запущена!")

                .keyboard(false, true)
                .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("playerchat-close", "Закрыть чат"))
                .message()

                .send(vkBot);

        botUser.createSessionMessageHandler(SESSION_HANDLER_NAME, message -> {
            //TODO: Добавить проверку на бан и мут игрока

            if (message.contains("playerchat-close")) {
                closePlayerChat(peerId, botUser, vkBot);

                return;
            }

            // check cooldown
            String cooldownName = ("chat-integration-").concat(playerName);
            if (CooldownUtil.hasCooldown(cooldownName)) {

                vkBot.printAndDeleteMessage(peerId, "❗ Ошибка, подождите еще " + NumberUtil.getTime(CooldownUtil.getCooldown(cooldownName))
                        + " для повторного написания сообщения.");
                return;
            }

            if (!GroupManager.INSTANCE.getPlayerGroup(playerName).isStaff()) {
                CooldownUtil.putCooldown(cooldownName, 1000 * 30);
            }

            if (GroupManager.INSTANCE.getPlayerGroup(playerName).isUniversal()) {
                CooldownUtil.putCooldown(cooldownName, 1000 * 15);
            }

            // send message
            CorePlayer playerSender = PlazmixCore.getInstance().getOfflinePlayer(playerName);

            for (PlayerChatIntegration anotherChatIntegration : CHAT_INTEGRATION_MAP.valueCollection()) {
                if (!anotherChatIntegration.bukkitServer.getName().equalsIgnoreCase(playerChatIntegration.bukkitServer.getName())) {
                    continue;
                }

                vkBot.printMessage(anotherChatIntegration.peerId, "✒ [VK CHAT] " + ChatColor.stripColor(playerSender.getDisplayName()) + " ➥ " + message);
            }

            for (CorePlayer corePlayer : bukkitServer.getOnlinePlayers()) {
                corePlayer.sendMessage("§9§l[VK CHAT] " + playerSender.getDisplayName() + " §8➥ " + playerSender.getGroup().getSuffix() + message);
            }
        });
    }

    /**
     * Закрыть кешированный чат игроков
     */
    private void closePlayerChat(int peerId,
                                 @NonNull BotUser botUser,
                                 @NonNull VkBot vkBot) {

        PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.remove(peerId);

        if (playerChatIntegration == null) {
            return;
        }

        vkBot.printMessage(peerId, "\uD83D\uDED1 Интеграция игрового чата от сервера " + playerChatIntegration.bukkitServer.getName() + " закрыта!");
        botUser.closeSessionMessageHandler(SESSION_HANDLER_NAME);
    }


    @AllArgsConstructor
    protected class PlayerChatIntegration {

        protected final BotUser botUser;
        protected final String playerName;

        protected final int peerId;

        protected BukkitServer bukkitServer;
        protected final Consumer<String> chatMessageConsumer;

        public void close() {
            closePlayerChat(peerId, botUser, VkBot.INSTANCE);
        }
    }

    protected static class PlayerChatListener implements EventListener {

        @EventHandler
        public void onPlayerChat(PlayerChatEvent event) {
            CorePlayer corePlayer = event.getCorePlayer();
            BukkitServer bukkitServer = event.getBukkitServer();

            String message = ChatColor.stripColor(event.getMessage());

            if (ServerMode.isTyped(bukkitServer, ServerSubModeType.SURVIVAL) && !message.startsWith("!"))
                return;

            String messageFormat = "✒ [" + bukkitServer.getName() + "] "
                    + ChatColor.stripColor(corePlayer.getDisplayName()) + ": " + (message.startsWith("!") ? message.substring(1) : message);

            for (PlayerChatIntegration playerChatIntegration : CHAT_INTEGRATION_MAP.valueCollection()) {
                if (!bukkitServer.getName().equalsIgnoreCase(playerChatIntegration.bukkitServer.getName())) {
                    return;
                }

                playerChatIntegration.chatMessageConsumer.accept(messageFormat);
            }
        }

        @EventHandler
        public void onPlayerRedirect(PlayerServerRedirectEvent event) {
            CorePlayer corePlayer = event.getCorePlayer();
            BukkitServer bukkitServer = event.getServerTo();


            //FIXME: Интеграция не переносится

            PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.valueCollection()
                    .stream()
                    .filter(integration -> integration.playerName.equalsIgnoreCase(corePlayer.getName()))
                    .findFirst()
                    .orElse(null);

            if (playerChatIntegration != null) {
                VkBot.INSTANCE.printMessage(playerChatIntegration.peerId, "❗ Интеграция игрового чата была перенесена на сервер "
                        + bukkitServer.getName() + " в связи с переключением Вашего аккаунта на указанный!");

                playerChatIntegration.bukkitServer = bukkitServer;
                CHAT_INTEGRATION_MAP.put(playerChatIntegration.peerId, playerChatIntegration);
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerLeaveEvent event) {
            CorePlayer corePlayer = event.getCorePlayer();

            CHAT_INTEGRATION_MAP.valueCollection()
                    .stream()
                    .filter(integration -> integration.playerName.equalsIgnoreCase(corePlayer.getName()))
                    .findFirst()
                    .ifPresent(PlayerChatIntegration::close);
        }
    }

}
