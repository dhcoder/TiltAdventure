package dhcoder.tool.javafx.property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional meta-data about a property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertyMeta {
    String name() default "";
    String displayName() default "";
    String description() default "";
    boolean exclude() default false;
}
