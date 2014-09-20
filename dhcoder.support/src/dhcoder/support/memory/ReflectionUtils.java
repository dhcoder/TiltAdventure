package dhcoder.support.memory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static dhcoder.support.text.StringUtils.format;

/**
 * A collection of utility methods that use reflection.
 */
public final class ReflectionUtils {

    /**
     * Check that all non-primitive fields in a target class are null, or throw an exception otherwise.
     * <p/>
     * NOTE: This method should only be called in development code. Heavy use of reflection causes lots of small
     * allocations to be made, so it shouldn't be released in production code.
     *
     * @throws IllegalStateException if the target class has a field set to a non-default value.
     */
    public static void assertSame(final Object reference, final Object target) {
        final Field[] fields = target.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                field.setAccessible(true);
                if (field.get(target) != field.get(reference)) {
                    format("Reset missed field {0}#{1}", target.getClass().getSimpleName(), field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                    format("Unexpected illegal access of field {0}#{1}", target.getClass().getSimpleName(),
                        field.getName()));
            }
        }
    }

    private ReflectionUtils() {}

}
