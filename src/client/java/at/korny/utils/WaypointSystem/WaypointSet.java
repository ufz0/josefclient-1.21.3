package at.korny.utils.WaypointSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class WaypointSet {

    // Determines the filename based on singleplayer or multiplayer.
    private static String getFilename() {
        MinecraftClient client = MinecraftClient.getInstance();
        String filename;
        if(client.isInSingleplayer()) {
            String worldName = client.getServer().getSaveProperties().getLevelName().toString();
            filename = worldName + "-waypoints.txt";
        } else {
            String serverIP = MinecraftClient.getInstance().getCurrentServerEntry().toString();
            filename = serverIP + "-waypoints.txt";
        }
        return filename;
    }

    // Save a waypoint with the given name. The format is: name | x | y | z
    public static void saveWaypoint(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = (int) client.player.getX();
        int y = (int) client.player.getY();
        int z = (int) client.player.getZ();
        String waypointEntry = name + " | " + x + " | " + y + " | " + z;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilename(), true))) {
            writer.write(waypointEntry);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Delete a waypoint by name. Any line starting with "name | " will be removed.
    public static void deleteWaypoint(String name) {
        String filename = getFilename();
        File file = new File(filename);
        if (!file.exists()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("No waypoints found to delete"), false);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.startsWith(name + " | "))
                    .collect(Collectors.toList());
            Files.write(file.toPath(), updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
