package at.korny;

import at.korny.Screens.modMenu;
import at.korny.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.entity.player.PlayerEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import static at.korny.utils.MemoryUsageHelper.getMemoryUsagePercent;

public class JosefclientClient implements ClientModInitializer {

	public static final String MOD_ID = "josefclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final at.korny.utils.cpsHelper cpsHelper = new cpsHelper();
	private static final File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "options.txt");
	private String biome;

	private boolean rotating = false;
	private static float rotationSpeed = 300.0f; // Degrees per tick
	private float targetYaw = 0.0f;  // The target yaw to rotate towards
	private float currentYaw = 0.0f; // Current player's body yaw
	public static boolean gravity = false;

	public static boolean showFPS = true;
	public static boolean showCoords = false;
	public static boolean showDebug = false;
	public static boolean showDurability = false;
	public static boolean showCPS = false;


	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		loadSettings();
		MinecraftClient mcClient = MinecraftClient.getInstance();
		Keybinds.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			biome = BiomeHelper.getPlayerBiome();
			cpsHelper.update();
			while (Keybinds.g.wasPressed()) {
				assert client.player != null;
				showFPS = !showFPS;
				client.player.sendMessage(Text.of("FPS HUD: " + showFPS), false);
				saveOptions();
			}
			while (Keybinds.h.wasPressed()) {
				assert client.player != null;
				showCoords = !showCoords;
				client.player.sendMessage(Text.of("Location HUD: " + showCoords), false);
				saveOptions();
			}
			while (Keybinds.debug.wasPressed()) {
				assert client.player != null;
				showDebug = !showDebug;
				client.player.sendMessage(Text.of("Debug HUD: " + showDebug), false);
				saveOptions();
			}
			while(Keybinds.rotate.wasPressed()){
				assert client.player != null;
				rotating = !rotating;
			}
			while(Keybinds.F5.wasPressed()){
				assert client.player != null;
				gravity = !gravity;
				client.player.sendMessage(Text.of("Gravity disabled: " + gravity), false);
				client.player.setNoGravity(gravity);
			}
			while(Keybinds.F6.wasPressed()){
				assert client.player != null;
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
		//Render CPS
		HudRenderCallback.EVENT.register(this::CPS);
		//Renders Durability
		HudRenderCallback.EVENT.register(this::Durability);
		//saveOptions();
	}

	private void fpsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if (!showFPS) return;
		MinecraftClient client = MinecraftClient.getInstance();
		int fps = FPSHelper.getFPS();

		if (client.player != null) {
			context.drawText(client.textRenderer, "[FPS] " + String.valueOf(fps), 10, 15, 0xFFFFFF, true);
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
	private void CPS(DrawContext context, RenderTickCounter renderTickCounter){
		if(!showCPS) return;
		MinecraftClient client = MinecraftClient.getInstance();

		if(client.player != null){
			context.drawText(client.textRenderer, "[CPS]" + cpsHelper.getLeftCPS() + "|" + cpsHelper.getRightCPS(), 10, 70, 0xFFFFFF,true);
		}
	}
	private void Durability(DrawContext context, RenderTickCounter renderTickCounter){
		if(!showDurability) return;
		MinecraftClient client = MinecraftClient.getInstance();

		if (ItemDurability.getItemDurability(client.player) != -1) {
			if (client.player != null) {
				context.drawText(client.textRenderer, "[Durability] " + ItemDurability.getItemDurability(client.player) + "/" + ItemDurability.getItemMaxDurability(client.player), 10, 80, 0xFFFFF, true);
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

	private void loadSettings() {
		if (!optionsFile.exists()) return;

		try (BufferedReader reader = new BufferedReader(new FileReader(optionsFile))) {
			String line;
			Map<String, Boolean> settings = new HashMap<>();

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("josefclient.")) {
					String[] parts = line.split("=");
					if (parts.length == 2) {
						settings.put(parts[0].trim(), Boolean.parseBoolean(parts[1].trim()));
					}
				}
			}

			showFPS = settings.getOrDefault("josefclient.showFPS", true);
			showCoords = settings.getOrDefault("josefclient.showCoords", false);
			showDebug = settings.getOrDefault("josefclient.showDebug", false);
			showDurability = settings.getOrDefault("josefclient.showDurability", false);
			showCPS = settings.getOrDefault("josefclient.showCPS", false);

		} catch (IOException e) {
			LOGGER.error("Failed to load settings!", e);
		}
	}

	public static void saveOptions() {
		File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "options.txt");

		try {
			// Alte Datei einlesen
			List<String> lines = new ArrayList<>();
			if (optionsFile.exists()) {
				lines = Files.readAllLines(optionsFile.toPath());
			}

			// Mod-Variablen als neue Zeilen hinzufügen oder ersetzen
			Map<String, String> modOptions = new HashMap<>();
			modOptions.put("josefclient.showFPS", String.valueOf(showFPS));
			modOptions.put("josefclient.showCoords", String.valueOf(showCoords));
			modOptions.put("josefclient.showDebug", String.valueOf(showDebug));
			modOptions.put("josefclient.showDurability", String.valueOf(showDurability));
			modOptions.put("josefclient.showCPS", String.valueOf(showCPS));

			// Neue Datei mit aktualisierten Werten schreiben
			List<String> updatedLines = new ArrayList<>();
			for (String line : lines) {
				String key = line.split("=")[0].trim();
				if (modOptions.containsKey(key)) {
					updatedLines.add(key + "=" + modOptions.get(key));
					modOptions.remove(key);
				} else {
					updatedLines.add(line);
				}
			}

			// Falls neue Werte noch nicht existieren, ans Ende anhängen
			for (Map.Entry<String, String> entry : modOptions.entrySet()) {
				updatedLines.add(entry.getKey() + "=" + entry.getValue());
			}

			// Datei überschreiben
			Files.write(optionsFile.toPath(), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Fehler beim Speichern der Mod-Optionen!", e);
		}
	}
}