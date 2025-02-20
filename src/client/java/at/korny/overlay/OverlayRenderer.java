package at.korny.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@FunctionalInterface
public interface OverlayRenderer {
    void render(DrawContext context, MinecraftClient client, Overlay overlay);
}
