package at.korny;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.*;

public class JosefclientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("auernig")
				.executes(context -> {
					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					// For versions below 1.20, remode "() ->" directly.
					String instance = MinecraftVersion.create().getName();
					context.getSource().sendFeedback(() -> Text.literal(instance), false);

					return 1;
				})));
		HudRenderCallback.EVENT.register(this::onHudRender);
	}

	private void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		int fps = FPSHelper.getFPS();
		if (client.player != null) {
			context.drawText(client.textRenderer, String.valueOf(fps), 10, 10, 0xFFFFFF, true);
		}
	}
}