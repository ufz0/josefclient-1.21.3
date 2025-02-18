package at.korny.utils;

public class rotationHelper {
    public static float lerp(float start, float end, float delta) {
        float difference = end - start;
        return start + difference * delta;  // Interpolates smoothly between start and end
    }
}
