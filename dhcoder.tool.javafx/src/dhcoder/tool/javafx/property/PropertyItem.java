package dhcoder.tool.javafx.property;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * A class which wraps property methods and is meant for use in a {@link PropertySheet}
 */
public final class PropertyItem implements PropertySheet.Item {

    private static void verifyGetMethod(final Method getMethod) {
        if (getMethod.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException("Property get method must return a type");
        }

        if (getMethod.getParameterCount() > 0) {
            throw new IllegalArgumentException("Property get method must not take any parameters");
        }
    }

    private static void verifySetMethod(final Method setMethod, final Method getMethod) {
        if (setMethod.getParameterCount() != 1) {
            throw new IllegalArgumentException("Property set method must only take a single parameter");
        }

        if (setMethod.getParameterTypes()[0] != getMethod.getReturnType()) {
            throw new IllegalArgumentException("Property set method type should match get method");
        }
    }

    private final Object target;
    private final String name;
    private final Method getMethod;
    private String category = "";
    private Optional<Method> setMethod = Optional.empty();
    private String description = "";
    private Optional<Class<? extends PropertyEditor<?>>> editorClass = Optional.empty();

    public PropertyItem(final Object target, final String name, final Method getMethod) {
        verifyGetMethod(getMethod);
        this.target = target;
        this.name = name;
        this.getMethod = getMethod;
    }

    public PropertyItem setSetMethod(final Method setMethod) {
        verifySetMethod(setMethod, getMethod);

        this.setMethod = Optional.of(setMethod);
        return this;
    }

    @Override public Class<?> getType() {
        return getMethod.getReturnType();
    }

    @Override public String getCategory() {
        return category;
    }

    public PropertyItem setCategory(final String category) {
        this.category = category;
        return this;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String getDescription() {
        return description;
    }

    public PropertyItem setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override public Object getValue() {
        try {
            return getMethod.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void setValue(final Object value) {
        try {
            setMethod.get().invoke(target, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return editorClass;
    }

    public PropertyItem setPropertyEditorClass(Class<? extends PropertyEditor<?>> editorClass) {
        this.editorClass = Optional.of(editorClass);
        return this;
    }

    @Override public boolean isEditable() {
        return setMethod.isPresent();
    }
}
