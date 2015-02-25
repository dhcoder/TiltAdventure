package dhcoder.tool.javafx.property;

import static dhcoder.support.text.StringUtils.format;

/**
 * Exception thrown if a {@link PropertyDefn} annotation is added to a class but doesn't match any property.
 */
public final class UnreferencedPropertyException extends RuntimeException {
    private final PropertyDefn propertyDefn;

    public UnreferencedPropertyException(final PropertyDefn propertyDefn) {
        super(format("Unreferenced property definition: {0}", propertyDefn.name()));
        this.propertyDefn = propertyDefn;
    }

    public PropertyDefn getPropertyDefinition() {
        return propertyDefn;
    }
}
