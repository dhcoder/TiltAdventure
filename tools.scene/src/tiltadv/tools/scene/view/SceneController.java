package tiltadv.tools.scene.view;

import com.google.common.eventbus.Subscribe;
import dhcoder.tool.javafx.control.TileCanvas;
import dhcoder.tool.javafx.game.model.Scene;
import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.PaneUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import tiltadv.tools.scene.SceneContext;
import tiltadv.tools.scene.SceneTool;
import tiltadv.tools.scene.events.ContextChangedEventArgs;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 *
 * Note that we actually re-use the same scene UI for all scenes - that is, we instantiate one set of controls and
 * toggle its contents depending on the active scene context.
 */
public final class SceneController extends FxController {
    @FXML private ScrollPane sceneGridPane;
    @FXML private ListView listSceneItems;
    @FXML private AnchorPane propertySheetPane;

    private SceneTool sceneTool;
    private TileCanvas tileCanvas;
    private PropertySheet propertySheet;

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
        sceneTool.getEventBus().register(this);
    }

    @Subscribe
    public void onSceneContextChanged(final ContextChangedEventArgs args) {
        if (!args.getContextOpt().hasValue()) {
            sceneTool.getTilesetWindow().clear();
            return;
        }

        SceneContext context = args.getContextOpt().getValue();
        Scene gameScene = context.getScene();

        sceneTool.getTilesetWindow().setTileset(gameScene.getTileset());

        tileCanvas.setImage(gameScene.getTileset().getImage());
        tileCanvas.setWidth(gameScene.getNumRows() * gameScene.getTileset().getTileWidth());
        tileCanvas.setHeight(gameScene.getNumCols() * gameScene.getTileset().getTileHeight());
        tileCanvas.setTileWidth(gameScene.getTileset().getTileWidth());
        tileCanvas.setTileHeight(gameScene.getTileset().getTileHeight());

        final ObservableList<PropertySheet.Item> properties = BeanPropertyUtils.getProperties(gameScene);
        propertySheet.getItems().setAll(BeanPropertyUtils.getProperties(gameScene));
    }

    @FXML private void initialize() {
        listSceneItems.setItems(FXCollections.emptyObservableList());

        tileCanvas = new TileCanvas();
        tileCanvas.setBackgroundColor(Color.BLACK);
        tileCanvas.setZoomFactor(2);
        tileCanvas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        PaneUtils.setAnchors(tileCanvas, 0.0);
        sceneGridPane.setContent(tileCanvas);

        propertySheet = new PropertySheet();
        PaneUtils.setAnchors(propertySheet, 0.0);
        propertySheetPane.getChildren().add(propertySheet);
    }
}
