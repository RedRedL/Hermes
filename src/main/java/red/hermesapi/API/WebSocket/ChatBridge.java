package red.hermesapi.API.WebSocket;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import red.hermesapi.HermesAPI;


public class ChatBridge {
    public static final Channel connection = WebSocketConnectorHandler.webSocketConnection;

    public static void initialize() {
        register();
    }


    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) -> {
            // Access raw message content (without decorations)
            String raw = message.getContent().getString();
            String name = sender.getName().getString();

            String sentMessage = "[" + name + "] " + raw;


            for (Channel ch : WebSocketConnectorHandler.webSocketConnections) {
                HermesAPI.LOGGER.info(ch.toString());
                if (ch.isActive()) {
                    ch.writeAndFlush(new TextWebSocketFrame(sentMessage));
                }
            }

        });
    }
}

