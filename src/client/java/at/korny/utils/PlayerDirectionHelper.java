package at.korny.utils;

import net.minecraft.client.MinecraftClient;

public class PlayerDirectionHelper {
    public static String getCardinalDirection() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            return "Unknown";
        }

        float yaw = client.player.getYaw();

        // Normalize yaw to 0-360
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 315 || yaw < 45) {
            return "South";
        } else if (yaw >= 45 && yaw < 135) {
            return "West";
        } else if (yaw >= 135 && yaw < 225) {
            return "North";
        } else {
            return "East";
        }
    }
}

