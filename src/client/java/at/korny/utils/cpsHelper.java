package at.korny.utils;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class cpsHelper {

    private final List<Long> leftClickTimestamps = new ArrayList<>();
    private final List<Long> rightClickTimestamps = new ArrayList<>();

    public void update() {
        long currentTime = System.currentTimeMillis();

        // Track left mouse button (LMB) clicks
        if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            leftClickTimestamps.add(currentTime);
        }

        // Track right mouse button (RMB) clicks
        if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
            rightClickTimestamps.add(currentTime);
        }

        // Remove old clicks (older than 1 second)
        leftClickTimestamps.removeIf(time -> time < currentTime - 1000);
        rightClickTimestamps.removeIf(time -> time < currentTime - 1000);
    }

    // Get the CPS for left mouse button (LMB)
    public int getLeftCPS() {
        return leftClickTimestamps.size();
    }

    // Get the CPS for right mouse button (RMB)
    public int getRightCPS() {
        return rightClickTimestamps.size();
    }
}
