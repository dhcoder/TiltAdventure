package dhcoder.tool.javafx.utils;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.Validator;

/**
 * A useful list of {@link Validator} instances.
 */
public final class Validators {
    public static final Validator<String> numericValidator =
        Validator.createRegexValidator("Value must be numerical", "\\d+", Severity.ERROR);

    public static final Validator<String> asciiValidator = Validator
        .createRegexValidator("Value can only have letters (no spaces)", "[a-zA-Z]+",
            Severity.ERROR);

    public static final Validator<String> alphaNumericValidator = Validator
        .createRegexValidator("Value can only have letters and numbers (no spaces)", "[a-zA-Z][a-zA-Z0-9]*",
            Severity.ERROR);

}
