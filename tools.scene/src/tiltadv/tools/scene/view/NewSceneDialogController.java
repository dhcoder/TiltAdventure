package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.Validators;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.controlsfx.validation.ValidationSupport;
import tiltadv.tools.scene.SceneTool;

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

    private SceneTool sceneTool;

    public void onBrowseClicked(final MouseEvent clickEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(sceneTool.getAppSettings().getAssetPath());
        Platform.runLater(() -> directoryChooser.showDialog(sceneTool.getStage()));

        clickEvent.consume();
    }

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
    }

    @FXML
    private void initialize() {
        validationSupport = new ValidationSupport();

        validationSupport.registerValidator(textCols, Validators.numericValidator);
        validationSupport.registerValidator(textRows, Validators.numericValidator);
        validationSupport.registerValidator(textSceneName, Validators.asciiValidator);
    }
}
