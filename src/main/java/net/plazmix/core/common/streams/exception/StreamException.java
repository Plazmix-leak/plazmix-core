package net.plazmix.core.common.streams.exception;

import lombok.NonNull;

public class StreamException extends RuntimeException {

    public StreamException(@NonNull String message, Object... objects) {
        super(String.format(message, objects));
    }

}
