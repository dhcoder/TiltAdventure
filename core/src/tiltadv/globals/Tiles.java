package tiltadv.globals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Static class which holds all tiles used by our game.
 */
public final class Tiles {

    public static final TextureRegion PLAYER_S1;
    public static final TextureRegion PLAYER_S2;
    public static final TextureRegion PLAYER_S3;
    public static final TextureRegion PLAYER_E1;
    public static final TextureRegion PLAYER_E2;
    public static final TextureRegion PLAYER_E3;
    public static final TextureRegion PLAYER_N1;
    public static final TextureRegion PLAYER_N2;
    public static final TextureRegion PLAYER_N3;
    public static final TextureRegion LINK_S1;
    public static final TextureRegion LINK_S2;
    public static final TextureRegion LINK_S3;
    public static final TextureRegion LINK_S4;
    public static final TextureRegion LINK_S5;
    public static final TextureRegion LINK_S6;
    public static final TextureRegion LINK_E1;
    public static final TextureRegion LINK_E2;
    public static final TextureRegion LINK_E3;
    public static final TextureRegion LINK_E4;
    public static final TextureRegion LINK_E5;
    public static final TextureRegion LINK_E6;
    public static final TextureRegion LINK_E7;
    public static final TextureRegion LINK_E8;
    public static final TextureRegion LINK_N1;
    public static final TextureRegion LINK_N2;
    public static final TextureRegion LINK_N3;
    public static final TextureRegion LINK_N4;
    public static final TextureRegion LINK_N5;
    public static final TextureRegion LINK_N6;
    public static final TextureRegion LINK_N7;
    public static final TextureRegion LINK_SW1;
    public static final TextureRegion LINK_SW2;
    public static final TextureRegion LINK_SW3;
    public static final TextureRegion LINK_NE1;
    public static final TextureRegion LINK_NE2;
    public static final TextureRegion LINK_NE3;
    public static final TextureRegion OCTODOWN1;
    public static final TextureRegion OCTODOWN2;
    public static final TextureRegion OCTOLEFT1;
    public static final TextureRegion OCTOLEFT2;
    public static final TextureRegion OCTOUP1;
    public static final TextureRegion OCTOUP2;
    public static final TextureRegion OCTORIGHT1;
    public static final TextureRegion OCTORIGHT2;
    public static final TextureRegion BOULDER;
    public static final TextureRegion ROCK;
    public static final TextureRegion SWORDRIGHT;
    public static final TextureRegion SENSOR;
    private static final Texture textureSource8bit;
    private static final Texture textureSource16bit;
    private static final Texture textureDude;

    static {
        textureSource8bit = new Texture("Tiles.png");
        textureSource16bit = new Texture("Tiles16.png");
        textureDude = new Texture("Dude.png");

        PLAYER_S1 = new TextureRegion(textureDude, 0, 0, 16, 24);
        PLAYER_S2 = new TextureRegion(textureDude, 16, 0, 16, 24);
        PLAYER_S3 = new TextureRegion(textureDude, 48, 0, 16, 24);
        PLAYER_E1 = new TextureRegion(textureDude, 0, 24, 16, 24);
        PLAYER_E2 = new TextureRegion(textureDude, 16, 24, 16, 24);
        PLAYER_E3 = new TextureRegion(textureDude, 48, 24, 16, 24);
        PLAYER_N1 = new TextureRegion(textureDude, 0, 72, 16, 24);
        PLAYER_N2 = new TextureRegion(textureDude, 16, 72, 16, 24);
        PLAYER_N3 = new TextureRegion(textureDude, 48, 72, 16, 24);

        LINK_S1 = new TextureRegion(textureSource16bit, 0, 0, 16, 24);
        LINK_S2 = new TextureRegion(textureSource16bit, 16, 0, 16, 24);
        LINK_S3 = new TextureRegion(textureSource16bit, 32, 0, 16, 24);
        LINK_S4 = new TextureRegion(textureSource16bit, 48, 0, 16, 24);
        LINK_S5 = new TextureRegion(textureSource16bit, 64, 0, 16, 24);
        LINK_S6 = new TextureRegion(textureSource16bit, 80, 0, 16, 24);
        LINK_E1 = new TextureRegion(textureSource16bit, 0, 24, 16, 24);
        LINK_E2 = new TextureRegion(textureSource16bit, 16, 24, 16, 24);
        LINK_E3 = new TextureRegion(textureSource16bit, 32, 24, 16, 24);
        LINK_E4 = new TextureRegion(textureSource16bit, 48, 24, 16, 24);
        LINK_E5 = new TextureRegion(textureSource16bit, 64, 24, 16, 24);
        LINK_E6 = new TextureRegion(textureSource16bit, 80, 24, 16, 24);
        LINK_E7 = new TextureRegion(textureSource16bit, 96, 24, 16, 24);
        LINK_E8 = new TextureRegion(textureSource16bit, 112, 24, 16, 24);
        LINK_N1 = new TextureRegion(textureSource16bit, 0, 48, 16, 24);
        LINK_N2 = new TextureRegion(textureSource16bit, 16, 48, 16, 24);
        LINK_N3 = new TextureRegion(textureSource16bit, 32, 48, 16, 24);
        LINK_N4 = new TextureRegion(textureSource16bit, 48, 48, 16, 24);
        LINK_N5 = new TextureRegion(textureSource16bit, 64, 48, 16, 24);
        LINK_N6 = new TextureRegion(textureSource16bit, 80, 48, 16, 24);
        LINK_N7 = new TextureRegion(textureSource16bit, 96, 48, 16, 24);
        LINK_SW1 = new TextureRegion(textureSource16bit, 0, 72, 16, 24);
        LINK_SW2 = new TextureRegion(textureSource16bit, 16, 72, 16, 24);
        LINK_SW3 = new TextureRegion(textureSource16bit, 32, 72, 16, 24);
        LINK_NE1 = new TextureRegion(textureSource16bit, 0, 96, 16, 24);
        LINK_NE2 = new TextureRegion(textureSource16bit, 16, 96, 16, 24);
        LINK_NE3 = new TextureRegion(textureSource16bit, 32, 96, 16, 24);

        OCTODOWN1 = new TextureRegion(textureSource8bit, 150, 0, 16, 16);
        OCTODOWN2 = new TextureRegion(textureSource8bit, 150, 30, 16, 16);
        OCTOLEFT1 = new TextureRegion(textureSource8bit, 180, 0, 16, 16);
        OCTOLEFT2 = new TextureRegion(textureSource8bit, 180, 30, 16, 16);
        OCTOUP1 = new TextureRegion(textureSource8bit, 210, 0, 16, 16);
        OCTOUP2 = new TextureRegion(textureSource8bit, 210, 30, 16, 16);
        OCTORIGHT1 = new TextureRegion(textureSource8bit, 240, 30, 16, 16);
        OCTORIGHT2 = new TextureRegion(textureSource8bit, 240, 0, 16, 16);

        BOULDER = new TextureRegion(textureSource8bit, 120, 0, 16, 16);
        ROCK = new TextureRegion(textureSource8bit, 120, 30, 8, 10);
        SWORDRIGHT = new TextureRegion(textureSource8bit, 120, 60, 12, 7);
        SENSOR = new TextureRegion(textureSource8bit, 151, 61, 15, 15);
    }

    public static void dispose() {
        // TODO: Use asset manager instead
        textureSource8bit.dispose();
        textureSource16bit.dispose();
        textureDude.dispose();
    }

    private Tiles() { } // Disabled constructor

}
