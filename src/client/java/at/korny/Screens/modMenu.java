package at.korny.Screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class modMenu extends Screen {
    public modMenu() {
        super(Text.literal("My Custom Screen"));
    }
    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit"), button -> {
            // Action when button is clicked
            this.client.player.closeScreen();
        }).dimensions(10, 20, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close Game"), button -> {
            this.client.close();
        }).dimensions(10, 50, 100, 20).build());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}