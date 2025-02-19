package at.korny.Screens;

import at.korny.JosefclientClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class modMenu extends Screen {
    private final List<ButtonWidget> buttons = new ArrayList<>();

    public modMenu() {
        super(Text.literal("Josef Client Modmenu"));
    }

    @Override
    protected void init() {
        buttons.clear();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int verticalSpacing = 10;

        // Buttons hinzufügen
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit"), button -> this.client.player.closeScreen())
                .dimensions(0, 0, buttonWidth, buttonHeight).build()));
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..."), button -> {
            JosefclientClient.showFPS = !JosefclientClient.showFPS;
            JosefclientClient.saveOptions();
        }).dimensions(0, 0, buttonWidth, buttonHeight).build()));
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..."), button -> {
            JosefclientClient.showCoords = !JosefclientClient.showCoords;
            JosefclientClient.saveOptions();
        }).dimensions(0, 0, buttonWidth, buttonHeight).build()));
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..."), button -> {
            JosefclientClient.showCPS = !JosefclientClient.showCPS;
            JosefclientClient.saveOptions();
        }).dimensions(0, 0, buttonWidth, buttonHeight).build()));
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..."), button -> {
            JosefclientClient.showDurability = !JosefclientClient.showDurability;
            JosefclientClient.saveOptions();
        }).dimensions(0, 0, buttonWidth, buttonHeight).build()));
        buttons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..."), button -> {
            JosefclientClient.showDebug = !JosefclientClient.showDebug;
            JosefclientClient.saveOptions();
        }).dimensions(0, 0, buttonWidth, buttonHeight).build()));

        // Dynamische Positionierung der Buttons
        int totalHeight = buttons.size() * buttonHeight + (buttons.size() - 1) * verticalSpacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = this.width / 2 - buttonWidth / 2;

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setX(centerX);
            buttons.get(i).setY(startY + i * (buttonHeight + verticalSpacing));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        buttons.get(1).setMessage(Text.literal("Display FPS: " + JosefclientClient.showFPS));
        buttons.get(2).setMessage(Text.literal("Display location: " + JosefclientClient.showCoords));
        buttons.get(3).setMessage(Text.literal("Display CPS: " + JosefclientClient.showCPS));
        buttons.get(4).setMessage(Text.literal("Display durability: " + JosefclientClient.showDurability));
        buttons.get(5).setMessage(Text.literal("Display debug: " + JosefclientClient.showDebug));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
