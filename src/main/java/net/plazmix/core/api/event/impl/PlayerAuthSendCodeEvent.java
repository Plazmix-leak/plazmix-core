package net.plazmix.core.api.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.event.Event;
import net.plazmix.core.common.auth.AuthPlayer;

@RequiredArgsConstructor
@Getter
public class PlayerAuthSendCodeEvent extends Event {

    private final AuthPlayer authPlayer;
}
