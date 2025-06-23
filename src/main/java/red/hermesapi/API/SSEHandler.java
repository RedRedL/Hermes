package red.hermesapi.API;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import red.hermesapi.API.APIHandler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SSEHandler extends ChannelInboundHandlerAdapter {

    private static final Set<Channel> sseConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            if ("/players/connections".equals(request.uri())) {
                handleSSE(ctx, request);
                return; //End request
            }
        }

        // Not SSE, pass down the pipeline
        ctx.fireChannelRead(msg);
    }

    private void handleSSE(ChannelHandlerContext ctx, FullHttpRequest request) {

        // Establosh connection
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);

        // Write response headers and flush
        ctx.writeAndFlush(response);

        // Write initial event to keep connection open
        ctx.writeAndFlush(Unpooled.copiedBuffer("", CharsetUtil.UTF_8));

        // Add channel to active SSE list for messaging
        sseConnections.add(ctx.channel());

        // Periodic keepalive
        ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()) {
                ctx.writeAndFlush(Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
            }
        }, 15, 15, TimeUnit.SECONDS);
    }

    public static void broadcast(String message) {
        String formatted = message + "\n";
        for (Channel ch : sseConnections) {
            if (ch.isActive()) {
                ch.writeAndFlush(Unpooled.copiedBuffer(formatted, CharsetUtil.UTF_8));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (sseConnections.contains(ctx.channel())) {
            sseConnections.remove(ctx.channel());
            System.out.println("SSE client disconnected: " + ctx.channel());
        }
        super.channelInactive(ctx);
    }
}
