package at.korny.utils.WaypointSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.io.FileWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WaypointSet{


        // Taste registrieren (Standard: "N")

        // Event registrieren



    public static void saveWaypoint() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = (int) client.player.getX();
        int y = (int) client.player.getY();
        int z = (int) client.player.getZ();

        String position = x + " | " + y + " | " + z;

        try {
            FileWriter writer = new FileWriter("Waypoints.txt");
            writer.write(position);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
