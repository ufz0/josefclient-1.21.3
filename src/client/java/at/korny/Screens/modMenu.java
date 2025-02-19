package at.korny.Screens;

import at.korny.JosefclientClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class modMenu extends Screen {
    private ButtonWidget fpsButton;
    private ButtonWidget coordinatesToggle;

    public modMenu() {
        super(Text.literal("Josef Client Modmenu"));
    }

    @Override
    protected void init() {

        int buttonHeight = 20;
        int verticalSpacing = 10;
        int totalButtonHeight = 3 * buttonHeight + 2 * verticalSpacing;


        int startY = (this.height - totalButtonHeight) / 2;

        // Exit button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit"), button -> {
            this.client.player.closeScreen();
        }).dimensions(this.width / 2 - 60, startY, 120, buttonHeight).build()); // Center horizontally and use calculated Y

        // FPS toggle
        fpsButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..." + at.korny.JosefclientClient.showFPS), button -> {
            at.korny.JosefclientClient.showFPS = !at.korny.JosefclientClient.showFPS;
        }).dimensions(this.width / 2 - 60, startY + buttonHeight + verticalSpacing, 120, buttonHeight).build()); // Adjust Y position

        // Coordinate Toggle
        coordinatesToggle = this.addDrawableChild(ButtonWidget.builder(Text.literal("Loading..." + JosefclientClient.showCoords), button -> {
            at.korny.JosefclientClient.showCoords = !at.korny.JosefclientClient.showCoords;
        }).dimensions(this.width / 2 - 60, startY + 2 * (buttonHeight + verticalSpacing), 120, buttonHeight).build()); // Adjust Y position
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        super.render(context, mouseX, mouseY, delta);
        context.draw();
        //super.renderBackground(context, mouseX, mouseY, delta);
        //super.clearChildren();
        super.clearAndInit();

        fpsButton.setMessage(Text.literal("Display FPS: " + JosefclientClient.showFPS));
        coordinatesToggle.setMessage(Text.literal("Display location: " + JosefclientClient.showCoords));
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

}
