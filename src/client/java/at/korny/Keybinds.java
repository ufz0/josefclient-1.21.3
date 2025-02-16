package at.korny;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static final String CATEGORY = "Josef / korny client";
    public static KeyBinding g;
    public static KeyBinding h;
    public static KeyBinding debug;
    public static void register() {
        g = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.fpsToggle", // Translation key
                InputUtil.Type.KEYSYM, // Type (KEYSYM = keyboard, MOUSE = mouse button)
                GLFW.GLFW_KEY_G, // Default key (Change to your preferred key)
                CATEGORY // Category (custom or use existing like "key.categories.misc")
        ));
        h = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.coordinatesToggle", // Translation key
                InputUtil.Type.KEYSYM, // Type (KEYSYM = keyboard, MOUSE = mouse button)
                GLFW.GLFW_KEY_H, // Default key (Change to your preferred key)
                CATEGORY // Category (custom or use existing like "key.categories.misc")
        ));
        debug = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.debug", // Translation key
                InputUtil.Type.KEYSYM, // Type (KEYSYM = keyboard, MOUSE = mouse button)
                GLFW.GLFW_KEY_O, // Default key (Change to your preferred key)
                CATEGORY // Category (custom or use existing like "key.categories.misc")
        ));

    }
}
