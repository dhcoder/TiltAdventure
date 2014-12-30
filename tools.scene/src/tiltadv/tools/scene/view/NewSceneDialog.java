package tiltadv.tools.scene.view;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.utils.FxController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.File;

/**
 * Dialog to collect information when creating a new scene.
 */
public final class NewSceneDialog {
    public static final class Result {
        private String sceneName;
        private int numCols, numRows;
        private File tilesetPath;

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

        Result(final String sceneName, final int numCols, final int numRows, final File tilesetPath) {
            this.sceneName = sceneName;
            this.numCols = numCols;
            this.numRows = numRows;
            this.tilesetPath = tilesetPath;
        }
    }

    public Opt<Result> showAndWait() {
        Dialog<Result> newSceneDialog = new Dialog<>();
        NewSceneDialogController newSceneDialogController = FxController.loadView(NewSceneDialogController.class);
        newSceneDialog.getDialogPane().setContent(newSceneDialogController.getRoot());

        newSceneDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Node buttonOk = newSceneDialog.getDialogPane().lookupButton(ButtonType.OK);
        buttonOk.setDisable(true);
        Node buttonCancel = newSceneDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        Platform.runLater(buttonCancel::requestFocus);

        newSceneDialog.showAndWait();
        return Opt.withNoValue();
    }

}
