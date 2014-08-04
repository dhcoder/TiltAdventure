package tiltadv.globals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Static class which acts as a location to hold all tiles used by our game.
 */
public final class Tiles {

    private static final Texture texture;

    public static final Sprite PLAYERUP1;
    public static final Sprite PLAYERUP2;
    public static final Sprite PLAYERDOWN1;
    public static final Sprite PLAYERDOWN2;
    public static final Sprite PLAYERLEFT1;
    public static final Sprite PLAYERLEFT2;
    public static final Sprite PLAYERRIGHT1;
    public static final Sprite PLAYERRIGHT2;
    public static final Sprite RODRIGHT;
    public static final Sprite ROCK;

    static {
        texture = new Texture("Tiles.png");

        PLAYERUP1 = new Sprite(texture, 60, 0, 16, 16);
        PLAYERUP2 = new Sprite(texture, 60, 30, 16, 16);
        PLAYERDOWN1 = new Sprite(texture, 0, 0, 16, 16);
        PLAYERDOWN2 = new Sprite(texture, 0, 30, 16, 16);
        PLAYERLEFT1 = new Sprite(texture, 30, 0, 16, 16);
        PLAYERLEFT2 = new Sprite(texture, 30, 30, 16, 16);
        PLAYERRIGHT1 = new Sprite(texture, 90, 30, 16, 16);
        PLAYERRIGHT2 = new Sprite(texture, 90, 0, 16, 16);
        RODRIGHT = new Sprite(texture, 98, 126, 13, 4);
        ROCK = new Sprite(texture, 120, 0, 16, 16);
    }

    public static void dispose() {
        texture.dispose();
    }

    private Tiles() { } // Disabled constructor

}
