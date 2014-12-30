package dhcoder.tool.javafx.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import static dhcoder.support.text.StringUtils.format;

/**
 * {@link TextField} validator which ensures the user can only type digits into a TextField. It is an error to
 * initialize this validator with a {@link TextField} that is not already set to a numeric value.
 *
 * Ex: {@code textExample.textProperty().addListener(new NumericTextValidator(textExample));
}
 */
public final class NumericTextValidator implements ChangeListener<String> {
    public static final String REGEX_NUMERIC = "\\d+";
    private final TextField textField;

    public NumericTextValidator(final TextField textField) {
        if (!textField.getText().matches(REGEX_NUMERIC)) {
            throw new IllegalArgumentException(
                format("Numeric text field isn't initialized to a numeric value: {0}", textField.getText()));
        }

        this.textField = textField;
    }

    @Override
    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
        final String newValue) {
        if (!newValue.matches(REGEX_NUMERIC)) {
            textField.setText(oldValue);
        }
    }
}
