package net.plazmix.party.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.common.party.Party;
import net.plazmix.core.common.party.PartyManager;

public final class PartyHavingListener implements EventListener {

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        Party party = PartyManager.INSTANCE.getParty(event.getCorePlayer());

        if (party != null) {

            if (party.isLeader(event.getCorePlayer())) {
                party.alert("§d§lParty §8:: " + event.getCorePlayer().getDisplayName() + " §fраспустил компанию!");

                PartyManager.INSTANCE.deleteParty(party);

            } else {

                party.alert("§d§lParty §8:: " + event.getCorePlayer().getDisplayName() + " §fпокинул сервер и Вашу компанию заодно!");
                party.removeMember(event.getCorePlayer());

                if (party.getMembers().size() <= 1) {
                    party.alert("§d§lParty §8:: §fКомпания была расформирована из-за того, что все ее участники вышли!");

                    PartyManager.INSTANCE.deleteParty(party);
                }
            }
        }
    }

}
