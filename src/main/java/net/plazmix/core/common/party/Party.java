package net.plazmix.core.common.party;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class Party {

    private final String leader;
    private final List<String> members = new ArrayList<>();

    public void addMember(@NonNull CorePlayer member) {
        members.add(member.getName().toLowerCase());

        PartyManager.INSTANCE.addMemberToParty(this, member);

        for (String memberName : members) {
            PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixParty", "PLAYER_PARTY_" + memberName, this);
        }
    }

    public void removeMember(@NonNull CorePlayer member) {
        for (String memberName : members) {

            if (!memberName.equalsIgnoreCase(member.getName())) {
                PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixParty", "PLAYER_PARTY_" + memberName, this);

            } else {

                PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.DELETE, "TynixParty", "PLAYER_PARTY_" + memberName, null);
            }
        }

        members.remove(member.getName().toLowerCase());

        PartyManager.INSTANCE.removeMemberToParty(member);
    }

    public boolean isLeader(@NonNull CorePlayer corePlayer) {
        return corePlayer.isOnline() && leader.equalsIgnoreCase(corePlayer.getName());
    }

    public boolean isMember(@NonNull CorePlayer corePlayer) {
        return members.contains(corePlayer.getName().toLowerCase()) && !isLeader(corePlayer);
    }

    public void alert(@NonNull String alertMessage) {
        for (CorePlayer corePlayer : members.stream().map(PlazmixCore.getInstance()::getPlayer).collect(Collectors.toList())) {

            if (corePlayer != null && corePlayer.isOnline()) {
                corePlayer.sendMessage(alertMessage);
            }
        }
    }

    public void alertLang(@NonNull String messageKey, String... placeholders) {
        for (CorePlayer corePlayer : members.stream().map(PlazmixCore.getInstance()::getPlayer).collect(Collectors.toList())) {

            if (corePlayer != null && corePlayer.isOnline()) {
                corePlayer.sendLangMessage(messageKey, placeholders);
            }
        }
    }

    public void alertLang(@NonNull String messageKey) {
        alertLang(messageKey, new String[0]);
    }

    public void warp(@NonNull BukkitServer bukkitServer) {
        for (CorePlayer corePlayer : members.stream().map(PlazmixCore.getInstance()::getPlayer).collect(Collectors.toList())) {
            corePlayer.connectToServer(bukkitServer);
        }
    }

}