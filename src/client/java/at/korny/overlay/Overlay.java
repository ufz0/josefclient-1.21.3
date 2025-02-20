package at.korny.overlay;

import at.korny.Screens.modMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Overlay {
    public final String id;
    public int x, y;
    public final int defaultX, defaultY;
    public final String sampleText; // used for hit detection and drag area
    public boolean visible;
    public final OverlayRenderer renderer;

    public Overlay(String id, int defaultX, int defaultY, String sampleText, OverlayRenderer renderer) {
        this.id = id;
        this.x = defaultX;
        this.y = defaultY;
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.sampleText = sampleText;
        this.renderer = renderer;
        this.visible = true;
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (visible) {
            if (client.currentScreen instanceof modMenu) {
                int paddingX = 5;
                int paddingY = 3;
                int dragX = this.x - paddingX;
                int dragY = this.y - paddingY;
                int dragWidth = client.textRenderer.getWidth(sampleText) + paddingX * 2;
                int dragHeight = client.textRenderer.fontHeight + paddingY * 2;
                context.fill(dragX, dragY, dragX + dragWidth, dragY + dragHeight, 0x88000000);
            }
            renderer.render(context, client, this);
        }
    }

    public void resetPosition() {
        this.x = defaultX;
        this.y = defaultY;
    }
}
