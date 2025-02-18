package at.korny.utils;
import net.minecraft.client.MinecraftClient;
public class FPSHelper {
    public static int getFPS() {
        return MinecraftClient.getInstance().getCurrentFps();
    }
}

