package tiltadv.tools.scene.view;

import dhcoder.libgdx.assets.Scene;
import dhcoder.tool.javafx.fxutils.FxController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 */
public final class SceneController extends FxController {
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane sceneGridPane;
    @FXML private ListView listSceneItems;
    @FXML private TabPane tabScenes;

    @Override
    public Parent getRoot() {
        return rootPane;
    }

    public void addScene(final Scene gameScene, final String name, final EventHandler<Event> onSceneClosed) {
        Tab tabScene = new Tab(name);
        tabScene.setUserData(gameScene);

        tabScene.setOnClosed(onSceneClosed);
        tabScenes.getTabs().add(new Tab(name));
    }

    public List<Scene> getScenes() {
        ArrayList<Scene> scenes = new ArrayList<>(tabScenes.getTabs().size());
        for (Tab tabScene : tabScenes.getTabs()) {
            scenes.add((Scene)tabScene.getUserData());
        }
        return scenes;

    }
}
