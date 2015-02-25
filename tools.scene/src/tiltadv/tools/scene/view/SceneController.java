package tiltadv.tools.scene.view;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import dhcoder.tool.javafx.control.ResizableCanvas;
import dhcoder.tool.javafx.control.Tile;
import dhcoder.tool.javafx.game.model.Scene;
import dhcoder.tool.javafx.game.view.TiledImage;
import dhcoder.tool.javafx.property.PropertyUtils;
import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.ImageUtils;
import dhcoder.tool.javafx.utils.PaneUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import tiltadv.tools.scene.AppSettings;
import tiltadv.tools.scene.SceneContext;
import tiltadv.tools.scene.SceneTool;
import tiltadv.tools.scene.events.ContextChangedEventArgs;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 * <p/>
 * Note that we actually re-use the same scene UI for all scenes - that is, we instantiate one set of controls and
 * toggle its contents depending on the active scene context.
 */
public final class SceneController extends FxController {
    @FXML private ScrollPane sceneGridPane;
    @FXML private ListView listSceneItems;
    @FXML private AnchorPane propertySheetPane;

    private SceneTool sceneTool;
    private ResizableCanvas sceneCanvas;
    private PropertySheet propertySheet;

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
        sceneTool.getEventBus().register(this);
    }

    @Subscribe
    public void onSceneContextChanged(final ContextChangedEventArgs args) {
        if (!args.getContextOpt().hasValue()) {
            sceneCanvas.clear();
            sceneCanvas.setOnResized(null);
            return;
        }

        SceneContext context = args.getContextOpt().getValue();
        Scene gameScene = context.getScene();

        TiledImage tiledImage = new TiledImage(gameScene.getTileset(), gameScene.getNumRows(), gameScene.getNumCols(),
            Lists.newArrayList(new Tile(0, 0), new Tile(2, 0), new Tile(1, 0), new Tile(0, 2)));
        tiledImage.setBackgroundColor(Color.BLACK);
        tiledImage.getTileIndices().setAll(0, 0, 0, 0, 0, 0, 1, 0);

        tiledImage.setOnRefreshed(image -> {
            Image zoomedImage = ImageUtils.zoom(image, AppSettings.ZOOM_FACTOR);
            sceneCanvas.setOnResized(canvas -> canvas.getGraphicsContext2D().drawImage(zoomedImage, 0, 0));
            sceneCanvas.resize(zoomedImage.getWidth(), zoomedImage.getHeight());
        });

        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(gameScene);
//        final ObservableList<PropertySheet.Item> properties = BeanPropertyUtils.getProperties(gameScene);
        propertySheet.getItems().setAll(properties);
    }

    @FXML
    private void initialize() {
        listSceneItems.setItems(FXCollections.emptyObservableList());

        sceneCanvas = new ResizableCanvas();
        PaneUtils.setAnchors(sceneCanvas, 0.0);
        sceneGridPane.setContent(sceneCanvas);

        propertySheet = new PropertySheet();
        PaneUtils.setAnchors(propertySheet, 0.0);
        propertySheetPane.getChildren().add(propertySheet);
    }
}
