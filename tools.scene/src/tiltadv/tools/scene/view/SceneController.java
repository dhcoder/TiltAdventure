package tiltadv.tools.scene.view;

import dhcoder.tool.game.model.Scene;
import dhcoder.tool.game.view.TileGridCell;
import dhcoder.tool.javafx.utils.FxController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import org.controlsfx.control.GridView;
import tiltadv.tools.scene.SceneTool;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 */
public final class SceneController extends FxController {
    @FXML private AnchorPane sceneGridPane;
    @FXML private ListView listSceneItems;
    private SceneTool sceneTool;

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
    }

    public void setToScene(final Scene gameScene) {
        ObservableList<ImageView> tiles = FXCollections.observableArrayList();
        GridView<ImageView> test = new GridView<>(tiles);
        test.setCellFactory(param -> new TileGridCell());

        Image tilesetImage = gameScene.getTileset().getImage();

        for (int i = 0; i < 5; i++) {
            ImageView testTile = new ImageView(tilesetImage);
            testTile.setViewport(
                new Rectangle2D(0, 0, gameScene.getTileset().getTileWidth(), gameScene.getTileset().getTileHeight()));
            tiles.add(testTile);
        }

        PopupWindow popup = new PopupWindow() {

        };

        popup.getScene().setRoot(test);
        popup.setWidth(300);
        popup.setHeight(300);
        test.setPrefWidth(300);
        test.setPrefHeight(300);
        popup.show(sceneTool.getStage());
    }
}
