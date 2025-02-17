package at.korny;

import net.minecraft.client.MinecraftClient;

public class DayCoutn {
    public static long  DayCount(){
        return MinecraftClient.getInstance().world.getTimeOfDay()/24000;
    }
}
