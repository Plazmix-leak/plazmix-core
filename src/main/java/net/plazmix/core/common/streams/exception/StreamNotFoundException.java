package net.plazmix.core.common.streams.exception;

public class StreamNotFoundException extends StreamException {

    public StreamNotFoundException() {
        super("stream_not_found");
    }
}
