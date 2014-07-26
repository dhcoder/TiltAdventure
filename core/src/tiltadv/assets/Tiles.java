package tiltadv.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Static class which acts as a location to hold all tiles used by our game.
 */
public final class Tiles {

    private static Texture texture;

    public static Sprite playerUp1;
    public static Sprite playerUp2;
    public static Sprite playerDown1;
    public static Sprite playerDown2;
    public static Sprite playerLeft1;
    public static Sprite playerLeft2;
    public static Sprite playerRight1;
    public static Sprite playerRight2;
    public static Sprite rodRight;
    public static Sprite rock;

    static {
        texture = new Texture("Tiles.png");

        playerUp1 = new Sprite(texture, 60, 0, 16, 16);
        playerUp2 = new Sprite(texture, 60, 30, 16, 16);
        playerDown1 = new Sprite(texture, 0, 0, 16, 16);
        playerDown2 = new Sprite(texture, 0, 30, 16, 16);
        playerLeft1 = new Sprite(texture, 30, 0, 16, 16);
        playerLeft2 = new Sprite(texture, 30, 30, 16, 16);
        playerRight1 = new Sprite(texture, 90, 30, 16, 16);
        playerRight2 = new Sprite(texture, 90, 0, 16, 16);
        rodRight = new Sprite(texture, 98, 126, 13, 4);
        rock = new Sprite(texture, 120, 0, 16, 16);
    }

    public static void dispose() {
        texture.dispose();
    }

    private Tiles() { } // Disabled constructor

}
