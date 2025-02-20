package at.korny.utils.WaypointSystem;

import at.korny.Josefclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WaypointGet {

    public static String readAndSendMessage() {
        MinecraftClient client = MinecraftClient.getInstance();
        String filename;
        if(MinecraftClient.getInstance().isInSingleplayer())
        {
            String worldName = client.getServer().getSaveProperties().getLevelName().toString();
            filename = worldName +"-waypoints.txt";
        }else{
            String serverIP = MinecraftClient.getInstance().getCurrentServerEntry().toString();
            filename = serverIP +"-waypoints.txt";
        }
        File file = new File(filename);
        String coords = "";
        if (!file.exists()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Fehler beim laden der Wegpunkte"), false);
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) { // Zeile für Zeile lesen
                coords += line + "\n";
            }
        } catch (IOException e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Fehler beim lesen der Wegpunkte"), false);
            e.printStackTrace();
        }
        return coords;
    }
}
