package tiltadv;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.time.Duration;
import tiltadv.assets.Tiles;
import tiltadv.entity.Component;
import tiltadv.entity.Entity;
import tiltadv.entity.components.behavior.PlayerBehaviorComponent;
import tiltadv.entity.components.controllers.AccelerometerComponent;
import tiltadv.entity.components.controllers.KeyboardComponent;
import tiltadv.entity.components.data.MotionComponent;
import tiltadv.entity.components.data.SizeComponent;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.data.TransformComponent;
import tiltadv.entity.components.display.FpsDisplayComponent;
import tiltadv.entity.components.display.PlayerDisplayComponent;
import tiltadv.entity.components.display.SpriteComponent;
import tiltadv.entity.components.display.TiltDisplayComponent;
import tiltadv.memory.Pools;

import java.util.ArrayList;
import java.util.List;

public final class GdxApplication extends ApplicationAdapter {

    private static final int VIEWPORT_HEIGHT = 240;
    private static final int VIEWPORT_WIDTH = 320;

    // When you hit a breakpoint while debugging an app, this causes the delta time to be huge between the next frame
    // and this one. Clamping to a reasonable max value works around this issue.
    private static final float MAX_DELTA_TIME_SECS = .5f;

    private BitmapFont font;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private List<Entity> entities;

    @Override
    public void create() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();
        font = new BitmapFont();

        entities = new ArrayList<Entity>();
        Entity playerEntity = AddPlayerEntity();
        AddRockEntity(-30, -40);
        AddRockEntity(-60, 70);
        AddRockEntity(50, -90);
        AddRockEntity(90, 80);
        AddTiltIndicatorEntity(playerEntity);
        AddFpsEntity();
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(1f, .88f, .66f, 1f); // Desert-ish color, for testing!
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numEntities; ++i) {
            entities.get(i).render(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }

        batch.dispose();
        font.dispose();
        Tiles.dispose();
    }

    private void update() {
        {
            Duration elapsedTime = Pools.duration.grabNew();
            elapsedTime.setSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));

            int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation
            for (int i = 0; i < numEntities; ++i) {
                entities.get(i).update(elapsedTime);
            }
            Pools.duration.free(elapsedTime);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private Entity AddPlayerEntity() {
        Duration animDuration = Duration.fromSeconds(.1f);
        Animation animUp = new Animation(animDuration.getSeconds(), Tiles.playerUp1, Tiles.playerUp2);
        animUp.setPlayMode(Animation.PlayMode.LOOP);
        Animation animDown = new Animation(animDuration.getSeconds(), Tiles.playerDown1, Tiles.playerDown2);
        animDown.setPlayMode(Animation.PlayMode.LOOP);
        Animation animLeft = new Animation(animDuration.getSeconds(), Tiles.playerLeft1, Tiles.playerLeft2);
        animLeft.setPlayMode(Animation.PlayMode.LOOP);
        Animation animRight = new Animation(animDuration.getSeconds(), Tiles.playerRight1, Tiles.playerRight2);
        animRight.setPlayMode(Animation.PlayMode.LOOP);

        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent());
        components.add(SizeComponent.from(Tiles.playerDown1));
        components.add(new TransformComponent());
        components.add(new MotionComponent());
        components.add(new TiltComponent());
        components.add(Gdx.app.getType() == Application.ApplicationType.Android ? new AccelerometerComponent() :
            new KeyboardComponent());
        components.add(new PlayerBehaviorComponent());
        components.add(new PlayerDisplayComponent(animUp, animDown, animLeft, animRight));

        Entity playerEntity = new Entity(components);
        entities.add(playerEntity);

        return playerEntity;
    }

    private void AddRockEntity(final float x, final float y) {
        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent(Tiles.rock));
        components.add(SizeComponent.from(Tiles.rock));
        components.add(new TransformComponent.Builder().setTranslate(x, y).build());
        entities.add(new Entity(components));
    }

    private void AddFpsEntity() {
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()).build();
        FpsDisplayComponent fpsDisplayComponent = new FpsDisplayComponent(font);
        entities.add(new Entity(transformComponent, fpsDisplayComponent));
    }

    private void AddTiltIndicatorEntity(final Entity playerEntity) {
        float margin = 5f;
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(VIEWPORT_WIDTH / 2 - Tiles.rodRight.getWidth() - margin,
                VIEWPORT_HEIGHT / 2 - Tiles.rodRight.getHeight() - margin).build();

        SpriteComponent spriteComponent = new SpriteComponent();
        SizeComponent sizeComponent = SizeComponent.from(Tiles.rodRight);
        TiltDisplayComponent tiltDisplayComponent = new TiltDisplayComponent(Tiles.rodRight, playerEntity);
        entities.add(new Entity(spriteComponent, sizeComponent, transformComponent, tiltDisplayComponent));
    }

}
