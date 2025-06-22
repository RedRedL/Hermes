package red.webservertools.API.Players;


import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import red.webservertools.WebServerTools;


import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;


public class PlayerTracker {
    private static final Set<ServerPlayerEntity> onlinePlayers = new CopyOnWriteArraySet<>();

    public static void initialize() {
            WebServerTools.LOGGER.info("Initializing PlayerTracker for " + WebServerTools.MOD_ID);
            onlinePlayers.clear();

            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                ServerPlayerEntity player = handler.player;
                onlinePlayers.add(player);
            });

            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                ServerPlayerEntity player = handler.player;
                onlinePlayers.remove(player);
            });
        }

    public static Set<ServerPlayerEntity> getOnlinePlayers() {
        return Collections.unmodifiableSet(onlinePlayers);
        }

    // Helper method to get names
    public static List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (ServerPlayerEntity player : onlinePlayers) {
            names.add(player.getName().getString());
        }
        return names;
        }

    public static int getOnlinePlayerCount() {
        return onlinePlayers.size();
        }

    }
