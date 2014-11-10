package tiltadv.globals;

import dhcoder.libgdx.assets.Scene;
import dhcoder.libgdx.assets.SceneDatastore;

/**
 * Static class which holds all scenes used by our game.
 */
public final class Scenes {

    public static final Scene DEMO;

    static {
        SceneDatastore scenes = GameData.scenes;

        DEMO = scenes.get("demo");
    }

    private Scenes() { } // Disabled constructor
}
