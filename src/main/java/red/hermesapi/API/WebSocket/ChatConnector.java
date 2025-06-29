package red.hermesapi.API.WebSocket;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import red.hermesapi.API.Players.PlayerTracker;
import red.hermesapi.HermesAPI;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.util.CharsetUtil.UTF_8;

public class ChatConnector extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        if (uri.equals("/chat")) {
            upgradeConnection(ctx, request);
        } else {
            ctx.fireChannelRead(request.retain());
        }
    }

    private void upgradeConnection(ChannelHandlerContext ctx, FullHttpRequest request) {
        //HttpHeaders headers = request.headers();
        ctx.pipeline().replace(this, "ChatConnector", new WebSocketHandler());

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(request), null, true);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }


    protected String getWebSocketURL(FullHttpRequest request) {
        System.out.println("Req URI : " + request.uri());
        String url =  "ws://" + request.headers().get("Host") + request.uri() ;
        System.out.println("Constructed URL : " + url);
        return url;
    }
}