package at.korny;

import at.korny.Screens.modMenu;
import at.korny.overlay.Overlay;
import at.korny.overlay.OverlayRenderer;
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
			Map<String, String> settings = new HashMap<>();
			// Parse all mod options lines.
			for (String line : lines) {
				if (line.startsWith("josefclient.")) {
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						settings.put(parts[0].trim(), parts[1].trim());
					}
				}
			}
			for (Overlay overlay : overlays) {
				String xKey = "josefclient." + overlay.id + ".x";
				String yKey = "josefclient." + overlay.id + ".y";
				String visKey = "josefclient." + overlay.id + ".visible";
				if (settings.containsKey(xKey)) {
					try {
						overlay.x = Integer.parseInt(settings.get(xKey));
					} catch (NumberFormatException e) {
						LOGGER.error("Error parsing x for overlay {}: {}", overlay.id, settings.get(xKey));
					}
				}
				if (settings.containsKey(yKey)) {
					try {
						overlay.y = Integer.parseInt(settings.get(yKey));
					} catch (NumberFormatException e) {
						LOGGER.error("Error parsing y for overlay {}: {}", overlay.id, settings.get(yKey));
					}
				}
				if (settings.containsKey(visKey)) {
					overlay.visible = Boolean.parseBoolean(settings.get(visKey));
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load settings!", e);
		}
	}

	public static void saveOptions() {
		File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "options.txt");
		try {
			List<String> lines = new ArrayList<>();
			// Overwrite with our mod options.
			for (Overlay o : overlays) {
				lines.add("josefclient." + o.id + ".x=" + o.x);
				lines.add("josefclient." + o.id + ".y=" + o.y);
				lines.add("josefclient." + o.id + ".visible=" + o.visible);
			}
			Files.write(optionsFile.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			LOGGER.info("Saved options");
		} catch (IOException e) {
			LOGGER.error("Error saving mod options!", e);
		}
	}

}
