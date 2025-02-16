package at.korny;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.*;

public class JosefclientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("foo")
				.executes(context -> {
					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					// For versions below 1.20, remode "() ->" directly.
					context.getSource().sendFeedback(() -> Text.literal("Called /foo"), false);

					return 1;
				})));
	}
}