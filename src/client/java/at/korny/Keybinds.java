package at.korny;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Keybinds {
    public static final String CATEGORY = "Josef / korny client";
    public static KeyBinding g;
    public static KeyBinding h;
    public static KeyBinding debug;
    public static KeyBinding rotate;
    public static KeyBinding worldInfo;
    public static KeyBinding F5;
    public static KeyBinding F6;

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
                GLFW.GLFW_KEY_F4,// Default key (Change to your preferred key)
                CATEGORY // Category (custom or use existing like "key.categories.misc")
        ));
        rotate = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.spin", // Translation key
                InputUtil.Type.KEYSYM, // Type (KEYSYM = keyboard, MOUSE = mouse button)
                GLFW.GLFW_KEY_J,// Default key (Change to your preferred key)
                CATEGORY // Category (custom or use existing like "key.categories.misc")
        ));
        F5 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.toggleGravity",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F5,
                CATEGORY
        ));
        F6 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.modmenu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                CATEGORY
        ));
    }
}
