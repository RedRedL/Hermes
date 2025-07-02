package red.hermesapi.API.WebSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.buffer.Unpooled.copiedBuffer;


// Initialises websocket connections
public class WebSocketConnectorHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static WebSocketServerHandshaker handshaker;
    public static Channel webSocketConnection;
    public static final Set<Channel> webSocketConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        System.out.println("HTTP request received: " + request.method() + " " + request.uri());
        if (uri.equals("/chat")) {
            upgradeConnection(ctx, request);
        } else {
            ctx.fireChannelRead(request.retain());
        }
    }

    private void upgradeConnection(ChannelHandlerContext ctx, FullHttpRequest request) {
        ctx.pipeline().replace(this, "WebSocketConnectorHandler", new WebSocketHandler());

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(request), null, true);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }

        webSocketConnections.add(ctx.channel());
        System.out.println("Connection upgraded");
    }


    protected String getWebSocketURL(FullHttpRequest request) {
        System.out.println("Req URI : " + request.uri());
        String url =  "ws://" + request.headers().get("Host") + request.uri() ;
        System.out.println("Constructed URL : " + url);
        return url;
    }
}