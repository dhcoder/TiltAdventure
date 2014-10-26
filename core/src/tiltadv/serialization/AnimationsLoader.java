package tiltadv.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import tiltadv.assets.AnimationDatastore;
import tiltadv.assets.AnimationGroup;
import tiltadv.assets.Tileset;
import tiltadv.assets.TilesetDatastore;
import tiltadv.globals.Services;

import java.util.ArrayList;

/**
 * Class that loads {@link Animation}s into our {@link AnimationDatastore}.
 */
public final class AnimationsLoader {

    private final static class AnimationGroupData {
        public String tilesetPath;
        public ArrayList<AnimationData> animations;
    }

    private final static class AnimationData {
        public String name;
        public float frameDuration;
        public int[][] frames;
    }

    public static void load(final String jsonPath) {
        final Json json = Services.get(Json.class);
        final TilesetDatastore tilesets = Services.get(TilesetDatastore.class);
        final AnimationDatastore animations = Services.get(AnimationDatastore.class);

        final FileHandle fileHandle = Gdx.files.internal(jsonPath);
        AnimationGroupData groupData = json.fromJson(AnimationGroupData.class, fileHandle.readString());

        Tileset tileset = tilesets.get(groupData.tilesetPath);
        String groupName = fileHandle.nameWithoutExtension();

        AnimationGroup animationGroup = new AnimationGroup();
        final int numAnimations = groupData.animations.size();
        for (int i = 0; i < numAnimations; ++i) {
            AnimationData animData = groupData.animations.get(i);
            TextureRegion[] frames = new TextureRegion[animData.frames.length];
            for (int j = 0; j < frames.length; ++j) {
                frames[j] = tileset.getTile(animData.frames[j][0], animData.frames[j][1]);
            }

            final Animation animation = new Animation(animData.frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
            animationGroup.add(animData.name, animation);
        }

        animations.add(groupName, animationGroup);
    }

}
