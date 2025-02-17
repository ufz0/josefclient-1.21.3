package at.korny;

import net.minecraft.client.MinecraftClient;

public class Version {
    public static String version(){
        return MinecraftClient.getInstance().getGameVersion();
    }
}
