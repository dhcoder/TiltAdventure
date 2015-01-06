package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.Validators;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import tiltadv.tools.scene.SceneTool;

import java.io.File;

import static dhcoder.support.text.StringUtils.isWhitespace;

/**
 * Contents
 */
public final class NewSceneDialogController extends FxController {
    private final FileChooser.ExtensionFilter tilesetFilter = new FileChooser.ExtensionFilter("Tileset File", "*.tst");
    public TextField textSceneName;
    public TextField textCols;
    public TextField textRows;
    public TextField textTileset;
    public Button buttonBrowseTileset;
    public ValidationSupport validationSupport;
    private SceneTool sceneTool;

    public void onBrowseClicked(final MouseEvent clickEvent) {

        FileChooser tilesetChooser = new FileChooser();
        tilesetChooser.setInitialDirectory(sceneTool.getAppSettings().getTilesetDir());
        tilesetChooser.getExtensionFilters().add(tilesetFilter);
        Platform.runLater(() -> {
            File selected = tilesetChooser.showOpenDialog(sceneTool.getStage());
            if (selected != null) {
                textTileset.setText(selected.getName());
            }
        });

        clickEvent.consume();
    }

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
    }

    public void setOkButton(final Button buttonOk) {
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            boolean isInvalid = newValue;
            buttonOk.setDisable(isInvalid);
        });
        buttonOk.setDisable(true);
    }

    @FXML
    private void initialize() {
        validationSupport = new ValidationSupport();

        validationSupport.registerValidator(textSceneName, Validators.alphaNumericValidator);
        validationSupport.registerValidator(textCols, Validators.numericValidator);
        validationSupport.registerValidator(textRows, Validators.numericValidator);
        validationSupport.registerValidator(textTileset, (Control c, String text) -> ValidationResult
            .fromErrorIf(c, "Tileset target must exist", (isWhitespace(text) || !tilesetExists(text))));

//        textTileset.setText("zelda.tst"); // For easy debug
    }

    private boolean tilesetExists(final String text) {
        return new File(sceneTool.getAppSettings().getTilesetDir(), text).exists();
    }
}
