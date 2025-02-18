package at.korny.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemDurability {
    public static int getItemDurability(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandStack(); // Holt das Item in der Haupthand

        if (!heldItem.isEmpty() && heldItem.isDamageable()) { // Prüft, ob das Item Haltbarkeit hat
            return heldItem.getMaxDamage() - heldItem.getDamage();
        }
        return -1;

        //public static int = heldItem.getMaxDamage() - heldItem.getDamage();
    }
    public static int getItemMaxDurability(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandStack();
        if (!heldItem.isEmpty() && heldItem.isDamageable()) {
            return heldItem.getMaxDamage();
        }else{
            return -1;
        }
    }

}