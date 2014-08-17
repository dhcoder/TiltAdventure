package tiltadv.globals;

import dhcoder.support.pattern.ServiceLocator;

/**
 * Static methods for accessing all services registered in our game.
 */
public final class Services {
    private static ServiceLocator serviceLocator = new ServiceLocator();

    public static <T> void register(final Class<? extends T> classType, final T instance) {
        serviceLocator.register(classType, instance);
    }

    public static <T> T get(final Class<? extends T> classType) {
        return serviceLocator.get(classType);
    }

    private Services() {} // Disabled constructor for static class
}
