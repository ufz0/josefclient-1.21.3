package at.korny;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static final String CATEGORY = "Josef / korny client";
    public static KeyBinding g;
    public static KeyBinding h;
    public static KeyBinding n;
    public static KeyBinding u;
    public static KeyBinding zoom;
    public static KeyBinding x;
    public static KeyBinding debug;
    public static KeyBinding rotate;
    public static KeyBinding F5;
    public static KeyBinding F6;
    public static KeyBinding F7;

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
        n = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.waypointSet", // Translation Key
                InputUtil.Type.KEYSYM, // Type(KEYSM = keyboard,Mouse = mouse button)
                GLFW.GLFW_KEY_N, // Default Key (Change to your preferred Key)
                CATEGORY // Category ( custom or use existing like "key.categories.misc")
        ));
        u = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.waypointLoad",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                CATEGORY
        ));
        zoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));
        x = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.BigAngle", // Translation key
                InputUtil.Type.KEYSYM, // Type (KEYSYM = keyboard, MOUSE = mouse button)
                GLFW.GLFW_KEY_H, // Default key (Change to your preferred key)
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
        F7 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.waypoints",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                CATEGORY
        ));
        debug = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.korny.debug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                CATEGORY
        ));
    }
}
