package tiltadv.tools.scene.view;

import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import tiltadv.tools.scene.model.GameScene;

import java.util.function.Consumer;

/**
 * UI for editing a collection of scenes.
 */
public final class ScenesController extends FxController {
    @FXML private AnchorPane scenePane;
    @FXML private TabPane tabScenes;

    private Consumer<GameScene> onSceneAdding;
    private Consumer<GameScene> onSceneSelected;
    private Consumer<GameScene> onSceneRemoved;

    /**
     * Return the number of open scenes.
     */
    public int getSceneCount() {
        return tabScenes.getTabs().size();
    }

    public GameScene getActiveScene() {
        if (tabScenes.getTabs().size() == 0) {
            throw new IllegalStateException("Attempted to request a scene when there are none open");
        }

        Tab tabActive = tabScenes.getSelectionModel().getSelectedItem();
        return (GameScene)tabActive.getUserData();
    }

    public void setOnSceneAdding(final Consumer<GameScene> onSceneAdding) {
        this.onSceneAdding = onSceneAdding;
    }

    public void setOnSceneRemoved(final Consumer<GameScene> onSceneRemoved) {
        this.onSceneRemoved = onSceneRemoved;
    }

    public void setOnSceneSelected(final Consumer<GameScene> onSceneSelected) {
        this.onSceneSelected = onSceneSelected;
    }

    public void addScene(final GameScene gameScene, final String name) {
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
        fireOnSceneClosed((GameScene)tabActive.getUserData());
    }

    @FXML
    private void initialize() {
        Parent scenePaneContents = FxController.loadView(SceneController.class).getRoot();
        AnchorPane.setBottomAnchor(scenePaneContents, 0.0);
        AnchorPane.setTopAnchor(scenePaneContents, 0.0);
        AnchorPane.setLeftAnchor(scenePaneContents, 0.0);
        AnchorPane.setRightAnchor(scenePaneContents, 0.0);
        scenePane.getChildren().add(scenePaneContents);

        tabScenes.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                fireOnSceneSelected((GameScene)newTab.getUserData());
            }
        });
    }

    private void fireOnSceneAdded(final GameScene scene) {
        if (onSceneAdding != null) {
            onSceneAdding.accept(scene);
        }
    }

    private void fireOnSceneClosed(final GameScene scene) {
        if (onSceneRemoved != null) {
            onSceneRemoved.accept(scene);
        }
    }

    private void fireOnSceneSelected(final GameScene scene) {
        if (onSceneSelected != null) {
            onSceneSelected.accept(scene);
        }
    }
}
