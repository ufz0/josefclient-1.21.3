package at.korny.utils.WaypointSystem;

import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.io.IOException;

public class WaypointSet {

    public static void WaypointSet() {

        MinecraftClient client = MinecraftClient.getInstance();
        int x = (int) client.player.getX();
        int y = (int) client.player.getY();
        int z = (int) client.player.getZ();

        String position = x + " | " + y + " | " + z;

            try {
                FileWriter FW = new FileWriter("Waypoints.txt");
                FW.write(position);
                FW.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
