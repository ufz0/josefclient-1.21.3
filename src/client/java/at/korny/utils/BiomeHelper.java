package at.korny.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import java.util.Optional;

public class BiomeHelper {
    public static String getPlayerBiome() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) {
            return "Unknown";
        }

        BlockPos playerPos = client.player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = client.world.getBiome(playerPos);

        // Get the biome registry from the world
        Optional<Registry<Biome>> optionalRegistry = client.world.getRegistryManager().getOptional(RegistryKeys.BIOME);

        if (optionalRegistry.isPresent()) {
            Registry<Biome> biomeRegistry = optionalRegistry.get();
            Optional<RegistryKey<Biome>> biomeKey = biomeEntry.getKey();

            if (biomeKey.isPresent()) {
                Identifier biomeId = biomeKey.get().getValue();
                return biomeId.getPath(); // Get only the biome name (e.g., "plains")
            }
        }

        return "Unknown";
    }
}
