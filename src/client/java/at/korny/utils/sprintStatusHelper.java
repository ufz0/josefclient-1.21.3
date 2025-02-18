package at.korny.utils;

import net.minecraft.client.MinecraftClient;

public class sprintStatusHelper {
    public static boolean isSprinting() {
        return MinecraftClient.getInstance().player.isSprinting();

    }
}
