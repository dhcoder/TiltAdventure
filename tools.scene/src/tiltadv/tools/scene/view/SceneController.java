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
        if (!args.getContextOpt().hasValue()) {
            sceneTool.getTilesetWindow().clearTileset();
            return;
        }

        SceneContext context = args.getContextOpt().getValue();
        Scene gameScene = context.getScene();

        sceneTool.getTilesetWindow().setTileset(gameScene.getTileset());
        sceneTool.getTilesetWindow().show();
    }
}
