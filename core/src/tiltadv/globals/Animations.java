package tiltadv.globals;

import com.badlogic.gdx.graphics.g2d.Animation;
import tiltadv.assets.AnimationDatastore;

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
    public static final Animation OCTORIGHT;

    static {
        AnimationDatastore animations = Services.get(AnimationDatastore.class);

        PLAYER_S = animations.get("player", "walk_s");
        PLAYER_E = animations.get("player", "walk_e");
        PLAYER_N = animations.get("player", "walk_n");
        PLAYER_SE = animations.get("player", "walk_se");
        PLAYER_NW = animations.get("player", "walk_nw");

        OCTOUP = animations.get("octo", "walk_n");
        OCTODOWN = animations.get("octo", "walk_s");
        OCTORIGHT = animations.get("octo", "walk_e");
    }

    private Animations() { } // Disabled constructor
}
