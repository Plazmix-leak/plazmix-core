package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;

@RequiredArgsConstructor
@Getter
public class CoreMessageToVkEvent extends Event {

    private final int peerId;
    private final String bodyMessage;
}
