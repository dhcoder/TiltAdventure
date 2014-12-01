package dhcoder.tool.command;

/**
 * Interface for converting between keycodes and key names. This should be instantiated with a specific provider based
 * on the input system that you're using and used to initialize {@link Shortcut}
 */
public interface KeyNameProvider {
    String getName(int key);
    int getKey(String name) throws IllegalArgumentException;
}
