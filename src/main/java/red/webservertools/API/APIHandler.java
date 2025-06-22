package red.webservertools.API;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import net.minecraft.server.MinecraftServer;
import red.webservertools.API.Players.PlayerTracker;
import red.webservertools.WebServerTools;

import java.util.List;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.util.CharsetUtil.UTF_8;


/**
 * Handles a server-side channel.
 */
public class APIHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // (1)


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        WebServerTools.LOGGER.info("Received HTTP request: " + uri);
        if (uri.equals("/players/count")) {

            int playerCount = PlayerTracker.getOnlinePlayerCount();

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    copiedBuffer("Online players: " + playerCount, UTF_8)
            );
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else if (uri.equals("/players/names")) {

            List<String> onlinePlayersList = PlayerTracker.getOnlinePlayerNames();
            String playerNames = String.join(", ", onlinePlayersList);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    copiedBuffer("Online players: " + playerNames, UTF_8)
            );
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}