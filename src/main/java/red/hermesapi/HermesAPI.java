package red.hermesapi;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import red.hermesapi.API.Players.PlayerTracker;
import red.hermesapi.API.APIServer;
import red.hermesapi.API.WebSocket.ChatBridge;
import red.hermesapi.Config.ConfigLoader;
import red.hermesapi.Config.HermesConfig;


public class HermesAPI implements ModInitializer {
	public static final String MOD_ID = "hermes_api";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Load config file
		HermesConfig config = ConfigLoader.loadConfig();

		// When the server runs
		LOGGER.info("Hello Fabric world!");


		//Initialise player tracker
		PlayerTracker.initialize();

		//Initialise Chat tracker
		ChatBridge.initialize();


		// Server on specified port
		int port = config.api_port;


		// Run the server in a separate thread to avoid blocking Minecraft
		new Thread(() -> {
			try {
				new APIServer(port).run(); // Attempt to start sever on port
				LOGGER.info("APIServer Online!");
			} catch (Exception e) { // Shit might go wrong
				LOGGER.error("API Server crashed!", e);
			}
		}, "API Thread").start(); // Give the thread a name for debugging


	}
}