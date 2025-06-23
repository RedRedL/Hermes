package red.hermesapi.API.Players;


import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import red.hermesapi.API.APIHandler;
import red.hermesapi.API.SSEHandler;
import red.hermesapi.HermesAPI;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class PlayerTracker {
    private static final ConcurrentHashMap<String, ServerPlayerEntity> onlinePlayers = new ConcurrentHashMap<>();


    public static void initialize() {
            HermesAPI.LOGGER.info("Initializing PlayerTracker for " + HermesAPI.MOD_ID);
            onlinePlayers.clear();

            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                ServerPlayerEntity player = handler.player;
                String UUID = player.getUuidAsString();
                onlinePlayers.put(UUID, player);
                SSEHandler.broadcast(player.getName().getString() + " has joined!");
            });

            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                String UUID = handler.player.getUuidAsString();
                onlinePlayers.remove(UUID);
                SSEHandler.broadcast(handler.player.getName().getString() + " has left.");
            });
        }


    // Helper method to get names
    public static List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (ServerPlayerEntity player : onlinePlayers.values()) {
            names.add(player.getName().getString());
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
        }


    public static int getOnlinePlayerCount() {
        return onlinePlayers.size();
        }

    }
