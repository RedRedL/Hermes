package red.webservertools;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.webservertools.API.Players.PlayerTracker;
import red.webservertools.Item.ModItems;
import red.webservertools.API.APIServer;



public class WebServerTools implements ModInitializer {
	public static final String MOD_ID = "webservertools";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();

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