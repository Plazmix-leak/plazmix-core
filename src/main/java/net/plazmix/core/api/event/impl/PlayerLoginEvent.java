package net.plazmix.core.api.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.plazmix.core.api.event.CancellableEvent;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class PlayerLoginEvent extends CancellableEvent {

    private final String playerName;
    private String cancelReason;

    private final UUID playerUuid;
}
