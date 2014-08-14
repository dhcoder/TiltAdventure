package tiltadv.globals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Static class which acts as a location to hold all tiles used by our game.
 */
public final class Tiles {

    public static final TextureRegion PLAYERDOWN1;
    public static final TextureRegion PLAYERDOWN2;
    public static final TextureRegion PLAYERLEFT1;
    public static final TextureRegion PLAYERLEFT2;
    public static final TextureRegion PLAYERUP1;
    public static final TextureRegion PLAYERUP2;
    public static final TextureRegion PLAYERRIGHT1;
    public static final TextureRegion PLAYERRIGHT2;
    public static final TextureRegion OCTODOWN1;
    public static final TextureRegion OCTODOWN2;
    public static final TextureRegion OCTOLEFT1;
    public static final TextureRegion OCTOLEFT2;
    public static final TextureRegion OCTOUP1;
    public static final TextureRegion OCTOUP2;
    public static final TextureRegion OCTORIGHT1;
    public static final TextureRegion OCTORIGHT2;
    public static final TextureRegion RODRIGHT;
    public static final TextureRegion ROCK;
    private static final Texture texture;

    static {
        texture = new Texture("Tiles.png");

        PLAYERDOWN1 = new TextureRegion(texture, 0, 0, 16, 16);
        PLAYERDOWN2 = new TextureRegion(texture, 0, 30, 16, 16);
        PLAYERLEFT1 = new TextureRegion(texture, 30, 0, 16, 16);
        PLAYERLEFT2 = new TextureRegion(texture, 30, 30, 16, 16);
        PLAYERUP1 = new TextureRegion(texture, 60, 0, 16, 16);
        PLAYERUP2 = new TextureRegion(texture, 60, 30, 16, 16);
        PLAYERRIGHT1 = new TextureRegion(texture, 90, 30, 16, 16);
        PLAYERRIGHT2 = new TextureRegion(texture, 90, 0, 16, 16);
        OCTODOWN1 = new TextureRegion(texture, 150, 0, 16, 16);
        OCTODOWN2 = new TextureRegion(texture, 150, 30, 16, 16);
        OCTOLEFT1 = new TextureRegion(texture, 180, 0, 16, 16);
        OCTOLEFT2 = new TextureRegion(texture, 180, 30, 16, 16);
        OCTOUP1 = new TextureRegion(texture, 210, 0, 16, 16);
        OCTOUP2 = new TextureRegion(texture, 210, 30, 16, 16);
        OCTORIGHT1 = new TextureRegion(texture, 240, 30, 16, 16);
        OCTORIGHT2 = new TextureRegion(texture, 240, 0, 16, 16);
        RODRIGHT = new TextureRegion(texture, 98, 126, 13, 4);
        ROCK = new TextureRegion(texture, 120, 0, 16, 16);
    }

    public static void dispose() {
        texture.dispose();
    }

    private Tiles() { } // Disabled constructor

}
