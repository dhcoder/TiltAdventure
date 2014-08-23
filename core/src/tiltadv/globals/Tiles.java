package tiltadv.globals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Static class which holds all tiles used by our game.
 */
public final class Tiles {

    public static final TextureRegion PLAYER_S;
    public static final TextureRegion PLAYER_SE;
    public static final TextureRegion PLAYER_E;
    public static final TextureRegion PLAYER_NE;
    public static final TextureRegion PLAYER_N;
    public static final TextureRegion PLAYER_NW;
    public static final TextureRegion PLAYER_W;
    public static final TextureRegion PLAYER_SW;
    public static final TextureRegion LINKDOWN1;
    public static final TextureRegion LINKDOWN2;
    public static final TextureRegion LINKLEFT1;
    public static final TextureRegion LINKLEFT2;
    public static final TextureRegion LINKUP1;
    public static final TextureRegion LINKUP2;
    public static final TextureRegion LINKRIGHT1;
    public static final TextureRegion LINKRIGHT2;
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
    private static final Texture textureSource;
    private static final Texture tempTextureSource;

    static {
        textureSource = new Texture("Tiles.png");
        tempTextureSource = new Texture("Dude.png");

        PLAYER_S = new TextureRegion(tempTextureSource, 0, 0, 32, 32);
        PLAYER_SE = new TextureRegion(tempTextureSource, 32, 0, 32, 32);
        PLAYER_E = new TextureRegion(tempTextureSource, 64, 0, 32, 32);
        PLAYER_NE = new TextureRegion(tempTextureSource, 96, 0, 32, 32);
        PLAYER_N = new TextureRegion(tempTextureSource, 128, 0, 32, 32);
        PLAYER_NW = new TextureRegion(tempTextureSource, 160, 0, 32, 32);
        PLAYER_W = new TextureRegion(tempTextureSource, 192, 0, 32, 32);
        PLAYER_SW = new TextureRegion(tempTextureSource, 224, 0, 32, 32);

        LINKDOWN1 = new TextureRegion(textureSource, 0, 0, 16, 16);
        LINKDOWN2 = new TextureRegion(textureSource, 0, 30, 16, 16);
        LINKLEFT1 = new TextureRegion(textureSource, 30, 0, 16, 16);
        LINKLEFT2 = new TextureRegion(textureSource, 30, 30, 16, 16);
        LINKUP1 = new TextureRegion(textureSource, 60, 0, 16, 16);
        LINKUP2 = new TextureRegion(textureSource, 60, 30, 16, 16);
        LINKRIGHT1 = new TextureRegion(textureSource, 90, 30, 16, 16);
        LINKRIGHT2 = new TextureRegion(textureSource, 90, 0, 16, 16);
        OCTODOWN1 = new TextureRegion(textureSource, 150, 0, 16, 16);
        OCTODOWN2 = new TextureRegion(textureSource, 150, 30, 16, 16);
        OCTOLEFT1 = new TextureRegion(textureSource, 180, 0, 16, 16);
        OCTOLEFT2 = new TextureRegion(textureSource, 180, 30, 16, 16);
        OCTOUP1 = new TextureRegion(textureSource, 210, 0, 16, 16);
        OCTOUP2 = new TextureRegion(textureSource, 210, 30, 16, 16);
        OCTORIGHT1 = new TextureRegion(textureSource, 240, 30, 16, 16);
        OCTORIGHT2 = new TextureRegion(textureSource, 240, 0, 16, 16);
        RODRIGHT = new TextureRegion(textureSource, 98, 126, 13, 4);
        ROCK = new TextureRegion(textureSource, 120, 0, 16, 16);
    }

    public static void dispose() {
        textureSource.dispose();
    }

    private Tiles() { } // Disabled constructor

}
