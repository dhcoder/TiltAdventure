package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Static class which holds all animations used by our game.
 */
public final class Animations {

    public static final Animation PLAYER_S;
    public static final Animation PLAYER_E;
    public static final Animation PLAYER_NE;
    public static final Animation PLAYER_N;
    public static final Animation PLAYER_SW;
    public static final Animation OCTOUP;
    public static final Animation OCTODOWN;
    public static final Animation OCTOLEFT;
    public static final Animation OCTORIGHT;

    static {
        PLAYER_S = new Animation(0.1f, Tiles.LINK_S1, Tiles.LINK_S2);
        PLAYER_S.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_E = new Animation(0.1f, Tiles.LINK_E1, Tiles.LINK_E2);
        PLAYER_E.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_NE = new Animation(0.1f, Tiles.LINK_NE1, Tiles.LINK_NE2);
        PLAYER_NE.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_N = new Animation(0.1f, Tiles.LINK_N1, Tiles.LINK_N2);
        PLAYER_N.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_SW = new Animation(0.1f, Tiles.LINK_SW1, Tiles.LINK_SW2);
        PLAYER_SW.setPlayMode(Animation.PlayMode.LOOP);
        OCTOUP = new Animation(0.1f, Tiles.OCTOUP1, Tiles.OCTOUP2);
        OCTOUP.setPlayMode(Animation.PlayMode.LOOP);
        OCTODOWN = new Animation(0.1f, Tiles.OCTODOWN1, Tiles.OCTODOWN2);
        OCTODOWN.setPlayMode(Animation.PlayMode.LOOP);
        OCTOLEFT = new Animation(0.1f, Tiles.OCTOLEFT1, Tiles.OCTOLEFT2);
        OCTOLEFT.setPlayMode(Animation.PlayMode.LOOP);
        OCTORIGHT = new Animation(0.1f, Tiles.OCTORIGHT1, Tiles.OCTORIGHT2);
        OCTORIGHT.setPlayMode(Animation.PlayMode.LOOP);
    }

    private Animations() { } // Disabled constructor

}
