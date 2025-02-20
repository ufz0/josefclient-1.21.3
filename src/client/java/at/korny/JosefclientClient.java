package at.korny;

import at.korny.Screens.modMenu;
import at.korny.overlay.Overlay;
import at.korny.overlay.OverlayRenderer;
import at.korny.utils.*;
import at.korny.utils.WaypointSystem.WaypointSet;
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
import java.util.*;

import static at.korny.utils.MemoryUsageHelper.getMemoryUsagePercent;

public class JosefclientClient implements ClientModInitializer {

	public static final String MOD_ID = "josefclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final cpsHelper cpsHelper = new cpsHelper();
	private static final File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "options.txt");
	private String biome;

	private boolean rotating = false;
	private static float rotationSpeed = 300.0f; // Degrees per tick
	private float targetYaw = 0.0f;  // The target yaw to rotate towards
	private float currentYaw = 0.0f; // Current player's body yaw
	public static boolean gravity = false;

	// Instead of separate booleans and position fields, we store overlays in one list.
	public static List<Overlay> overlays = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		MinecraftClient mcClient = MinecraftClient.getInstance();
		Keybinds.register();

		// Create overlays first.
		overlays.add(new Overlay("FPS", 10, 15, "[FPS] 000", (context, client, overlay) -> {
			int fps = FPSHelper.getFPS();
			context.drawText(client.textRenderer, "[FPS] " + fps, overlay.x, overlay.y, 0xFFFFFF, true);
		}));
		overlays.add(new Overlay("Coords", 10, 25, "[X] 000", (context, client, overlay) -> {
			if (client.player != null) {
				int x = (int) Math.floor(client.player.getX());
				int y = (int) Math.floor(client.player.getY());
				int z = (int) Math.floor(client.player.getZ());
				String direction = PlayerDirectionHelper.getCardinalDirection();
				context.drawText(client.textRenderer, "[X] " + x, overlay.x, overlay.y, 0xFFFFFF, true);
				context.drawText(client.textRenderer, "[Y] " + y, overlay.x, overlay.y + 10, 0xFFFFFF, true);
				context.drawText(client.textRenderer, "[Z] " + z, overlay.x, overlay.y + 20, 0xFFFFFF, true);
				context.drawText(client.textRenderer, "[Direction] " + direction, overlay.x, overlay.y + 30, 0xFFFFFF, true);
			}
		}));
		overlays.add(new Overlay("CPS", 10, 70, "[CPS] 00|00", (context, client, overlay) -> {
			if (client.player != null) {
				context.drawText(client.textRenderer, "[CPS] " + cpsHelper.getLeftCPS() + "|" + cpsHelper.getRightCPS(), overlay.x, overlay.y, 0xFFFFFF, true);
			}
		}));
		overlays.add(new Overlay("Durability", 10, 80, "[Durability] 000/000", (context, client, overlay) -> {
			boolean inModMenu = client.currentScreen instanceof at.korny.Screens.modMenu;
			// If not in mod menu and no item is held, do not render.
			if (!inModMenu && client.player != null && client.player.getMainHandStack().isEmpty()) {
				return;
			}
			int durability;
			int maxDurability;
			if (client.player != null && !client.player.getMainHandStack().isEmpty()) {
				durability = ItemDurability.getItemDurability(client.player);
				maxDurability = ItemDurability.getItemMaxDurability(client.player);
			} else {
				durability = 0;
				maxDurability = 0;
			}
			String text;
			if (maxDurability > 0) {
				text = "[Durability] " + durability + "/" + maxDurability;
			} else {
				text = "[Durability] N/A";
			}
			int color = 0xFFFFFF;
			if (maxDurability > 0) {
				double percent = ((double) durability / maxDurability) * 100;
				color = (percent > 75) ? 0x008000 : (percent > 25 ? 0xFFFF00 : 0xFF0000);
			}
			context.drawText(client.textRenderer, text, overlay.x, overlay.y, color, true);
		}));
		overlays.add(new Overlay("Debug", 10, 10, "DEBUG MENU", (context, client, overlay) -> {
			if (client.player != null) {
				int y = overlay.y;
				int spacing = 15;
				context.drawText(client.textRenderer, "DEBUG MENU", overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Server Version] " + VersionHelper.getVersion(), overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Memory] " + getMemoryUsagePercent() + "%", overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Sprinting] " + sprintStatusHelper.isSprinting(), overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Weather] " + weatherHelper.getWeather(), overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Day] " + DayCounter.DayCount(), overlay.x, y, 0xFFFFFF, true);
				y += spacing;
				context.drawText(client.textRenderer, "[Biome] " + biome, overlay.x, y, 0xFFFFFF, true);
			}
		}));

		// Load overlay settings AFTER creation so saved positions/visibility are applied.
		loadSettings();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.player != null){
				WaypointSet.WaypointSet();
			}
			biome = BiomeHelper.getPlayerBiome();
			cpsHelper.update();

			while (Keybinds.g.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("FPS");
					client.player.sendMessage(Text.of("FPS HUD: " + getOverlay("FPS").visible), false);
					saveOptions();
				}
			}
			while (Keybinds.h.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("Coords");
					client.player.sendMessage(Text.of("Location HUD: " + getOverlay("Coords").visible), false);
					saveOptions();
				}
			}
			while (Keybinds.debug.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("Debug");
					client.player.sendMessage(Text.of("Debug HUD: " + getOverlay("Debug").visible), false);
					saveOptions();
				}
			}
			while (Keybinds.F6.wasPressed()) {
				if(client.player != null) {
					client.setScreen(new modMenu());
				}
			}
			while (Keybinds.rotate.wasPressed()) {
				if(client.player != null) {
					rotating = !rotating;
				}
			}
			while (Keybinds.F5.wasPressed()) {
				if(client.player != null) {
					gravity = !gravity;
					client.player.sendMessage(Text.of("Gravity disabled: " + gravity), false);
					client.player.setNoGravity(gravity);
				}
			}

			if (rotating && client.player != null) {
				targetYaw = client.player.getYaw() + rotationSpeed;
				currentYaw = rotationHelper.lerp(currentYaw, targetYaw, 0.1f);
				client.player.setYaw(currentYaw);
				client.player.setBodyYaw(currentYaw);
			}
		});

		// Register one HUD render callback that iterates over all overlays.
		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			for (Overlay overlay : overlays) {
				overlay.render(context, client);
			}
		});
	}


	// Utility methods for toggling and retrieving overlays by ID
	public static void toggleOverlay(String id) {
		Overlay o = getOverlay(id);
		if (o != null) {
			o.visible = !o.visible;
		}
	}
	public static Overlay getOverlay(String id) {
		for (Overlay o : overlays) {
			if (o.id.equals(id)) {
				return o;
			}
		}
		return null;
	}

	private void loadSettings() {
		if (!optionsFile.exists()) return;
		try {
			List<String> lines = Files.readAllLines(optionsFile.toPath());
			for (String line : lines) {
				if (!line.contains("=")) continue;
				String[] parts = line.split("=", 2);
				if (parts.length != 2) continue;
				String key = parts[0].trim();
				String value = parts[1].trim();
				for (Overlay overlay : overlays) {
					String prefix = "josefclient." + overlay.id + ".";
					if (key.equals(prefix + "x")) {
						try {
							overlay.x = Integer.parseInt(value);
						} catch (NumberFormatException e) {
							LOGGER.error("Error parsing {} for {}: {}", key, overlay.id, value);
						}
					} else if (key.equals(prefix + "y")) {
						try {
							overlay.y = Integer.parseInt(value);
						} catch (NumberFormatException e) {
							LOGGER.error("Error parsing {} for {}: {}", key, overlay.id, value);
						}
					} else if (key.equals(prefix + "visible")) {
						overlay.visible = Boolean.parseBoolean(value);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load settings!", e);
		}
	}

	public static void saveOptions() {
		// Build our mod options map.
		Map<String, String> modOptions = new HashMap<>();
		for (Overlay o : overlays) {
			modOptions.put("josefclient." + o.id + ".x", String.valueOf(o.x));
			modOptions.put("josefclient." + o.id + ".y", String.valueOf(o.y));
			modOptions.put("josefclient." + o.id + ".visible", String.valueOf(o.visible));
		}
		// Read existing options.
		List<String> lines = new ArrayList<>();
		if (optionsFile.exists()) {
			try {
				lines = Files.readAllLines(optionsFile.toPath());
			} catch (IOException e) {
				LOGGER.error("Error reading options file", e);
			}
		}
		// Create a merged list.
		List<String> newLines = new ArrayList<>();
		Set<String> keysFound = new HashSet<>();
		for (String line : lines) {
			boolean updated = false;
			for (Map.Entry<String, String> entry : modOptions.entrySet()) {
				String key = entry.getKey();
				if (line.startsWith(key + "=")) {
					newLines.add(key + "=" + entry.getValue());
					keysFound.add(key);
					updated = true;
					break;
				}
			}
			if (!updated) {
				newLines.add(line);
			}
		}
		// Append keys that were not already present.
		for (Map.Entry<String, String> entry : modOptions.entrySet()) {
			if (!keysFound.contains(entry.getKey())) {
				newLines.add(entry.getKey() + "=" + entry.getValue());
			}
		}
		try {
			Files.write(optionsFile.toPath(), newLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			LOGGER.info("Saved options");
		} catch (IOException e) {
			LOGGER.error("Error saving mod options!", e);
		}
	}


}
