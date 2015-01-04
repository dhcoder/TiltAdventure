package tiltadv.tools.scene.view;

import com.google.common.eventbus.Subscribe;
import dhcoder.tool.javafx.game.model.Scene;
import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import tiltadv.tools.scene.SceneContext;
import tiltadv.tools.scene.SceneTool;
import tiltadv.tools.scene.events.ContextChangedEventArgs;

/**
 * UI for editing a scene. Contains a list of all actors in the scene, as well as a property editor and visual grid.
 */
public final class SceneController extends FxController {
    @FXML private AnchorPane sceneGridPane;
    @FXML private ListView listSceneItems;
    private SceneTool sceneTool;

    public void setSceneTool(final SceneTool sceneTool) {
        this.sceneTool = sceneTool;
        sceneTool.getEventBus().register(this);
    }

    @Subscribe
    public void onSceneContextChanged(final ContextChangedEventArgs args) {
        if (!args.getContextOpt().hasValue()) {return;}

        SceneContext context = args.getContextOpt().getValue();
        Scene gameScene = context.getScene();

        sceneTool.getTilesetWindow().setTileset(gameScene.getTileset());
        sceneTool.getTilesetWindow().show();
//        ObservableList<Tile> tiles = FXCollections.observableArrayList();
//        GridView<Tile> gridView = new GridView<>(tiles);
//        gridView.setCellWidth(64d);
//        gridView.setCellHeight(64d);
//        gridView.setCellFactory(param -> new TileGridCell());
//
//        for (int i = 0; i < 5; i++) {
//            tiles.add(new Tile(gameScene.getTileset(), i, 0));
//        }
//
//        Stage stage = new Stage(StageStyle.UTILITY);
//        stage.initOwner(sceneTool.getStage());
//        stage.setScene(new javafx.scene.Scene(gridView, 300, 300));
//        Canvas test = new Canvas(300, 300);
//        test.getGraphicsContext2D().drawImage(gameScene.getTileset().getImage(), 0, 0);
//        ImageView test = new ImageView(gameScene.getTileset().getImage());
//        test.setFitWidth(16*4);
//        test.setFitHeight(16 * 4);
//        test.setViewport(new Rectangle2D(0, 0, 16, 16));
//
//        Pane pane = new Pane(test);
//        stage.setScene(new javafx.scene.Scene(pane));
//        stage.show();
    }
}
