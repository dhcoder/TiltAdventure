package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Static class which holds all animations used by our game.
 */
public final class Animations {

    public static final Animation PLAYER_S;
    public static final Animation PLAYER_SE;
    public static final Animation PLAYER_E;
    public static final Animation PLAYER_NE;
    public static final Animation PLAYER_N;
    public static final Animation PLAYER_NW;
    public static final Animation PLAYER_W;
    public static final Animation PLAYER_SW;
    public static final Animation OCTOUP;
    public static final Animation OCTODOWN;
    public static final Animation OCTOLEFT;
    public static final Animation OCTORIGHT;

    static {
        PLAYER_S = new Animation(0.1f, Tiles.LINKDOWN1, Tiles.LINKDOWN2);
        PLAYER_S.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_SE = new Animation(0.1f, Tiles.LINKDOWN1, Tiles.LINKDOWN2);
        PLAYER_SE.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_E = new Animation(0.1f, Tiles.LINKRIGHT1, Tiles.LINKRIGHT2);
        PLAYER_E.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_NE = new Animation(0.1f, Tiles.LINKRIGHT1, Tiles.LINKRIGHT2);
        PLAYER_NE.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_N = new Animation(0.1f, Tiles.LINKUP1, Tiles.LINKUP2);
        PLAYER_N.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_NW = new Animation(0.1f, Tiles.LINKUP1, Tiles.LINKUP2);
        PLAYER_NW.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_W = new Animation(0.1f, Tiles.LINKLEFT1, Tiles.LINKLEFT2);
        PLAYER_W.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_SW = new Animation(0.1f, Tiles.LINKLEFT1, Tiles.LINKLEFT2);
        PLAYER_SW.setPlayMode(Animation.PlayMode.LOOP);
        OCTOUP  = new Animation(0.1f, Tiles.OCTOUP1, Tiles.OCTOUP2);
        OCTOUP.setPlayMode(Animation.PlayMode.LOOP);
        OCTODOWN  = new Animation(0.1f, Tiles.OCTODOWN1, Tiles.OCTODOWN2);
        OCTODOWN.setPlayMode(Animation.PlayMode.LOOP);
        OCTOLEFT  = new Animation(0.1f, Tiles.OCTOLEFT1, Tiles.OCTOLEFT2);
        OCTOLEFT.setPlayMode(Animation.PlayMode.LOOP);
        OCTORIGHT  = new Animation(0.1f, Tiles.OCTORIGHT1, Tiles.OCTORIGHT2);
        OCTORIGHT.setPlayMode(Animation.PlayMode.LOOP);
    }

    private Animations() { } // Disabled constructor

}
