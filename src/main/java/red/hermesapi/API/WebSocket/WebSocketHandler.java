package red.hermesapi.API.WebSocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private boolean receivingFragmentedMessage = false;
    private StringBuilder textBuffer = new StringBuilder();
    private ByteBuf binaryBuffer = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        if (msg instanceof WebSocketFrame) {
            System.out.println("WebSocket frame recieved");
            WebSocketFrame frame = (WebSocketFrame) msg;

            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

                if (textFrame.isFinalFragment()) {
                    System.out.println("TextWebSocketFrame (complete): " + textFrame.text());
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("Message received: " + textFrame.text()));
                } else {
                    receivingFragmentedMessage = true;
                    textBuffer.setLength(0);
                    textBuffer.append(textFrame.text());
                }

            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;

                if (binaryFrame.isFinalFragment()) {
                    System.out.println("BinaryWebSocketFrame (complete): " + binaryFrame.content());
                } else {
                    receivingFragmentedMessage = true;
                    binaryBuffer = Unpooled.buffer();
                    binaryBuffer.writeBytes(binaryFrame.content());
                }

            } else if (frame instanceof ContinuationWebSocketFrame) {
                ContinuationWebSocketFrame continuation = (ContinuationWebSocketFrame) frame;

                if (!receivingFragmentedMessage) {
                    System.out.println("Unexpected continuation frame");
                    return;
                }

                if (textBuffer.length() > 0) {
                    textBuffer.append(continuation.text());
                    if (continuation.isFinalFragment()) {
                        System.out.println("Reassembled Text Message: " + textBuffer.toString());
                        ctx.channel().writeAndFlush(
                                new TextWebSocketFrame("Message received: " + textBuffer.toString()));
                        textBuffer.setLength(0);
                        receivingFragmentedMessage = false;
                    }
                } else if (binaryBuffer != null) {
                    binaryBuffer.writeBytes(continuation.content());
                    if (continuation.isFinalFragment()) {
                        System.out.println("Reassembled Binary Message: " + binaryBuffer);
                        binaryBuffer.release();
                        binaryBuffer = null;
                        receivingFragmentedMessage = false;
                    }
                }

            } else if (frame instanceof PingWebSocketFrame) {
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));

            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("Pong received");

            } else if (frame instanceof CloseWebSocketFrame) {
                System.out.println("Close frame received");
                ctx.close();

            } else {
                System.out.println("Unsupported frame type");
                ctx.fireChannelRead(((FullHttpRequest) msg).retain());
            }
        }
    }
}
