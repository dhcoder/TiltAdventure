package tiltadv.tools.scene.view;

import dhcoder.libgdx.assets.Scene;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 */
public final class SceneController {
    @FXML private AnchorPane sceneGridPane;
    @FXML private ListView listSceneItems;
    @FXML private TabPane tabScenes;


    public void addScene(final Scene gameScene, final String name, final EventHandler<Event> onSceneClosed) {
        Tab tabScene = new Tab(name);
        tabScene.setOnClosed(onSceneClosed);
        tabScenes.getTabs().add(new Tab(name));
    }
}
