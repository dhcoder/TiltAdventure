package tiltadv.tools.scene.view;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.utils.FxController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import tiltadv.tools.scene.SceneTool;

import java.io.File;
import java.util.Optional;

/**
 * Dialog to collect information when creating a new scene.
 */
public final class NewSceneDialog {

    public static final class Result {
        private String sceneName;
        private int numCols, numRows;
        private File tilesetPath;

        Result(final String sceneName, final int numCols, final int numRows, final File tilesetPath) {
            this.sceneName = sceneName;
            this.numCols = numCols;
            this.numRows = numRows;
            this.tilesetPath = tilesetPath;
        }

        public String getSceneName() {
            return sceneName;
        }

        public int getNumCols() {
            return numCols;
        }

        public int getNumRows() {
            return numRows;
        }

        public File getTilesetPath() {
            return tilesetPath;
        }
    }

    public Opt<Result> showAndWait(final SceneTool sceneTool) {
        NewSceneDialogController controller = FxController.loadView(NewSceneDialogController.class);
        controller.setSceneTool(sceneTool);

        Dialog<ButtonType> newSceneDialog = new Dialog<>();
        newSceneDialog.initOwner(sceneTool.getStage());
        newSceneDialog.setTitle("New Scene");
        newSceneDialog.setHeaderText("Initialize a new scene (values can be modified later)");
        newSceneDialog.getDialogPane().setContent(controller.getRoot());

        newSceneDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        controller.setOkButton((Button)newSceneDialog.getDialogPane().lookupButton(ButtonType.OK));

        Platform.runLater(() -> controller.textSceneName.requestFocus());
        Optional<ButtonType> innerResult = newSceneDialog.showAndWait();

        Opt<Result> result = Opt.withNoValue();
        if (innerResult.get() == ButtonType.OK) {
            result.set(new Result(controller.textSceneName.getText(), Integer.parseInt(controller.textCols.getText()),
                Integer.parseInt(controller.textRows.getText()), new File(controller.textTileset.getText())));
        }

        return result;
    }

}
