package net.plazmix.guilds.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.guild.CoreGuild;
import net.plazmix.core.common.guild.GuildSqlHandler;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.guilds.GuildRequestManager;
import net.plazmix.guilds.inventory.GuildMembersInventory;
import net.plazmix.guilds.inventory.GuildMenuInventory;
import net.plazmix.guilds.inventory.GuildTopInventory;

import java.util.Arrays;

public final class GuildCommand extends CommandExecutor {

    public GuildCommand() {
        super("g", "guilds", "guild", "гильдия", "гильдия");

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

            case "create":
            case "создать": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild create <название>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild != null) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас уже есть гильдия!");
                    break;
                }

                if (corePlayer.getGroup().getLevel() < Group.LUXURY.getLevel()) {
                    commandSender.sendLangMessage("NO_PERM", "%group%", Group.LUXURY.getColouredName());
                    break;
                }

                if (corePlayer.getPlazma() < 20) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, для создания гильдии необходимо иметь §620 плазмы");
                    break;
                }

                if (GuildSqlHandler.INSTANCE.getFromTitle(args[1]) != null) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, гильдия с указанным названием уже существует!");
                    break;
                }

                guild = CoreGuild.create(corePlayer.getName(), args[1]);
                guild.addPlayer(corePlayer.getPlayerId(), CoreGuild.GuildStatus.LEADER);

                corePlayer.takePlazma(20);
                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно создали гильдию с названием: §e" + ChatColor.translateAlternateColorCodes('&', args[1]));
                break;
            }

            case "invite":
            case "пригласить": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild invite <ник>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);
                CorePlayer targetPlayer = PlazmixCore.getInstance().getPlayer(args[1]);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cВы не можете добавить в компанию самого себя!");
                    break;
                }

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                CoreGuild guildTarget = CoreGuild.of(targetPlayer);

                if (guildTarget != null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок уже состоит в гильдии!");
                    break;
                }

                if (guild.getMemberIdsMap().size() == 10 && corePlayer.getGroup().getLevel() <= 5) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не можете пригласить больше 15 участников в гильдию");
                    break;
                }

                if (guild.getMemberIdsMap().size() == 15 && corePlayer.getGroup().getLevel() <= 6) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не можете пригласить больше 15 участников в гильдию");
                    break;
                }

                if (guild.getMemberIdsMap().size() == 20 && corePlayer.getGroup().getLevel() <= 7) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не можете пригласить больше 20 участников в гильдию");
                    break;
                }

                if (GuildRequestManager.INSTANCE.hasGuildRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId())) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не можете пригласить этого участника, так как у него уже есть запрос в гильдию!");
                    break;
                }

                if (guild.getLeaderId() != corePlayer.getPlayerId()) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не можете пригласить этого участника, так как вы не являетесь лидером!");
                    break;
                }

                GuildRequestManager.INSTANCE.addGuildRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId());
                corePlayer.sendMessage("§d§lГильдии §8:: §fПриглашение в гильдию было §aуспешно §fотправлено " + targetPlayer.getDisplayName());

                targetPlayer.sendMessage(ChatMessageType.CHAT, JsonChatMessage.create("§d§lГильдии §8:: " + corePlayer.getDisplayName() + " §fпригласил Вас в гильдию: §c" + ChatColor.translateAlternateColorCodes('&', guild.getTitle()))
                        .addComponents(JsonChatMessage.create("    ").build())

                        .addComponents(JsonChatMessage.create("§a§l[ПРИНЯТЬ]")
                                .addClick(ClickEvent.Action.RUN_COMMAND, "/guild accept " + corePlayer.getName())
                                .addHover(HoverEvent.Action.SHOW_TEXT, "§aПринять приглашение в гильдию от " + corePlayer.getName())
                                .build())

                        .addComponents(JsonChatMessage.create("    ").build())

                        .addComponents(JsonChatMessage.create("§c§l[ОТКЛОНИТЬ]")
                                .addClick(ClickEvent.Action.RUN_COMMAND, "/guild cancel " + corePlayer.getName())
                                .addHover(HoverEvent.Action.SHOW_TEXT, "§cОтклонить приглашение в гильдию от " + corePlayer.getName())
                                .build())

                        .addText("\n")
                        .build());
                break;
            }

            case "accept": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild accept <ник>");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);
                CoreGuild coreGuild = CoreGuild.of(targetPlayer);

                if (coreGuild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у данного игрока нет своей гильдии!");
                    break;
                }

                if (CoreGuild.of(corePlayer) != null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cВы уже состоите в другой гильдии!");
                    break;
                }

                if (!GuildRequestManager.INSTANCE.hasGuildRequest(targetPlayer.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cУ Вас нет приглашения в компанию от данного игрока!");
                    break;
                }

                GuildRequestManager.INSTANCE.removeAll(corePlayer.getPlayerId());
                coreGuild.addPlayer(corePlayer.getName(), CoreGuild.GuildStatus.MEMBER);

                coreGuild.alert("§d§lГильдии §8:: " + corePlayer.getDisplayName() + " §fвступил в Вашу гильдию по приглашению " + targetPlayer.getDisplayName());
                break;
            }

            case "cancel": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild cancel <ник>");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                if (!GuildRequestManager.INSTANCE.hasGuildRequest(targetPlayer.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет приглашения в гильдию от данного игрока!");
                    break;
                }

                GuildRequestManager.INSTANCE.removeAll(corePlayer.getPlayerId());

                targetPlayer.sendMessage("§d§lГильдии §8:: " + corePlayer.getDisplayName() + " §cотклонил §fВаше приглашение!");
                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно §cотклонили §fприглашение от " + targetPlayer.getDisplayName());

                GuildRequestManager.INSTANCE.removeGuildRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId());
                break;
            }

            case "r":
            case "request":
            case "requests": {
                int requestsCount = GuildRequestManager.INSTANCE.getGuildsRequestsIds(corePlayer.getPlayerId()).size();

                if (requestsCount <= 0) {
                    commandSender.sendLangMessage("GUILD_DONT_HAVE_INVITES");

                    return;
                }

                commandSender.sendLangMessage("GUILD_LIST_REQUESTS",
                        "%count%", NumberUtil.spaced(requestsCount));

                int guildCounter = 1;
                for (CorePlayer offlinePlayer : GuildRequestManager.INSTANCE.getOfflineGuildIds(corePlayer.getPlayerId())) {

                    JsonChatMessage.create(" §e" + guildCounter + ". " + offlinePlayer.getDisplayName())
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §aпринять")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/guild accept " + offlinePlayer.getName())

                            .sendMessage(corePlayer);

                    guildCounter++;
                }

                break;
            }

            case "disband":
            case "расформировать": {
                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (guild.getStatus(corePlayer.getName()) != CoreGuild.GuildStatus.LEADER) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не являетесь лидером гильдии");
                    break;
                }

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно удалили свою гильдию §c" + ChatColor.translateAlternateColorCodes('&', guild.getTitle()));
                CoreGuild.delete(guild);
                break;
            }

            case "shop":
            case "магазин": {
                break;
            }

            case "top": {
                corePlayer.openInventory(new GuildTopInventory());
                break;
            }

            case "chat":
            case "чат": {
                CoreGuild guild = CoreGuild.of(corePlayer.getName());

                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild chat <сообщение>");
                    break;
                }

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                String chatMessage = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
                guild.alert("§3§lGC §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName() + " §8➥ " +corePlayer.getGroup().getSuffix() + chatMessage);

                break;
            }

            case "leave":
            case "выйти": {
                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (corePlayer.getPlayerId() == guild.getLeaderId()) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, чтобы выйти из своей гильдии, необходимо удалить ее - /guild disband");
                    break;
                }

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно вышли из гильдии §c" + ChatColor.translateAlternateColorCodes('&', guild.getTitle()));
                guild.removePlayer(corePlayer.getName());
                break;
            }

            case "меню":
            case "menu": {
                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у данного игрока нет своей гильдии!");
                    break;
                }

                corePlayer.openInventory(new GuildMenuInventory());
                break;
            }

            case "list":
            case "members":
            case "участники": {
                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                corePlayer.openInventory(new GuildMembersInventory());

                break;

            }

            case "addcoins": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild addcoins <кол-во>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (!ValidateUtil.isNumber(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, аргумент не является числом!");
                    break;
                }

                int money = Integer.parseInt(args[1]);

                if (corePlayer.getCoins() < money) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, у вас недостаточно коинов для пополнения гильдии!");
                    break;
                }

                guild.getEconomy().addCoins(Integer.parseInt(args[1]));
                corePlayer.takeCoins(Integer.parseInt(args[1]));

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно внесли на бюджет гильдии §a" + Integer.parseInt(args[1]) + " §fкоинов");
                guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName()  + " §fвнёс в бюджет гильдии §a" + Integer.parseInt(args[1]) + " §fкоинов");
                break;
            }

            case "removecoins": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild removecoins <кол-во>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (!ValidateUtil.isNumber(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fОшибка, аргумент не является числом!");
                    break;
                }

                if (guild.getEconomy().getCoins() <= Integer.parseInt(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fОшибка, в бюджете гильдии недостаточно коинов!");
                    break;
                }

                corePlayer.addCoins(Integer.parseInt(args[1]));
                guild.getEconomy().removeCoins(Integer.parseInt(args[1]));

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно сняли с бюджета гильдии " + Integer.parseInt(args[1]) + " коинов");
                guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName()  + " §fснял с бюджета гильдии §a" + Integer.parseInt(args[1]) + " §fкоинов");
                break;
            }

            case "addplazma": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild addplazma <кол-во>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (!ValidateUtil.isNumber(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, аргумент не является числом!");
                    break;
                }

                int money = Integer.parseInt(args[1]);

                if (corePlayer.getPlazma() < money) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, у вас недостаточно плазмы для пополнения гильдии!");
                    break;
                }

                corePlayer.takePlazma(Integer.parseInt(args[1]));
                guild.getEconomy().addGolds(Integer.parseInt(args[1]));

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно внесли на бюджет гильдии " + Integer.parseInt(args[1]) + " плазмы");
                guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName()  + " §fвнёс в бюджет гильдии §e" + Integer.parseInt(args[1]) + " §fплазмы");
                break;
            }

            case "removeplazma": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild removeplazma <кол-во>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (!ValidateUtil.isNumber(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fОшибка, аргумент не является числом!");
                    break;
                }

                if (guild.getEconomy().getGolds() <= Integer.parseInt(args[1])) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fОшибка, в бюджете гильдии недостаточно плазмы!");
                    break;
                }

                corePlayer.addPlazma(Integer.parseInt(args[1]));
                guild.getEconomy().removeGolds(Integer.parseInt(args[1]));

                corePlayer.sendMessage("§d§lГильдии §8:: §fВы успешно сняли с бюджета гильдии " + Integer.parseInt(args[1]) + " плазмы");
                guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName()  + " §fснял с бюджета гильдии §e" + Integer.parseInt(args[1]) + " §fплазмы");
                break;
            }

            case "kick":
            case "кикнуть": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild kick <ник>");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);
                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (guild.getStatus(corePlayer.getName()) != CoreGuild.GuildStatus.LEADER) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не являетесь лидером гильдии");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cВы не можете кикнуть самого себя!");
                    break;
                }

                if (!guild.hasPlayer(targetPlayer.getName())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок не состоит в вашей гильдии");
                    break;
                }

                guild.removePlayer(targetPlayer.getName());
                guild.alert("§d§lГильдии §8:: " + targetPlayer.getDisplayName() + " §fбыл кикнут из гильдии лидером " + corePlayer.getDisplayName());
                break;
            }

            case "settings":
            case "настройки": {
                // Logic
                break;
            }

            case "rankup":
            case "setrank":
            case "giverank":
            case "rank": {
                if (args.length < 3) {
                    commandSender.sendMessage("§d§lГильдии §8:: §fИспользуйте - §d/guild rank <ник> <ранг>");
                    commandSender.sendMessage("§fСписок рангов:");

                    commandSender.sendMessage(ChatColor.GREEN + "MEMBER");
                    commandSender.sendMessage(ChatColor.AQUA + "HELPER");
                    commandSender.sendMessage(ChatColor.RED + "MODER");
                    break;
                }

                CoreGuild guild = CoreGuild.of(corePlayer);
                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (guild == null) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                if (guild.getStatus(corePlayer.getName()) != CoreGuild.GuildStatus.LEADER) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, Вы не являетесь лидером гильдии");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cВы не можете выдавать ранг самому себе!");
                    break;
                }

                if (!guild.hasPlayer(targetPlayer.getName())) {
                    commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок не состоит в вашей гильдии");
                    break;
                }

                switch (args[2].toLowerCase()) {

                    case "default":
                    case "member":
                    case "стандартный":
                    case "player": {
                        if (guild.getStatus(targetPlayer.getName()) == CoreGuild.GuildStatus.MEMBER) {
                            commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок и так имеет ранг " + CoreGuild.GuildStatus.MEMBER.getDisplayName());
                            break;
                        }

                        guild.addPlayer(targetPlayer.getName(), CoreGuild.GuildStatus.MEMBER);
                        guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName() + " §fустановил игроку " + targetPlayer.getDisplayName() + " §fранг: " + CoreGuild.GuildStatus.MEMBER.getDisplayName());
                        break;
                    }

                    case "helper":
                    case "помощник":
                    case "хелпер": {
                        if (guild.getStatus(targetPlayer.getName()) == CoreGuild.GuildStatus.HELPER) {
                            commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок и так имеет ранг " + CoreGuild.GuildStatus.HELPER.getDisplayName());
                            break;
                        }

                        guild.addPlayer(targetPlayer.getName(), CoreGuild.GuildStatus.HELPER);
                        guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName() + " §fустановил игроку " + targetPlayer.getDisplayName() + " §fранг: " + CoreGuild.GuildStatus.HELPER.getDisplayName());
                        break;
                    }

                    case "moder":
                    case "модератор":
                    case "модер": {
                        if (guild.getStatus(targetPlayer.getName()) == CoreGuild.GuildStatus.MODER) {
                            commandSender.sendMessage("§d§lГильдии §8:: §cОшибка, данный игрок и так имеет ранг " + CoreGuild.GuildStatus.MODER.getDisplayName());
                            break;
                        }

                        guild.addPlayer(targetPlayer.getName(), CoreGuild.GuildStatus.MODER);
                        guild.alert("§d§lГильдии §8:: " + guild.getStatus(corePlayer.getName()).getDisplayName() + " " + corePlayer.getDisplayName() + " §fустановил игроку " + targetPlayer.getDisplayName() + " §fранг: " + CoreGuild.GuildStatus.MODER.getDisplayName());
                        break;
                    }
                }
                break;
            }

            case "test":
            case "тест": {
                CoreGuild guild = CoreGuild.of(corePlayer);

                if (guild == null) {
                    corePlayer.sendMessage("§d§lГильдии §8:: §cОшибка, у Вас нет гильдии!");
                    break;
                }

                corePlayer.sendMessage(guild.toJson());
                break;
            }

            default:
                sendHelpMessage(commandSender);
        }
    }

    private void sendHelpMessage(@NonNull CommandSender commandSender) {
        CoreGuild guild = CoreGuild.of(commandSender.getName());

        if (guild == null) {
            commandSender.sendLangMessage("NO_GUILD_HELP");
            return;
        }

        if (guild.getStatus(commandSender.getName()) != CoreGuild.GuildStatus.LEADER) {
            commandSender.sendLangMessage("HAS_GUILD_MEMBER_HELP");

        } else {
            commandSender.sendLangMessage("HAS_GUILD_LEADER_HELP");
        }

    }

}
