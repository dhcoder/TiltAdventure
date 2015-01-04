package dhcoder.tool.javafx.game.view;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.control.ResizableCanvas;
import dhcoder.tool.javafx.game.model.Tileset;
import dhcoder.tool.javafx.utils.FxController;
import dhcoder.tool.javafx.utils.ImageUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A floating window which displays a tileset and lets the user select an active tile.
 */
public final class TilesetWindow extends Stage {

    private static final int DEFAULT_HEIGHT = 300;
    private static final int DEFAULT_WIDTH = 300;

    private final Opt<Tileset> tilesetOpt = Opt.withNoValue();
    private final Opt<Image> zoomedImageOpt = Opt.withNoValue();
    private final ResizableCanvas canvas = new ResizableCanvas();

    private int zoomFactor = 1;

    public TilesetWindow() {
        super(StageStyle.UTILITY);
        setTitle("Tileset Window");

        final TilesetWindowController controller = FxController.loadView(TilesetWindowController.class);
        controller.contentPane.setContent(canvas);

        zoomFactor = getZoomFactor(controller.zoomGroup.getSelectedToggle());
        controller.zoomGroup.selectedToggleProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(final Observable observable) {
                if (!tilesetOpt.hasValue()) {
                    return;
                }

                Toggle toggle = controller.zoomGroup.getSelectedToggle();
                zoomFactor = getZoomFactor(toggle);
                resampleTilesetImage(tilesetOpt.getValue().getImage());
                draw();
            }
        });

        setScene(new Scene(controller.getRoot(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public Opt<Tileset> getTilesetOpt() {
        return tilesetOpt;
    }

    public void clearTileset() {
        tilesetOpt.clear();
        zoomedImageOpt.clear();
        draw();

        canvas.setWidth(0);
        canvas.setHeight(0);
    }

    public TilesetWindow setTileset(final Tileset tileset) {
        tilesetOpt.set(tileset);
        resampleTilesetImage(tileset.getImage());
        draw();
        return this;
    }

    private int getZoomFactor(final Toggle toggle) {
        // UserData specified in FXML
        return Integer.parseInt((String)toggle.getUserData());
    }

    private void resampleTilesetImage(final Image tilesetImage) {
        canvas.setWidth(tilesetImage.getWidth() * zoomFactor);
        canvas.setHeight(tilesetImage.getHeight() * zoomFactor);
        zoomedImageOpt.set(ImageUtils.zoom(tilesetImage, zoomFactor));
    }

    private void draw() {
        if (!zoomedImageOpt.hasValue()) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        Image image = zoomedImageOpt.getValue();
        canvas.getGraphicsContext2D().setFill(Color.MAGENTA);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
