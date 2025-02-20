package at.korny.Screens;

import at.korny.JosefclientClient;
import at.korny.Keybinds;
import at.korny.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class modMenu extends Screen {
    private final List<ButtonWidget> buttons = new ArrayList<>();

    // For dynamic dragging of overlays
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
        // Calculate startY so that toggle buttons come first, then action buttons.
        int startY = (this.height - (JosefclientClient.overlays.size() + 2) * (buttonHeight + verticalSpacing)) / 2;

        int btnIndex = 0;
        // Create a toggle button for each overlay with status indicator.
        for (Overlay overlay : JosefclientClient.overlays) {
            String status = overlay.visible ? "ON" : "OFF";
            // Build the display text with colored status
            MutableText btnText = Text.literal("Display " + overlay.id + ": ")
                    .append(Text.literal(status)
                            .setStyle(Style.EMPTY.withColor(overlay.visible ? Formatting.GREEN : Formatting.RED)));
            ButtonWidget btn = this.addDrawableChild(ButtonWidget.builder(
                    btnText,
                    button -> {
                        overlay.visible = !overlay.visible;
                        saveAndRefresh();
                    }
            ).dimensions(centerX, startY + btnIndex * (buttonHeight + verticalSpacing), buttonWidth, buttonHeight).build());
            buttons.add(btn);
            btnIndex++;
        }
        // Add a Reset Positions button with a special icon to indicate an action.
        ButtonWidget resetBtn = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("⟲ Reset Positions"),
                button -> {
                    for (Overlay overlay : JosefclientClient.overlays) {
                        overlay.resetPosition();
                    }
                    JosefclientClient.saveOptions();
                }
        ).dimensions(centerX, startY + btnIndex * (buttonHeight + verticalSpacing), buttonWidth, buttonHeight).build());
        buttons.add(resetBtn);
        btnIndex++;
        // Add an Exit button with a special icon.
        ButtonWidget exitBtn = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("✖ Exit"),
                button -> MinecraftClient.getInstance().setScreen(null)
        ).dimensions(centerX, startY + btnIndex * (buttonHeight + verticalSpacing), buttonWidth, buttonHeight).build());
        buttons.add(exitBtn);
    }

    // Helper method to update button texts and save options.
    private void saveAndRefresh() {
        for (int i = 0; i < JosefclientClient.overlays.size(); i++) {
            Overlay overlay = JosefclientClient.overlays.get(i);
            String status = overlay.visible ? "ON" : "OFF";
            MutableText btnText = Text.literal("Display " + overlay.id + ": ")
                    .append(Text.literal(status)
                            .setStyle(Style.EMPTY.withColor(overlay.visible ? Formatting.GREEN : Formatting.RED)));
            buttons.get(i).setMessage(btnText);
        }
        JosefclientClient.saveOptions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Leave background empty so game overlays remain visible.
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Iterate through overlays to see if the click is within any overlay's bounds.
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
