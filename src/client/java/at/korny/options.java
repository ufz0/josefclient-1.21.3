package at.korny;

import at.korny.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static at.korny.JosefclientClient.overlays;


public class options {

    private static final File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "options.txt");
    public static final String MOD_ID = "josefclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static void loadSettings() {
        if (!optionsFile.exists()) return;
        try {
            List<String> lines = Files.readAllLines(optionsFile.toPath());
            for (String line : lines) {
                if (!line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
                for (Overlay overlay : overlays) {
                    String prefix = "josefclient." + overlay.id + ".";
                    if (key.equals(prefix + "x")) {
                        try {
                            overlay.x = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            LOGGER.error("Error parsing {} for {}: {}", key, overlay.id, value);
                        }
                    } else if (key.equals(prefix + "y")) {
                        try {
                            overlay.y = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            LOGGER.error("Error parsing {} for {}: {}", key, overlay.id, value);
                        }
                    } else if (key.equals(prefix + "visible")) {
                        overlay.visible = Boolean.parseBoolean(value);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load settings!", e);
        }
    }

    public static void saveOptions() {
        // Build our mod options map.
        Map<String, String> modOptions = new HashMap<>();
        for (Overlay o : overlays) {
            modOptions.put("josefclient." + o.id + ".x", String.valueOf(o.x));
            modOptions.put("josefclient." + o.id + ".y", String.valueOf(o.y));
            modOptions.put("josefclient." + o.id + ".visible", String.valueOf(o.visible));
        }
        // Read existing options.
        List<String> lines = new ArrayList<>();
        if (optionsFile.exists()) {
            try {
                lines = Files.readAllLines(optionsFile.toPath());
            } catch (IOException e) {
                LOGGER.error("Error reading options file", e);
            }
        }
        // Create a merged list.
        List<String> newLines = new ArrayList<>();
        Set<String> keysFound = new HashSet<>();
        for (String line : lines) {
            boolean updated = false;
            for (Map.Entry<String, String> entry : modOptions.entrySet()) {
                String key = entry.getKey();
                if (line.startsWith(key + "=")) {
                    newLines.add(key + "=" + entry.getValue());
                    keysFound.add(key);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                newLines.add(line);
            }
        }
        // Append keys that were not already present.
        for (Map.Entry<String, String> entry : modOptions.entrySet()) {
            if (!keysFound.contains(entry.getKey())) {
                newLines.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        try {
            Files.write(optionsFile.toPath(), newLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.info("Saved options");
        } catch (IOException e) {
            LOGGER.error("Error saving mod options!", e);
        }
    }
}
