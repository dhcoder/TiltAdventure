package dhcoder.support.memory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static dhcoder.support.text.StringUtils.format;

/**
 * A collection of utility methods that use reflection.
 */
public final class ReflectionUtils {

    /**
     * Use reflection to check that all fields in the two classes being compared have the same value.
     * <p/>
     * NOTE: This method should only be called in development code. Heavy use of reflection causes lots of small
     * allocations to be made, so it shouldn't be released in production code.
     *
     * @throws IllegalStateException if the target class has a field set to a non-default value.
     */
    public static <T> void assertSame(final T obj1, final T obj2) {
        final Field[] fields = obj1.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                field.setAccessible(true);
                final Object value1 = field.get(obj1);
                final Object value2 = field.get(obj2);
                if (!Objects.equals(value1, value2)) {
                    throw new IllegalStateException(
                        format("Field {0}#{1} has value {2}, expected {3}", obj1.getClass().getSimpleName(),
                            field.getName(), value1, value2));
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                    format("Unexpected illegal access of field {0}#{1}", obj1.getClass().getSimpleName(),
                        field.getName()));
            }
        }
    }

    private ReflectionUtils() {}

}
