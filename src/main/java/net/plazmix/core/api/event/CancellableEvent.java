package net.plazmix.core.api.event;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class CancellableEvent extends Event {

    private boolean cancelled;
}
