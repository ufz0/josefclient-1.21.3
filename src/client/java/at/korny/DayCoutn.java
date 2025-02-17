package at.korny;

import net.minecraft.client.MinecraftClient;

public class DayCoutn {
    public static long  DayCount(){
        return MinecraftClient.getInstance().world.getTime()/24000;
    }
}
