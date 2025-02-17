package at.korny;

import net.minecraft.client.MinecraftClient;

public class Version {
    public static String getVersion(){
            return MinecraftClient.getInstance().getGameVersion();
    }

}
