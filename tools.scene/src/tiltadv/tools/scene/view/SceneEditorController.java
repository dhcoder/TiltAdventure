package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.control.TileCanvas;
import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import tiltadv.tools.scene.AppSettings;

public final class SceneEditorController extends FxController {
    public final TileCanvas sceneCanvas = new TileCanvas();
    public final TileCanvas tilesetCanvas = new TileCanvas();

    @FXML private ScrollPane scenePane;
    @FXML private ScrollPane tilesetPane;

    @FXML private void initialize() {
        scenePane.setContent(sceneCanvas);
        tilesetPane.setContent(tilesetCanvas);

        sceneCanvas.setZoomFactor(AppSettings.ZOOM_FACTOR);
        tilesetCanvas.setZoomFactor(AppSettings.ZOOM_FACTOR);
    }
}
