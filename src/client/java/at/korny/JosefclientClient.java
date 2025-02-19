package at.korny;

import at.korny.Screens.modMenu;
import at.korny.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.entity.player.PlayerEntity;
import java.util.function.BiConsumer;
import static at.korny.utils.MemoryUsageHelper.getMemoryUsagePercent;

public class JosefclientClient implements ClientModInitializer {
	private boolean rotating = false;
	private static float rotationSpeed = 300.0f; // Degrees per tick
	private float targetYaw = 0.0f;  // The target yaw to rotate towards
	private float currentYaw = 0.0f; // Current player's body yaw
	public static final String MOD_ID = "josefclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final at.korny.utils.cpsHelper cpsHelper = new cpsHelper();

	private boolean showFPS = true;
	private boolean showCoords = true;
	private boolean showWorldInfo = true;
	private boolean showDebug = false;
	private boolean gravity = true;
	private boolean showDurability = true;
	private boolean showCPS = true;

	private String biome;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		MinecraftClient mcClient = MinecraftClient.getInstance();
		Keybinds.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			biome = BiomeHelper.getPlayerBiome();
			cpsHelper.update();
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
				showDebug = !showDebug;
				client.player.sendMessage(Text.of("Debug HUD: " + showDebug), false);
			}
			while(Keybinds.rotate.wasPressed()){
				assert client.player != null;
				rotating = !rotating;
			}
			while(Keybinds.worldInfo.wasPressed()){
				assert client.player != null;
				showWorldInfo = !showWorldInfo;
				showDurability = !showDurability;
				showCPS = !showCPS;
				client.player.sendMessage(Text.of("World Info: " + showWorldInfo), false);
			}
			while(Keybinds.F5.wasPressed()){
				assert client.player != null;
				gravity = !gravity;
				client.player.sendMessage(Text.of("Gravity disabled: " + gravity), false);
				client.player.setNoGravity(gravity);
			}
			while(Keybinds.F6.wasPressed()){
				assert client.player != null;
				client.player.sendMessage(Text.of("This feature is comming soon!"), false);
				MinecraftClient.getInstance().setScreen(new modMenu());
			}

			// If rotating is true, incrementally rotate the player smoothly
			if (rotating && client.player != null) {
				targetYaw = client.player.getYaw() + rotationSpeed;

				// Lerp the yaw for smooth rotation
				currentYaw = rotationHelper.lerp(currentYaw, targetYaw, 0.1f);  // 0.1f is the smoothness factor

				// Apply the body yaw rotation only (not affecting head yaw)
				client.player.setYaw(currentYaw);  // Update yaw (affects both body and head)
				client.player.setBodyYaw(currentYaw); // Update body yaw (only affects the body)
			}
		});

		// Render FPS overlay
		HudRenderCallback.EVENT.register(this::fpsRenderer);
		// Render coordinates overlay
		HudRenderCallback.EVENT.register(this::coordsRenderer);
		// Render debug overlay
		HudRenderCallback.EVENT.register(this::debugRenderer);
		// Render world info overlay
		HudRenderCallback.EVENT.register(this::worldRenderer);
		//Render CPS
		HudRenderCallback.EVENT.register(this::CPS);
		//Renders Durability
		HudRenderCallback.EVENT.register(this::Durability);
	}

	private void fpsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showFPS) return;
		MinecraftClient client = MinecraftClient.getInstance();
		int fps = FPSHelper.getFPS();

		if (client.player != null) {
			context.drawText(client.textRenderer, "[FPS] " + String.valueOf(fps), 10, 10, 0xFFFFFF, true);
		}
	}

	private void coordsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showCoords) return;

		MinecraftClient client = MinecraftClient.getInstance();

        assert client.player != null;
        int x = (int) Math.floor(client.player.getX());
		int y = (int) Math.floor(client.player.getY());
		int z = (int) Math.floor(client.player.getZ());


		String direction = PlayerDirectionHelper.getCardinalDirection();


		if (client.player != null) {
			context.drawText(client.textRenderer, "[X] " + String.valueOf(x), 10, 25, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "[Y] " + String.valueOf(y), 10, 35, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "[Z] " + String.valueOf(z), 10, 45, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "[Direction] " + direction, 10, 55, 0xFFFFFF, true);
		}
	}
	private void worldRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showWorldInfo) return;
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player != null) {
			context.drawText(client.textRenderer, "[Day] " + String.valueOf(DayCounter.DayCount()), 10, 90, 0xFFFFFF, true);
		}
	}
	private void CPS(DrawContext context, RenderTickCounter renderTickCounter){
		if(!showCPS) return;
		MinecraftClient client = MinecraftClient.getInstance();

		if(client.player != null){
			context.drawText(client.textRenderer, "[CPS]" + cpsHelper.getLeftCPS() + "|" + cpsHelper.getRightCPS(), 10, 100, 0xFFFFFF,true);
		}
	}
	private void Durability(DrawContext context, RenderTickCounter renderTickCounter){
		if(!showDurability) return;
		MinecraftClient client = MinecraftClient.getInstance();

		if (ItemDurability.getItemDurability(client.player) != -1) {
			if (client.player != null) {
				context.drawText(client.textRenderer, "[Durability]" + ItemDurability.getItemDurability(client.player) + "/" + ItemDurability.getItemMaxDurability(client.player), 10, 110, 0xFFFFF, true);
			}
		}
	}
	private void debugRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showDebug) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.textRenderer == null || client.player == null) return;

		PlayerEntity player = client.player;

		// Get screen width & height dynamically
		int screenWidth = context.getScaledWindowWidth();
		int screenHeight = context.getScaledWindowHeight();

		// Define base Y position and spacing
		int y = 10;
		int spacing = 15; // Space between each line

		// Helper function to center text dynamically
		BiConsumer<String, Integer> drawCenteredText = (text, yPos) -> {
			int textWidth = client.textRenderer.getWidth(text);
			int x = (screenWidth - textWidth) - 10; // Center X
			context.drawText(client.textRenderer, text, x, yPos, 0xFFFFFF, true);
		};

		// Draw debug info
		drawCenteredText.accept("DEBUG MENU", y);
		y += spacing;
		drawCenteredText.accept("[Server Version] " + VersionHelper.getVersion(), y);
		y+= spacing;
		drawCenteredText.accept("[Memory] " + getMemoryUsagePercent()+"%", y);
		y+=spacing;
		drawCenteredText.accept("[Sprinting] "+sprintStatusHelper.isSprinting(),y);
		y += spacing;
		drawCenteredText.accept("[Weather] " + String.valueOf(weatherHelper.getWeather()),y);
		y+= spacing;
		drawCenteredText.accept("[Day] " + String.valueOf(DayCounter.DayCount()),y);
		y+=spacing;
		drawCenteredText.accept("[Biome] " + String.valueOf(biome),y);
	}
}