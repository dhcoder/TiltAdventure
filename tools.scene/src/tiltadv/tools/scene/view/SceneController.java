package tiltadv.tools.scene.view;

import com.google.common.eventbus.Subscribe;
import dhcoder.tool.javafx.control.GridCanvas;
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
    private GridCanvas gridCanvas;
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

        gridCanvas.setImage(gameScene.getTileset().getImage());
        gridCanvas.setWidth(gameScene.getNumRows() * gameScene.getTileset().getTileWidth());
        gridCanvas.setHeight(gameScene.getNumCols() * gameScene.getTileset().getTileHeight());
        gridCanvas.setTileWidth(gameScene.getTileset().getTileWidth());
        gridCanvas.setTileHeight(gameScene.getTileset().getTileHeight());

        final ObservableList<PropertySheet.Item> properties = BeanPropertyUtils.getProperties(gameScene);
        propertySheet.getItems().setAll(BeanPropertyUtils.getProperties(gameScene));
    }

    @FXML private void initialize() {
        listSceneItems.setItems(FXCollections.emptyObservableList());

        gridCanvas = new GridCanvas();
        gridCanvas.setBackgroundColor(Color.BLACK);
        gridCanvas.setZoomFactor(2);
        gridCanvas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        PaneUtils.setAnchors(gridCanvas, 0.0);
        sceneGridPane.setContent(gridCanvas);

        propertySheet = new PropertySheet();
        PaneUtils.setAnchors(propertySheet, 0.0);
        propertySheetPane.getChildren().add(propertySheet);
    }
}
