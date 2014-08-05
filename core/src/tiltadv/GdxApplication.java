package tiltadv;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.collision.CollisionSystem;
import dhcoder.support.collision.shape.Circle;
import dhcoder.support.collision.shape.Rectangle;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.entity.Component;
import tiltadv.entity.Entity;
import tiltadv.entity.components.behavior.PlayerBehaviorComponent;
import tiltadv.entity.components.collision.ObstacleCollisionComponent;
import tiltadv.entity.components.collision.PlayerCollisionComponent;
import tiltadv.entity.components.display.FpsDisplayComponent;
import tiltadv.entity.components.display.PlayerDisplayComponent;
import tiltadv.entity.components.display.SpriteComponent;
import tiltadv.entity.components.display.TiltDisplayComponent;
import tiltadv.entity.components.input.AccelerometerComponent;
import tiltadv.entity.components.input.KeyboardComponent;
import tiltadv.entity.components.input.TiltComponent;
import tiltadv.entity.components.model.MotionComponent;
import tiltadv.entity.components.model.SizeComponent;
import tiltadv.entity.components.model.TransformComponent;
import tiltadv.globals.Tiles;
import tiltadv.memory.Pools;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

public final class GdxApplication extends ApplicationAdapter {

    private static final int VIEWPORT_HEIGHT = 240;
    private static final int VIEWPORT_WIDTH = 320;

    // When you hit a breakpoint while debugging an app, this causes the delta time to be huge between the next frame
    // and this one. Clamping to a reasonable max value works around this issue. A max here also prevents physics update
    // logic from dealing with time steps that are too large (at which point, objects start going through walls, etc.)
    private static final float MAX_DELTA_TIME_SECS = 1f / 20f;

    private BitmapFont font;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private List<Entity> entities;
    private CollisionSystem collisionSystem;

    @Override
    public void create() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();
        font = new BitmapFont();
        initializeServices();
        initializeEntities();
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

    private void initializeServices() {
        collisionSystem = new CollisionSystem(200);
        Services.register(CollisionSystem.class, collisionSystem);
    }

    private void initializeEntities() {
        entities = new ArrayList<Entity>();
        Entity playerEntity = AddPlayerEntity();

        int numRocks = 7;
        for (int i = 0; i < numRocks; ++i) {
            float circleDistance = (float)i / (float)numRocks * Angle.TWO_PI;
            float xScale = 120;
            float yScale = 90;
            AddRockEntity(xScale * cos(circleDistance), yScale * sin(circleDistance));
        }

        AddTiltIndicatorEntity(playerEntity);
        AddFpsEntity();
    }

    private void update() {
        {
            Duration elapsedTime = Pools.durations.grabNew();
            elapsedTime.setSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));

            int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation
            for (int i = 0; i < numEntities; ++i) {
                entities.get(i).update(elapsedTime);
            }
            Pools.durations.free(elapsedTime);
        }
        collisionSystem.triggerCollisions();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private Entity AddPlayerEntity() {
        Duration animDuration = Duration.fromSeconds(.1f);
        Animation animUp = new Animation(animDuration.getSeconds(), Tiles.PLAYERUP1, Tiles.PLAYERUP2);
        animUp.setPlayMode(Animation.PlayMode.LOOP);
        Animation animDown = new Animation(animDuration.getSeconds(), Tiles.PLAYERDOWN1, Tiles.PLAYERDOWN2);
        animDown.setPlayMode(Animation.PlayMode.LOOP);
        Animation animLeft = new Animation(animDuration.getSeconds(), Tiles.PLAYERLEFT1, Tiles.PLAYERLEFT2);
        animLeft.setPlayMode(Animation.PlayMode.LOOP);
        Animation animRight = new Animation(animDuration.getSeconds(), Tiles.PLAYERRIGHT1, Tiles.PLAYERRIGHT2);
        animRight.setPlayMode(Animation.PlayMode.LOOP);

        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent());
        components.add(SizeComponent.from(Tiles.PLAYERDOWN1));
        components.add(new TransformComponent());
        components.add(new MotionComponent());
        components.add(new TiltComponent());
        components.add(Gdx.app.getType() == Application.ApplicationType.Android ? new AccelerometerComponent() :
            new KeyboardComponent());
        components.add(new PlayerBehaviorComponent());
        components.add(new PlayerDisplayComponent(animUp, animDown, animLeft, animRight));
        components.add(new PlayerCollisionComponent(new Rectangle(Tiles.PLAYERUP1.getWidth() / 2, Tiles.PLAYERUP1.getHeight() / 2)));

        Entity playerEntity = new Entity(components);
        entities.add(playerEntity);

        return playerEntity;
    }

    private void AddRockEntity(final float x, final float y) {
        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent(Tiles.ROCK));
        components.add(SizeComponent.from(Tiles.ROCK));
        components.add(new TransformComponent.Builder().setTranslate(x, y).build());
        components.add(new ObstacleCollisionComponent(new Rectangle(Tiles.ROCK.getWidth() / 2, Tiles.ROCK.getHeight() / 2)));
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
            .setTranslate(VIEWPORT_WIDTH / 2 - Tiles.RODRIGHT.getWidth() - margin,
                VIEWPORT_HEIGHT / 2 - Tiles.RODRIGHT.getHeight() - margin).build();

        SpriteComponent spriteComponent = new SpriteComponent();
        SizeComponent sizeComponent = SizeComponent.from(Tiles.RODRIGHT);
        TiltDisplayComponent tiltDisplayComponent = new TiltDisplayComponent(Tiles.RODRIGHT, playerEntity);
        entities.add(new Entity(spriteComponent, sizeComponent, transformComponent, tiltDisplayComponent));
    }

}
