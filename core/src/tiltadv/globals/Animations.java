package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Static class which holds all animations used by our game.
 */
public final class Animations {

    public static final Animation PLAYER_S;
    public static final Animation PLAYER_E;
    public static final Animation PLAYER_N;
    public static final Animation PLAYER_SE;
    public static final Animation PLAYER_NW;
    public static final Animation OCTOUP;
    public static final Animation OCTODOWN;
    public static final Animation OCTOLEFT;
    public static final Animation OCTORIGHT;

    static {
        PLAYER_S = new Animation(0.1f, Tiles.PLAYER_S1, Tiles.PLAYER_S2, Tiles.PLAYER_S1, Tiles.PLAYER_S3);
        PLAYER_S.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_E = new Animation(0.1f, Tiles.PLAYER_E1, Tiles.PLAYER_E2, Tiles.PLAYER_E1, Tiles.PLAYER_E3);
        PLAYER_E.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_N = new Animation(0.1f, Tiles.PLAYER_N1, Tiles.PLAYER_N2, Tiles.PLAYER_N1, Tiles.PLAYER_N3);
        PLAYER_N.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_SE = new Animation(0.1f, Tiles.PLAYER_SE1, Tiles.PLAYER_SE2, Tiles.PLAYER_SE1, Tiles.PLAYER_SE3);
        PLAYER_SE.setPlayMode(Animation.PlayMode.LOOP);
        PLAYER_NW = new Animation(0.1f, Tiles.PLAYER_NW1, Tiles.PLAYER_NW2, Tiles.PLAYER_NW1, Tiles.PLAYER_NW3);
        PLAYER_NW.setPlayMode(Animation.PlayMode.LOOP);

//        PLAYER_S = new Animation(0.05f, Tiles.LINK_S1, Tiles.LINK_S2, Tiles.LINK_S3, Tiles.LINK_S2, Tiles.LINK_S1,
//            Tiles.LINK_S5, Tiles.LINK_S6, Tiles.LINK_S5);
//        PLAYER_S.setPlayMode(Animation.PlayMode.LOOP);
//
//        PLAYER_E = new Animation(0.05f, Tiles.LINK_E1, Tiles.LINK_E2, Tiles.LINK_E3, Tiles.LINK_E4, Tiles.LINK_E4,
//            Tiles.LINK_E5, Tiles.LINK_E6, Tiles.LINK_E7, Tiles.LINK_E8);
//        PLAYER_E.setPlayMode(Animation.PlayMode.LOOP);
//
//        PLAYER_N = new Animation(0.05f, Tiles.LINK_N1, Tiles.LINK_N2, Tiles.LINK_N3, Tiles.LINK_N4, Tiles.LINK_N1,
//            Tiles.LINK_N5, Tiles.LINK_N6, Tiles.LINK_N7);
//        PLAYER_N.setPlayMode(Animation.PlayMode.LOOP);

//        PLAYER_NE = new Animation(0.1f, Tiles.LINK_NE1, Tiles.LINK_NE2, Tiles.LINK_NE1, Tiles.LINK_NE3);
//        PLAYER_NE.setPlayMode(Animation.PlayMode.LOOP);
//
//        PLAYER_SW = new Animation(0.1f, Tiles.LINK_SW1, Tiles.LINK_SW2, Tiles.LINK_SW1, Tiles.LINK_SW3);
//        PLAYER_SW.setPlayMode(Animation.PlayMode.LOOP);

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
