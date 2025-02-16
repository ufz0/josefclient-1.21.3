package at.cloudmesh

import net.fabricmc.api.ClientModInitializer

object CloudClientClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		println("Initializing Cloud Client")
	}
}