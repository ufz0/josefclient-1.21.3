package at.korny;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.logging.Logger;

import static net.minecraft.server.command.CommandManager.*;

public class JosefclientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		MinecraftClient mcClient = MinecraftClient.getInstance();

		Keybinds.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while(Keybinds.myKey.wasPressed()){
				assert client.player != null;
				showHud = !showHud;
				client.player.sendMessage(Text.of("HUD:"+showHud), false);
			}
		});
		
		// Render FPS overlay
		HudRenderCallback.EVENT.register(this::fpsRenderer);
		// Render coordinates overlay
		HudRenderCallback.EVENT.register(this::coordsRenderer);
	}


	private boolean showHud = true;
	private void fpsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if(!showHud) return;
		MinecraftClient client = MinecraftClient.getInstance();
		int fps = FPSHelper.getFPS();

		if (client.player != null) {
			context.drawText(client.textRenderer, "FPS: "+String.valueOf(fps), 10, 10, 0xFFFFFF, true);
			}
	}
	private void coordsRenderer(DrawContext context, RenderTickCounter renderTickCounter) {
		if(!showHud) return;

		MinecraftClient client = MinecraftClient.getInstance();

		int x = (int) Math.floor(client.player.getX());
		int y = (int) Math.floor(client.player.getY());
		int z = (int) Math.floor(client.player.getZ());
		if (client.player != null) {
			context.drawText(client.textRenderer, "X: "+String.valueOf(x), 10, 20, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "Y: "+String.valueOf(y), 10, 30, 0xFFFFFF, true);
			context.drawText(client.textRenderer, "Z: "+String.valueOf(z), 10, 40, 0xFFFFFF, true);
		}

	}

}