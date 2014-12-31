package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.Validators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;

/**
 * Contents
 */
public final class NewSceneDialogController extends FxController {
    public TextField textSceneName;
    public TextField textCols;
    public TextField textRows;
    public TextField textTileset;
    public Button buttonBrowseTileset;
    public ValidationSupport validationSupport;

    @FXML
    private void initialize() {
        validationSupport = new ValidationSupport();

        validationSupport.registerValidator(textCols, Validators.numericValidator);
        validationSupport.registerValidator(textRows, Validators.numericValidator);
        validationSupport.registerValidator(textSceneName, Validators.asciiValidator);
    }
}
