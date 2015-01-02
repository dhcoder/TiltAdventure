package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 */
public final class SceneController extends FxController {
    @FXML private AnchorPane sceneGridPane;
    @FXML private ListView listSceneItems;
}
