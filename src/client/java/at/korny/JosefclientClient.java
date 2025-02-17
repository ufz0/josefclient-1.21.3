package at.korny;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class JosefclientClient implements ClientModInitializer {
	private boolean rotating = false;
	private static float rotationSpeed = 300.0f; // Degrees per tick
	private float targetYaw = 0.0f;  // The target yaw to rotate towards
	private float currentYaw = 0.0f; // Current player's body yaw

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		MinecraftClient mcClient = MinecraftClient.getInstance();
		Keybinds.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (Keybinds.g.wasPressed()) {
				assert client.player != null;
				showFPS = !showFPS;
				client.player.sendMessage(Text.of("FPS HUD: " + showFPS), false);
			}
			while (Keybinds.h.wasPressed()) {
				assert client.player != null;
				showCoords = !showCoords;
				client.player.sendMessage(Text.of("Location HUD: " + showCoords), false);
			}
			while (Keybinds.debug.wasPressed()) {
				assert client.player != null;
				rotating = !rotating; // Toggle rotating state
			}

			// If rotating is true, incrementally rotate the player smoothly
			if (rotating && client.player != null) {
				targetYaw = client.player.getYaw() + rotationSpeed;

				// Lerp the yaw for smooth rotation
				currentYaw = lerp(currentYaw, targetYaw, 0.1f);  // 0.1f is the smoothness factor

				// Apply the body yaw rotation only (not affecting head yaw)
				client.player.setYaw(currentYaw);  // Update yaw (affects both body and head)
				client.player.setBodyYaw(currentYaw); // Update body yaw (only affects the body)

			}
		});

		// Render FPS overlay
		HudRenderCallback.EVENT.register(this::fpsRenderer);
		// Render coordinates overlay
		HudRenderCallback.EVENT.register(this::coordsRenderer);
	}

	private boolean showFPS = true;
	private boolean showCoords = true;

	private void fpsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showFPS) return;
		MinecraftClient client = MinecraftClient.getInstance();
		int fps = FPSHelper.getFPS();

		if (client.player != null) {
			context.drawText(client.textRenderer, "FPS: " + String.valueOf(fps), 10, 10, 0xFFFFFF, true);
		}
	}

	private void coordsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showCoords) return;

		MinecraftClient client = MinecraftClient.getInstance();

		int x = (int) Math.floor(client.player.getX());
		int y = (int) Math.floor(client.player.getY());
		int z = (int) Math.floor(client.player.getZ());
		if (client.player != null) {
			context.drawText(client.textRenderer, "X: " + String.valueOf(x), 10, 20, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "Y: " + String.valueOf(y), 10, 30, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "Z: " + String.valueOf(z), 10, 40, 0xFFFFFF, true);
		}
	}

	// Smooth lerp function to interpolate between two values
	private float lerp(float start, float end, float delta) {
		float difference = end - start;
		return start + difference * delta;  // Interpolates smoothly between start and end
	}
}