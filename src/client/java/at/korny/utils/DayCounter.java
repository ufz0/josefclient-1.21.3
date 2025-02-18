package at.korny.utils;

import net.minecraft.client.MinecraftClient;

public class DayCounter {
    public static long  DayCount(){
        return MinecraftClient.getInstance().world.getTimeOfDay()/24000;
    }
}
