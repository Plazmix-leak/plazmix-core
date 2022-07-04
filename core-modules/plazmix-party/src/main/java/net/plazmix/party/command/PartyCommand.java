package net.plazmix.party.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.party.Party;
import net.plazmix.core.common.party.PartyManager;
import net.plazmix.core.common.party.PartyRequestManager;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PartyCommand extends CommandExecutor {

    public PartyCommand() {
        super("party", "пати", "парти", "p");

        setCanUseLoginServer(true);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            sendHelpMessage(corePlayer);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "invite": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/party invite <ник>");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не можете добавить в компанию самого себя!");
                    break;
                }

                if (PartyManager.INSTANCE.hasParty(targetPlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Данный игрок уже состоит в другой компании!");
                    break;
                }

                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party != null && party.isMember(targetPlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Данный игрок уже состоит в Вашей компании!");
                    break;
                }

                if (PartyRequestManager.INSTANCE.hasPartyRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы уже приглашали данного игрока в компанию!");
                    break;
                }

                PartyRequestManager.INSTANCE.addPartyRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId());
                corePlayer.sendMessage("§6§lParty §8:: §fПриглашение в компанию было §aуспешно §fотправлено " + targetPlayer.getDisplayName());

                targetPlayer.sendMessage(ChatMessageType.CHAT, JsonChatMessage.create("§6§lParty §8:: " + corePlayer.getDisplayName() + " §fпригласил Вас в кооперативную игру:\n\n")
                        .addComponents(JsonChatMessage.create("    ").build())

                        .addComponents(JsonChatMessage.create("§a§l[ПРИНЯТЬ]")
                                .addClick(ClickEvent.Action.RUN_COMMAND, "/party accept " + corePlayer.getName())
                                .addHover(HoverEvent.Action.SHOW_TEXT, "§aПринять приглашение в кооперативную игру с " + corePlayer.getName())
                                .build())

                        .addComponents(JsonChatMessage.create("    ").build())

                        .addComponents(JsonChatMessage.create("§c§l[ОТКЛОНИТЬ]")
                                .addClick(ClickEvent.Action.RUN_COMMAND, "/party cancel " + corePlayer.getName())
                                .addHover(HoverEvent.Action.SHOW_TEXT, "§cОтклонить приглашение в кооперативную игру с " + corePlayer.getName())
                                .build())

                        .addText("\n")
                        .build());

                break;
            }

            case "accept": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/party accept <ник>");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cВы не можете добавить в компанию самого себя!");
                    break;
                }

                if (PartyManager.INSTANCE.hasParty(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cВы уже состоите в другой компании!");
                    break;
                }

                if (!PartyRequestManager.INSTANCE.hasPartyRequest(targetPlayer.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cУ Вас нет приглашения в компанию от данного игрока!");
                    break;
                }

                PartyRequestManager.INSTANCE.removeAll(corePlayer.getPlayerId());

                Party party = PartyManager.INSTANCE.getParty(targetPlayer) == null ? PartyManager.INSTANCE.createParty(targetPlayer) : PartyManager.INSTANCE.getParty(targetPlayer);
                party.addMember(corePlayer);

                party.alert("§6§lParty §8:: " + corePlayer.getDisplayName() + " §fвступил в Вашу компанию по приглашению " + targetPlayer.getDisplayName());
                break;
            }

            case "cancel": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/party cancel <ник>");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не можете добавить в компанию самого себя!");
                    break;
                }

                if (!PartyRequestManager.INSTANCE.hasPartyRequest(targetPlayer.getPlayerId(), corePlayer.getPlayerId())) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, у Вас нет приглашения в компанию от данного игрока!");
                    break;
                }

                PartyRequestManager.INSTANCE.removeAll(corePlayer.getPlayerId());

                targetPlayer.sendMessage("§d§lParty §8:: " + corePlayer.getDisplayName() + " §cотклонил §fВаше приглашение!");
                corePlayer.sendMessage("§d§lParty §8:: §fВы успешно §cотклонили §fприглашение от " + targetPlayer.getDisplayName());

                PartyRequestManager.INSTANCE.removePartyRequest(corePlayer.getPlayerId(), targetPlayer.getPlayerId());
                break;
            }

            case "kick": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/party kick <ник>");
                    break;
                }

                if (!PartyManager.INSTANCE.hasParty(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8::§cОшибка, Вы не состоите в компании!");
                    break;
                }

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (!targetPlayer.isOnline()) {
                    commandSender.sendLangMessage("PLAYER_OFFLINE");
                    break;
                }

                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (!party.isLeader(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8::§cОшибка, Вы не являетесь лидером компании!");
                    return;
                }

                if (!party.isMember(targetPlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8::§cДанный игрок не состоит в Вашей компании!");
                    break;
                }

                if (targetPlayer.getName().equals(corePlayer.getName())) {
                    commandSender.sendMessage("§d§lPlazmix §8::§cВы не можете кикнуть из компании самого себя!");
                    break;
                }

                party.alert("§d§lParty §8:: " + targetPlayer.getDisplayName() + " §fбыл кикнуть из компании лидером " + corePlayer.getDisplayName() + "!");
                party.removeMember(targetPlayer);

                if (party.getMembers().size() <= 1) {
                    party.alert("§d§lParty §8:: §fКомпания была расформирована из-за того, что все ее участники вышли!");

                    PartyManager.INSTANCE.deleteParty(party);
                }

                break;
            }

            case "warp": {
                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не состоите в компании!");
                    break;
                }

                if (!party.isLeader(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не являетесь лидером компании!");
                    break;
                }

                party.warp(corePlayer.getBukkitServer());
                party.alert("§d§lParty §8:: §fЛидер " + corePlayer.getDisplayName() + " §fпереместил Вас на сервер §e" + corePlayer.getBukkitServer().getName());
                break;
            }

            case "list": {
                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не состоите в компании!");
                    break;
                }

                commandSender.sendMessage("§d§lParty §8:: §fСписок участников компании:");

                for (CorePlayer partyMember : party.getMembers().stream().map(PlazmixCore.getInstance()::getPlayer).collect(Collectors.toSet())) {
                    commandSender.sendMessage(" §8• " + partyMember.getDisplayName()
                            + " §7(" + partyMember.getBukkitServer().getName() + ")"
                            + (party.isLeader(partyMember) ? " §8§l| §c§lЛИДЕР" : ""));
                }

                break;
            }

            case "leave": {
                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не состоите в компании!");
                    break;
                }

                if (party.isLeader(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, лидер компании не может покинуть ее! Чтобы удалить компанию, пишите - /party disband");
                    break;
                }

                party.alert("§d§lParty §8:: " + corePlayer.getDisplayName() + " §fсамостоятельно покинул компанию!");
                party.removeMember(corePlayer);

                if (party.getMembers().size() <= 1) {
                    party.alert("§d§lParty §8:: §fКомпания была расформирована из-за того, что все ее участники вышли!");

                    PartyManager.INSTANCE.deleteParty(party);
                }

                break;
            }

            case "disband": {
                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не состоите в компании!");
                    break;
                }

                if (!party.isLeader(corePlayer)) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не являетесь лидером компании!");
                    break;
                }

                party.alert("§d§lParty §8:: " + corePlayer.getDisplayName() + " §fраспустил компанию!");
                PartyManager.INSTANCE.deleteParty(party);
                break;
            }

            case "chat": {
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/party chat <сообщение>");
                    break;
                }

                Party party = PartyManager.INSTANCE.getParty(corePlayer);

                if (party == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не состоите в компании!");
                    break;
                }

                String chatMessage = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
                party.alert("§3§lPC §8:: " + corePlayer.getDisplayName() + " §8➥ " +corePlayer.getGroup().getSuffix() + chatMessage);
                break;
            }

            default: {
                sendHelpMessage(corePlayer);
            }
        }
    }

    private void sendHelpMessage(@NonNull CorePlayer corePlayer) {
        Party party = PartyManager.INSTANCE.getParty(corePlayer);

        if (party != null && party.isLeader(corePlayer)) {
            corePlayer.sendLangMessage("HAS_PARTY_HELP");

            return;
        }

        corePlayer.sendLangMessage("NO_PARTY_HELP");
    }

}