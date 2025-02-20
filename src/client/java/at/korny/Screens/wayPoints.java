package at.korny.Screens;

import at.korny.utils.WaypointSystem.WaypointGet;
import at.korny.utils.WaypointSystem.WaypointSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class wayPoints extends Screen {
    private TextFieldWidget nameField;
    private final List<ButtonWidget> waypointDeleteButtons = new ArrayList<>();

    public wayPoints() {
        super(Text.literal("Waypoints"));
    }

    @Override
    protected void init() {
        super.init();

        // Text field for entering the waypoint name.
        nameField = new TextFieldWidget(textRenderer, this.width / 2 - 100, this.height - 120, 200, 20, Text.literal("Waypoint Name"));
        nameField.setText("");
        addDrawableChild(nameField);

        // "Create Waypoint" button: saves a waypoint with the name from the text field.
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Create Waypoint"), button -> {
                            String name = nameField.getText().trim();
                            if (name.isEmpty()) {
                                name = "Unnamed";
                            }
                            WaypointSet.saveWaypoint(name);
                            refreshWaypointButtons();
                        })
                        .dimensions(this.width / 2 - 100, this.height - 90, 200, 20)
                        .build()
        );

        // "Close" button to close the screen.
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                        .dimensions(this.width / 2 - 50, this.height - 50, 100, 20)
                        .build()
        );

        // Initialize delete buttons for existing waypoints.
        refreshWaypointButtons();
    }

    /**
     * Refreshes the list of delete buttons corresponding to each displayed waypoint.
     * Each delete button is placed next to its waypoint line in the white box.
     */
    private void refreshWaypointButtons() {
        // Remove old delete buttons using the public removeSelectableChild method.
        for (ButtonWidget btn : waypointDeleteButtons) {
            this.remove(btn);
        }
        waypointDeleteButtons.clear();

        // Retrieve current waypoints.
        String coords = WaypointGet.readAndSendMessage();
        if (coords.isEmpty()) return;
        String[] lines = coords.split("\n");

        int lineHeight = textRenderer.fontHeight + 2;
        int boxWidth = 200;
        int boxPadding = 10;
        int dynamicBoxHeight = boxPadding * 2 + lines.length * lineHeight;
        int left = (this.width - boxWidth) / 2;
        int top = (this.height - dynamicBoxHeight) / 2;

        // For each waypoint line, add a small delete ("X") button.
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            // Expecting the format: "name | x | y | z"
            String[] parts = line.split(" \\| ");
            if (parts.length < 4) continue;
            String waypointName = parts[0];
            int buttonX = left + boxWidth - 15;
            int buttonY = top + boxPadding + i * lineHeight;
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("X"), button -> {
                        WaypointSet.deleteWaypoint(waypointName);
                        refreshWaypointButtons();
                    })
                    .dimensions(buttonX, buttonY, 10, lineHeight)
                    .build();
            waypointDeleteButtons.add(deleteButton);
            addDrawableChild(deleteButton);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the transparent background.
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Retrieve waypoints and split into lines.
        String coords = WaypointGet.readAndSendMessage();
        String[] lines = coords.split("\n");
        int lineHeight = textRenderer.fontHeight + 2;
        int boxWidth = 200;
        int boxPadding = 10;
        int dynamicBoxHeight = boxPadding * 2 + lines.length * lineHeight;
        int left = (this.width - boxWidth) / 2;
        int top = (this.height - dynamicBoxHeight) / 2;

        // Draw the centered white box.
        context.fill(left, top, left + boxWidth, top + dynamicBoxHeight, 0xFFFFFFFF);

        // Draw each waypoint text (the delete icon is drawn via its own button).
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            context.drawText(textRenderer, line, left + 10, top + boxPadding + i * lineHeight, 0x000000, false);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Keep background transparent so that game overlays remain visible.
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
