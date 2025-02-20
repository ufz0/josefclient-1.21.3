package at.korny.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Overlay {
    public final String id;
    public int x, y;
    public final int defaultX, defaultY;
    public final String sampleText; // used for hit detection (bounding box)
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
            renderer.render(context, client, this);
        }
    }

    public void resetPosition() {
        this.x = defaultX;
        this.y = defaultY;
    }
}
