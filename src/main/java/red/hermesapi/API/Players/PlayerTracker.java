package red.hermesapi.API.Players;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.dynalink.StandardOperation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import red.hermesapi.API.APIHandler;
import red.hermesapi.API.SSEHandler;
import red.hermesapi.HermesAPI;


import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class PlayerTracker {
    private static final ConcurrentHashMap<String, ServerPlayerEntity> onlinePlayers = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ConcurrentHashMap> playerDatabase = new ConcurrentHashMap<>();


    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Paths.get("config/hermesapi");
    private static final Path DATA_FILE = CONFIG_DIR.resolve("player_data.json");



    public static void initialize() {
        HermesAPI.LOGGER.info("Initializing PlayerTracker for " + HermesAPI.MOD_ID);
        onlinePlayers.clear();

        //Load  player data
        loadPlayerData();


        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            String UUID = player.getUuidAsString();
            onlinePlayers.put(UUID, player);
            SSEHandler.broadcast(player.getName().getString() + " has joined!");
            savePlayerData();
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


    private static void savePlayerData() {
        try {
            // Create parent directories if they don't exist
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }

            // Double Hash map for user data
            // {UUID : {name: , ip: }}


            onlinePlayers.forEach((uuid, player) -> {
                ConcurrentHashMap<String, String> playerData = new ConcurrentHashMap<>();
                playerData.put("name", player.getName().getString());
                playerData.put("ip", player.getIp());
                playerDatabase.put(uuid, playerData);
            });


            // Atomic write pattern
            Path tempFile = CONFIG_DIR.resolve("player_data.tmp");

            // Write to temp file first
            try (BufferedWriter writer = Files.newBufferedWriter(
                    tempFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                gson.toJson(playerDatabase, writer);
            }

            // Atomically replace old file
            Files.move(
                    tempFile,
                    DATA_FILE,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );

        } catch (AccessDeniedException e) {
            HermesAPI.LOGGER.error("Permission denied when saving player data. Please check directory permissions for: {}", CONFIG_DIR);
        } catch (IOException e) {
            HermesAPI.LOGGER.error("Failed to save player data", e);
        }
    }


    private static void loadPlayerData() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
            Map<String, Map<String, String>> loadedData = gson.fromJson(reader, type);

            if (loadedData != null) {
                loadedData.forEach((uuidStr, playerData) -> {
                    ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
                    data.put("name", playerData.get("name"));
                    data.put("ip", playerData.get("ip"));
                    playerDatabase.put(uuidStr, data);
                });
            }
        } catch (IOException e) {
            HermesAPI.LOGGER.error("Failed to load player data from {}", DATA_FILE, e);
        }
    }
}