package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.validator.NumericTextValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Contents
 */
public final class NewSceneDialogController extends FxController {
    public TextField textSceneName;
    public TextField textCols;
    public TextField textRows;
    public TextField textTileset;
    public Button buttonBrowseTileset;

    @FXML private void initialize() {
        textCols.textProperty().addListener(new NumericTextValidator(textCols));
        textRows.textProperty().addListener(new NumericTextValidator(textRows));
    }
}
