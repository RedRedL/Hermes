package red.hermesapi;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import red.hermesapi.API.Players.PlayerTracker;

import red.hermesapi.API.APIServer;



public class HermesAPI implements ModInitializer {
	public static final String MOD_ID = "hermes_api";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");


		//Initialise player tracker
		PlayerTracker.initialize();



		// Server on port 8080
		int port = 8080;
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