package net.plazmix.streams.command;

import lombok.NonNull;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.streams.StreamManager;
import net.plazmix.core.common.streams.detail.AbstractStreamDetails;
import net.plazmix.core.common.streams.platform.StreamPlatform;

import java.util.Arrays;

public class StreamCommand extends CommandExecutor {

    public StreamCommand() {
        super("stream", "yt", "twitch", "youtube");

        setMinimalGroup(Group.MEDIA);

        setOnlyPlayers(true);
        setOnlyAuthorized(true);

        setCanUseLoginServer(false);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {

        if (args.length == 0) {
            sendHelpMessage(commandSender);

            return;
        }

        String lowerArgument = (args[0].toLowerCase());
        switch (lowerArgument) {

            case "add":
            case "добавить": {

                CorePlayer corePlayer = ((CorePlayer) commandSender);
                StreamManager streamManager = StreamManager.INSTANCE;

                if (args.length == 1) {
                    commandSender.sendLangMessage("STREAM_ADD_FORMAT");
                    return;
                }

                if (streamManager.getActiveStream(corePlayer) != null) {
                    commandSender.sendLangMessage("STREAM_ADD_ERROR_ALREADY_STREAMING");
                    return;
                }

                AbstractStreamDetails streamDetails = null;

                for (StreamPlatform<?> streamPlatform : streamManager.getAvailableStreamPlatforms()) {
                    streamDetails = streamPlatform.parseStreamUrl(args[1]);

                    if (streamDetails != null) {
                        break;
                    }
                }

                if (streamDetails == null) {
                    commandSender.sendLangMessage("STREAM_ADD_ERROR_UNKNOWN_STREAM_URL");
                    return;
                }

                if (streamDetails.getViewers() < 10) {
                    commandSender.sendLangMessage("STREAM_ADD_ERROR_ENOUGH_VIEWERS");
                }

                commandSender.sendLangMessage("STREAM_ADD_SUCCESS_STREAM", "%stream%", args[1]);
                streamManager.addPlayerStream(corePlayer, streamDetails);
                break;
            }

            case "удалить":
            case "remove":
            case "delete": {

                CorePlayer corePlayer = ((CorePlayer) commandSender);
                StreamManager streamManager = StreamManager.INSTANCE;

                if (streamManager.getActiveStream(corePlayer) == null) {
                    commandSender.sendLangMessage("STREAM_ADD_ERROR_NOT_STREAMING");
                    return;
                }

                commandSender.sendLangMessage("STREAM_REMOVE_SUCCESS_STREAM");
                streamManager.removePlayerStream(corePlayer);
                break;
            }
        }
    }
    protected void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage("§d§lStreams §8:: §fСписок доступных команд:");

        commandSender.sendMessage(" §8- §7Добавить стрим - §6/stream add <ссылка>");
        commandSender.sendMessage(" §8- §7Удалить стрим - §6/stream remove");
        commandSender.sendMessage(" §8- §7Посмотреть доступные стримы - §6/streams");
    }
}
