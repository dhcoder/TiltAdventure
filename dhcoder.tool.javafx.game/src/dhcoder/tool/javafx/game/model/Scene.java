package dhcoder.tool.game.model;

/**
 * Data class that corresponds to a TiltAdventure scene.
 */
public final class Scene {
    private final Tileset tileset;

    public Scene(final Tileset tileset) {
        this.tileset = tileset;
    }

    public Tileset getTileset() {
        return tileset;
    }

}
