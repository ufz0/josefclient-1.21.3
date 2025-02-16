package at.korny;

import net.fabricmc.api.ClientModInitializer;

public class JosefclientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		for(int i = 0; i < 100; i++)
		{
			System.out.println("Josefclient " + i); //josef
		}

		while(true)
		{
			int kornyishorny = 0;
			kornyishorny++;
		}
	}
}