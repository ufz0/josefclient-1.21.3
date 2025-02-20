package at.korny.utils.WaypointSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WaypointGet {

    public static void readAndSendMessage() {
        File file = new File("Waypoints.txt");

        if (!file.exists()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Fehler beim laden der Wegpunkte"), false);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) { // Zeile für Zeile lesen
                MinecraftClient.getInstance().player.sendMessage(Text.literal(line), false);
            }
        } catch (IOException e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Fehler beim lesen der Wegpunkte"), false);
            e.printStackTrace();
        }
    }
}
