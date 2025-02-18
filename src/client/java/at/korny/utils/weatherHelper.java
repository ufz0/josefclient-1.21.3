package at.korny.utils;

import net.minecraft.client.MinecraftClient;

public class weatherHelper {
    public static String getWeather(){
        if(MinecraftClient.getInstance().world.isRaining()){
            return "raining";
        }else if (MinecraftClient.getInstance().world.isThundering() && MinecraftClient.getInstance().world.isRaining()){
            return "rain & thunder ";
        }else if(MinecraftClient.getInstance().world.isThundering()){
            return "thunder";
        }else {
            return "clear";
        }
    }
}
