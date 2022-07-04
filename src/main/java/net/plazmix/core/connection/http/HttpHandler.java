package net.plazmix.core.connection.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.connection.callback.Callback;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Callback<String> callback;
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            callback.done(null, cause);
        } finally {
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {

            HttpResponse response = (HttpResponse) msg;
            int responseCode = response.status().code();

            if (responseCode == HttpResponseStatus.NO_CONTENT.code()) {
                done(ctx);
                return;
            }

            if (responseCode != HttpResponseStatus.OK.code()) {
                throw new IllegalStateException("Expected HTTP response 200 OK, got " + response.status());
            }
        }

        if (msg instanceof HttpContent) {

            HttpContent content = (HttpContent) msg;
            buffer.append(content.content().toString(  StandardCharsets.UTF_8 ));

            if (msg instanceof LastHttpContent) {
                done(ctx);
            }
        }

    }

    private void done(ChannelHandlerContext ctx) {
        try {
            callback.done(buffer.toString(), null);
        } finally {
            ctx.channel().close();
        }
    }
}
