package dhcoder.tool.javafx.game.view;

import dhcoder.support.opt.Opt;
import dhcoder.tool.javafx.control.ResizableCanvas;
import dhcoder.tool.javafx.game.model.Tileset;
import dhcoder.tool.javafx.utils.ImageUtils;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A floating window which displays a tileset and lets the user select an active tile.
 */
public final class TilesetWindow extends Stage {

    private static final int DEFAULT_HEIGHT = 300;
    private static final int DEFAULT_WIDTH = 300;
    private static final int ZOOM = 4;
    private Opt<Tileset> tilesetOpt = Opt.withNoValue();
    private Opt<Image> zoomedImageOpt = Opt.withNoValue();
    private ResizableCanvas canvas = new ResizableCanvas();

    public TilesetWindow() {
        super(StageStyle.UTILITY);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        setScene(new Scene(scrollPane, DEFAULT_WIDTH, DEFAULT_HEIGHT));
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
        canvas.setWidth(tileset.getImage().getWidth() * ZOOM);
        canvas.setHeight(tileset.getImage().getHeight() * ZOOM);

        zoomedImageOpt.set(ImageUtils.zoom(tileset.getImage(), ZOOM));

        draw();
        return this;
    }

    private void draw() {
        if (!zoomedImageOpt.hasValue()) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        Image image = zoomedImageOpt.getValue();
        canvas.getGraphicsContext2D().drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
