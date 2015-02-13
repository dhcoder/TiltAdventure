package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.control.TileCanvas;
import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public final class SceneEditorController extends FxController {
    public final TileCanvas tilesetCanvas = new TileCanvas();
    public ToggleGroup zoomGroup;
    public ScrollPane contentPane;
    public ColorPicker colorPicker;

    @FXML
    private void initialize() {
        contentPane.setContent(tilesetCanvas);

        tilesetCanvas.setZoomFactor(getZoomFactor(zoomGroup.getSelectedToggle()));
        zoomGroup.selectedToggleProperty().addListener((observable) -> {
            Toggle toggle = zoomGroup.getSelectedToggle();
            tilesetCanvas.setZoomFactor(getZoomFactor(toggle));
        });

        colorPicker.setValue(tilesetCanvas.getBackgroundColor());
        tilesetCanvas.backgroundColorProperty().bindBidirectional(colorPicker.valueProperty());
    }

    private int getZoomFactor(final Toggle toggle) {
        // UserData specified in FXML
        return Integer.parseInt((String)toggle.getUserData());
    }
}
