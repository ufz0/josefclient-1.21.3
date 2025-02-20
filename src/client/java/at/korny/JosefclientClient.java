package at.korny;

import at.korny.Screens.modMenu;
import at.korny.Screens.wayPoints;
import at.korny.overlay.Overlay;
import at.korny.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static at.korny.actions.BigAngle.ANTI_ZOOM_FOV;
import static at.korny.utils.MemoryUsageHelper.getMemoryUsagePercent;
import static at.korny.utils.WaypointSystem.WaypointGet.readAndSendMessage;
import static at.korny.utils.WaypointSystem.WaypointSet.saveWaypoint;
import static at.korny.actions.ZoomHelper.ZOOM_FOV;
import static at.korny.actions.ZoomHelper.originalFov;

public class JosefclientClient implements ClientModInitializer {

	public static final String MOD_ID = "josefclient";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final cpsHelper cpsHelper = new cpsHelper();
	private String biome;

	private boolean rotating = false;
	private static float rotationSpeed = 300.0f; // Degrees per tick
	private float targetYaw = 0.0f;  // The target yaw to rotate towards
	private float currentYaw = 0.0f; // Current player's body yaw
	public static boolean gravity = false;
	public static boolean isCPressed = false;

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
				int color = 0xFFFFFF;
				if (maxDurability > 0) {
					double percent = ((double) durability / maxDurability) * 100;
					color = (percent > 75) ? 0x008000 : (percent > 25 ? 0xFFFF00 : 0xFF0000);
				}
				context.drawText(client.textRenderer, text, overlay.x, overlay.y, color, true);
			}
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
		options.loadSettings();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			/*if(mcClient.isInSingleplayer() && mcClient.world != null || mcClient.getCurrentServerEntry() != null){
				String filename;
				if(MinecraftClient.getInstance().isInSingleplayer())
				{
					String worldName = mcClient.getServer().getSaveProperties().getLevelName().toString();
					filename = worldName +"-waypoints.txt";
				}else{
					String serverIP = mcClient.getCurrentServerEntry().toString();
					filename = serverIP +"-waypoints.txt";
				}
				File file = new File(filename);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}

			 */

			biome = BiomeHelper.getPlayerBiome();
			cpsHelper.update();

			while (Keybinds.n.wasPressed()) {
				saveWaypoint("newest");
				MinecraftClient.getInstance().player.sendMessage(Text.literal("Waypoint saved!"), true);

			}

			while (Keybinds.u.wasPressed()) {
				readAndSendMessage(); // Wegpunkte laden, wenn Taste gedrückt wird
			}

			if (Keybinds.c.isPressed()) { // Prüft, ob "C" gedrückt wird
				if (originalFov == -1) { // Falls das ursprüngliche FOV nicht gespeichert wurde
					originalFov = client.options.getFov().getValue();
				}
				client.options.getFov().setValue( ZOOM_FOV); // Setze Zoom-FOV
			} else { // Falls die Taste losgelassen wird
				if (originalFov != -1) { // Falls FOV gespeichert wurde
					client.options.getFov().setValue( originalFov); // Ursprüngliches FOV wiederherstellen
					originalFov = -1; // Reset
				}
			}

			if (Keybinds.x.isPressed()) { // Prüft, ob "X" gedrückt wird
				if (originalFov == -1) { // Falls das ursprüngliche FOV nicht gespeichert wurde
					originalFov = client.options.getFov().getValue();
				}
				client.options.getFov().setValue( ANTI_ZOOM_FOV); // Setze Zoom-FOV
			} else { // Falls die Taste losgelassen wird
				if (originalFov != -1) { // Falls FOV gespeichert wurde
					client.options.getFov().setValue( originalFov); // Ursprüngliches FOV wiederherstellen
					originalFov = -1; // Reset
				}
			}




			while (Keybinds.g.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("FPS");
					client.player.sendMessage(Text.of("FPS HUD: " + (getOverlay("FPS").visible ? "ON":"OFF")), true);
					options.saveOptions();
				}
			}
			while (Keybinds.h.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("Coords");
					client.player.sendMessage(Text.of("Location HUD: " + (getOverlay("Coords").visible ? "ON":"OFF")), true);
					options.saveOptions();
				}
			}
			while (Keybinds.debug.wasPressed()) {
				if(client.player != null) {
					toggleOverlay("Debug");
					client.player.sendMessage(Text.of("Debug HUD: " + (getOverlay("Debug").visible ? "ON":"OFF")), true);
					options.saveOptions();
				}
			}
			while (Keybinds.F6.wasPressed()) {
				if(client.player != null) {
					client.setScreen(new modMenu());
				}
			}
			while(Keybinds.F7.wasPressed()){
				if(client.player != null) {
					client.setScreen(new wayPoints());
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
					client.player.sendMessage(Text.of("Gravity: " + (gravity ? "OFF":"ON")), true);
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
}
