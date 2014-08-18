package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Static class which holds all animations used by our game.
 */
public final class Animations {

    public static final Animation PLAYERUP;
    public static final Animation PLAYERDOWN;
    public static final Animation PLAYERLEFT;
    public static final Animation PLAYERRIGHT;
    public static final Animation OCTOUP;
    public static final Animation OCTODOWN;
    public static final Animation OCTOLEFT;
    public static final Animation OCTORIGHT;

    static {
        PLAYERUP = new Animation(0.1f, Tiles.PLAYERUP1, Tiles.PLAYERUP2);
        PLAYERUP.setPlayMode(Animation.PlayMode.LOOP);
        PLAYERDOWN  = new Animation(0.1f, Tiles.PLAYERDOWN1, Tiles.PLAYERDOWN2);
        PLAYERDOWN.setPlayMode(Animation.PlayMode.LOOP);
        PLAYERLEFT  = new Animation(0.1f, Tiles.PLAYERLEFT1, Tiles.PLAYERLEFT2);
        PLAYERLEFT.setPlayMode(Animation.PlayMode.LOOP);
        PLAYERRIGHT  = new Animation(0.1f, Tiles.PLAYERRIGHT1, Tiles.PLAYERRIGHT2);
        PLAYERRIGHT.setPlayMode(Animation.PlayMode.LOOP);
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
