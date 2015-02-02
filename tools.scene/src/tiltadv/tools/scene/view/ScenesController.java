package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.game.model.Scene;
import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import tiltadv.tools.scene.SceneTool;

import java.util.function.Consumer;

/**
 * UI for editing a collection of scenes.
 */
public final class ScenesController extends FxController {
    @FXML private AnchorPane scenePane;
    @FXML private TabPane tabScenes;

    private Consumer<Scene> onSceneAdding;
    private Consumer<Scene> onSceneSelected;
    private Consumer<Scene> onSceneRemoved;
    private SceneController sceneController;

    /**
     * Return the number of open scenes.
     */
    public int getSceneCount() {
        return tabScenes.getTabs().size();
    }

    public Scene getActiveScene() {
        if (tabScenes.getTabs().size() == 0) {
            throw new IllegalStateException("Attempted to request a scene when there are none open");
        }

        Tab tabActive = tabScenes.getSelectionModel().getSelectedItem();
        return (Scene)tabActive.getUserData();
    }

    public void setOnSceneAdding(final Consumer<Scene> onSceneAdding) {
        this.onSceneAdding = onSceneAdding;
    }

    public void setOnSceneRemoved(final Consumer<Scene> onSceneRemoved) {
        this.onSceneRemoved = onSceneRemoved;
    }

    public void setOnSceneSelected(final Consumer<Scene> onSceneSelected) {
        this.onSceneSelected = onSceneSelected;
    }

    public void setSceneTool(final SceneTool sceneTool) {
        sceneController.setSceneTool(sceneTool);
    }

    public void addScene(final Scene gameScene, final String name) {
        Tab tabScene = new Tab(name);
        tabScene.setUserData(gameScene);

        tabScene.setOnClosed(event -> fireOnSceneClosed(gameScene));
        fireOnSceneAdded(gameScene);
        tabScenes.getTabs().add(tabScene);
        tabScenes.getSelectionModel().select(tabScene);
    }

    public void closeActiveScene() {
        if (tabScenes.getTabs().size() == 0) {
            throw new IllegalStateException("Attempted to close a scene when there are none to close");
        }

        Tab tabActive = tabScenes.getSelectionModel().getSelectedItem();
        tabScenes.getTabs().remove(tabActive);
        fireOnSceneClosed((Scene)tabActive.getUserData());
    }

    @FXML
    private void initialize() {
        sceneController = FxController.loadView(SceneController.class);
        Parent scenePaneContents = sceneController.getRoot();
        AnchorPane.setBottomAnchor(scenePaneContents, 0.0);
        AnchorPane.setTopAnchor(scenePaneContents, 0.0);
        AnchorPane.setLeftAnchor(scenePaneContents, 0.0);
        AnchorPane.setRightAnchor(scenePaneContents, 0.0);
        scenePane.getChildren().add(scenePaneContents);

        tabScenes.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                fireOnSceneSelected((Scene)newTab.getUserData());
            }
        });
    }

    private void fireOnSceneAdded(final Scene scene) {
        if (onSceneAdding != null) {
            onSceneAdding.accept(scene);
        }
    }

    private void fireOnSceneClosed(final Scene scene) {
        if (onSceneRemoved != null) {
            onSceneRemoved.accept(scene);
        }
    }

    private void fireOnSceneSelected(final Scene scene) {
        if (onSceneSelected != null) {
            onSceneSelected.accept(scene);
        }
    }
}
