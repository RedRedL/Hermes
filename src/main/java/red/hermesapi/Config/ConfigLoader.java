package red.hermesapi.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "hermes-config.json");

    public static HermesConfig loadConfig() {
        try {
            // Generate default json if config does not exist
            if (!CONFIG_FILE.exists()) {
                HermesConfig defaultConfig = new HermesConfig();
                saveConfig(defaultConfig);
                return defaultConfig;
            }

            FileReader reader = new FileReader(CONFIG_FILE);
            return GSON.fromJson(reader, HermesConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HermesConfig(); // fallback defaults
        }
    }

    public static void saveConfig(HermesConfig config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
