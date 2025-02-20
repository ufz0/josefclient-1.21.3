package at.korny.Screens;

import at.korny.JosefclientClient;
import at.korny.Keybinds;
import at.korny.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class modMenu extends Screen {
    private final List<ButtonWidget> buttons = new ArrayList<>();

    // For dynamic dragging
    private Overlay draggingOverlay = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public modMenu() {
        super(Text.literal("Josef Client Modmenu"));
    }

    @Override
    protected void init() {
        buttons.clear();
        int buttonWidth = 150;
        int buttonHeight = 20;
        int verticalSpacing = 10;
        int centerX = this.width / 2 - buttonWidth / 2;
        int startY = (this.height - (JosefclientClient.overlays.size() + 2) * (buttonHeight + verticalSpacing)) / 2;

        int btnIndex = 0;
        // Create a toggle button for each overlay
        for (Overlay overlay : JosefclientClient.overlays) {
            ButtonWidget btn = this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Display " + overlay.id + ": " + overlay.visible),
                    button -> {
                        overlay.visible = !overlay.visible;
                        saveAndRefresh();
                    }
            ).dimensions(centerX, startY + btnIndex * (buttonHeight + verticalSpacing), buttonWidth, buttonHeight).build());
            buttons.add(btn);
            btnIndex++;
        }
        // Add a Reset Positions button
        ButtonWidget resetBtn = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset Positions"),
                button -> {
                    for (Overlay overlay : JosefclientClient.overlays) {
                        overlay.resetPosition();
                    }
                    JosefclientClient.saveOptions();
                }
        ).dimensions(centerX, startY + btnIndex * (buttonHeight + verticalSpacing), buttonWidth, buttonHeight).build());
        buttons.add(resetBtn);
    }

    // Helper to update button texts and save options
    private void saveAndRefresh() {
        for (int i = 0; i < JosefclientClient.overlays.size(); i++) {
            Overlay overlay = JosefclientClient.overlays.get(i);
            buttons.get(i).setMessage(Text.literal("Display " + overlay.id + ": " + overlay.visible));
        }
        JosefclientClient.saveOptions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // No background to see game overlays
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Single dragging system for all overlays

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Iterate through overlays to see if the click is in any overlay's bounds.
        for (Overlay overlay : JosefclientClient.overlays) {
            int overlayX = overlay.x;
            int overlayY = overlay.y;
            String sample = overlay.sampleText;
            int width = client.textRenderer.getWidth(sample);
            int height = client.textRenderer.fontHeight;
            if (mouseX >= overlayX && mouseX <= overlayX + width &&
                    mouseY >= overlayY && mouseY <= overlayY + height) {
                draggingOverlay = overlay;
                dragOffsetX = (int)(mouseX - overlayX);
                dragOffsetY = (int)(mouseY - overlayY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingOverlay != null) {
            draggingOverlay.x = (int)(mouseX - dragOffsetX);
            draggingOverlay.y = (int)(mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingOverlay != null) {
            draggingOverlay = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
