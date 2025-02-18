package at.korny.utils;

import net.minecraft.client.MinecraftClient;

public class VersionHelper {
    public static String getVersion(){
            return MinecraftClient.getInstance().getGameVersion();
    }

}
