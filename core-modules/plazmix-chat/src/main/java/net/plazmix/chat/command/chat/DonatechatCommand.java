package net.plazmix.chat.command.chat;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.CooldownUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.ArrayList;
import java.util.Collection;

public class DonatechatCommand extends CommandExecutor {

    protected static final Collection<String> DISABLE_CHAT = new ArrayList<>();
    protected static final String DONATE_CHAT_COOLDOWN_PATTERN = "DonateChat-%s";

    public DonatechatCommand() {
        super("dc", "donatechat");

        setMinimalGroup(Group.STAR);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendLangMessage("DONATE_CHAT_FORMAT");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on": {
                if (!DISABLE_CHAT.contains(corePlayer.getName().toLowerCase())) {
                    commandSender.sendLangMessage("DONATE_CHAT_ERROR_DUPLICATE_ON");
                    break;
                }

                commandSender.sendLangMessage("DONATE_CHAT_SUCCESS_ON");
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
 
                String cooldownName = String.format(DONATE_CHAT_COOLDOWN_PATTERN, commandSender.getName());

                if (CooldownUtil.hasCooldown(cooldownName)) {
                    long cooldownMillis = CooldownUtil.getCooldown(cooldownName);

                    corePlayer.sendLangMessage("CHAT_COOLDOWN", "%time%", NumberUtil.getTime(cooldownMillis));
                    return;
                }

                String donateMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));

                for (CorePlayer donatePlayer : PlazmixCore.getInstance().getOnlinePlayers(onlinePlayer -> !onlinePlayer.getGroup().isDefault())) {
                    donatePlayer.sendMessage("§d§lDonateChat §8:: " + corePlayer.getDisplayName() + " §8» " + corePlayer.getGroup().getSuffix() + donateMessage);
                }

                CooldownUtil.putCooldown(cooldownName, 1000L * 30);
        }
    }

}
