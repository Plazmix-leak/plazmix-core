package net.plazmix.chat.command.chat;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.ArrayList;
import java.util.Collection;

public class StaffchatCommand extends CommandExecutor {

    protected static final Collection<String> DISABLE_CHAT = new ArrayList<>();

    public StaffchatCommand() {
        super("sc", "staffchat");

        setMinimalGroup(Group.JR_MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendLangMessage("STAFF_CHAT_FORMAT");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on": {
                if (!DISABLE_CHAT.contains(corePlayer.getName().toLowerCase())) {
                    commandSender.sendLangMessage("STAFF_CHAT_ERROR_DUPLICATE_ON!");
                    break;
                }

                commandSender.sendLangMessage("STAFF_CHAT_SUCCESS_ON");
                DISABLE_CHAT.remove(corePlayer.getName().toLowerCase());
                break;
            }

            case "off": {
                if (DISABLE_CHAT.contains(corePlayer.getName().toLowerCase())) {
                    commandSender.sendLangMessage("DONATE_CHAT_ERROR_DUPLICATE_OFF");
                    break;
                }

                commandSender.sendLangMessage("DONATE_CHAT_SUCCESS_OFF");
                DISABLE_CHAT.add(corePlayer.getName().toLowerCase());
                break;
            }

            default:
                if (DISABLE_CHAT.contains(corePlayer.getName().toLowerCase())) {
                    commandSender.sendLangMessage("DONATE_CHAT_OFF");
                    return;
                }

                String staffMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));

                for (CorePlayer staffPlayer : PlazmixCore.getInstance().getOnlinePlayers(onlinePlayer -> onlinePlayer.getGroup().isStaff()
                        && !DISABLE_CHAT.contains(onlinePlayer.getName().toLowerCase()))) {

                    staffPlayer.sendMessage("§d§lStaffChat §8:: " + corePlayer.getDisplayName() + " §8» " + corePlayer.getGroup().getSuffix() + staffMessage);
                }
        }
    }

}
