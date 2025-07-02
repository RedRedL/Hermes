package red.hermesapi.API;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import red.hermesapi.API.Players.PlayerTracker;
import red.hermesapi.HermesAPI;
import java.util.Set;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.util.CharsetUtil.UTF_8;


/**
 * Handles a server-side channel.
 */
public class APIHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        try {
            if (uri.equals("/players/count")) {
                handlePlayerCount(ctx);
            } else if (uri.equals("/players/names")) {
                handlePlayerNames(ctx);
            }
        } catch (Exception e) {
            exceptionCaught(ctx, e);
        }
    }

    // Existing endpoint: /players/count
    private void handlePlayerCount(ChannelHandlerContext ctx) {
        int count = PlayerTracker.getOnlinePlayerCount();
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                copiedBuffer(String.valueOf(count), UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // Existing endpoint: /players/names
    private void handlePlayerNames(ChannelHandlerContext ctx) {
        List<String> names = PlayerTracker.getOnlinePlayerNames();
        String namesString = String.join(",", names);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                copiedBuffer(namesString, UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}