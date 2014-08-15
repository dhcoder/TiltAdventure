package tiltadv;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.entity.Component;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.OscillationBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.collision.EnemyCollisionComponent;
import tiltadv.components.collision.ObstacleCollisionComponent;
import tiltadv.components.collision.PlayerCollisionComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.MovingUnitDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.input.AccelerometerComponent;
import tiltadv.components.input.KeyboardComponent;
import tiltadv.components.input.TiltComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.components.model.TransformComponent;
import tiltadv.globals.DevSettings;
import tiltadv.globals.Events;
import tiltadv.globals.Settings;
import tiltadv.globals.Tiles;
import tiltadv.memory.Pools;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

public final class GdxApplication extends ApplicationAdapter {

    private static final int VIEWPORT_HEIGHT = 240;
    private static final int VIEWPORT_WIDTH = 320;

    // When you hit a breakpoint while debugging an app, or if the phone you're using is just simply being slow, the
    // delta times between frames can be HUGE. Let's clamp to a reasonable max here. This also prevents physics update
    // logic from dealing with time steps that are too large (at which point, objects start going through walls, etc.)
    private static final float MAX_DELTA_TIME_SECS = 1f / 30f;

    private BitmapFont font;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private List<Entity> entities;
    private CollisionSystem collisionSystem;

    @Override
    public void create() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();
        if (Settings.IN_DEV_MODE) {
            shapeRenderer = new ShapeRenderer();
        }
        font = new BitmapFont();
        initializeServices();
        initializeEntities();

        Gdx.input.setInputProcessor(new InputAdapter() {
            private int numFingersDown;

            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                if (numFingersDown == 0) {
                    Events.onScreenTouchDown.fire(Gdx.input);
                }
                ++numFingersDown;
                return false;
            }

            @Override
            public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
                --numFingersDown;
                if (numFingersDown == 0) {
                    Events.onScreenTouchUp.fire(Gdx.input);
                }
                return false;
            }
        });
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

        if (Settings.IN_DEV_MODE && DevSettings.SHOW_COLLISION_SHAPES) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            collisionSystem.render(shapeRenderer);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }

        batch.dispose();
        font.dispose();
        Tiles.dispose();

        if (Settings.IN_DEV_MODE) {
            shapeRenderer.dispose();
        }
    }

    private void initializeServices() {
        collisionSystem = new CollisionSystem(200);
        Services.register(CollisionSystem.class, collisionSystem);
    }

    private void initializeEntities() {
        entities = new ArrayList<Entity>();
        Entity playerEntity = addPlayerEntity();

        addOctoEnemies();
        addMovingRockEntities();
        addTiltIndicatorEntity(playerEntity);
        addFpsEntity();
        addBoundaryWalls();
    }

    private void addOctoEnemies() {
        addOctoEnemy(-80, 20);
        addOctoEnemy(-30, 20);
        addOctoEnemy(-80, 80);
        addOctoEnemy(-30, 80);
        addOctoEnemy(80, 20);
        addOctoEnemy(30, 20);
        addOctoEnemy(30, 80);
        addOctoEnemy(80, 80);
        addOctoEnemy(-80, -20);
        addOctoEnemy(-30, -20);
        addOctoEnemy(-80, -80);
        addOctoEnemy(-30, -80);
        addOctoEnemy(30, -80);
        addOctoEnemy(80, -80);
        addOctoEnemy(30, -20);
        addOctoEnemy(80, -20);
    }

    private void addBoundaryWalls() {
        float screenW = VIEWPORT_WIDTH;
        float screenH = VIEWPORT_HEIGHT;
        float halfScreenW = screenW / 2f;
        float halfScreenH = screenH / 2f;
        float top = halfScreenH;
        float bottom = -halfScreenH;
        float left = -halfScreenW;
        float right = halfScreenW;
        float wallSize = 5f;
        entities.add(new Entity(new TransformComponent.Builder().setTranslate(left, 0f).build(),
            new ObstacleCollisionComponent(new Rectangle(wallSize, screenH))));
        entities.add(new Entity(new TransformComponent.Builder().setTranslate(right, 0f).build(),
            new ObstacleCollisionComponent(new Rectangle(wallSize, screenH))));
        entities.add(new Entity(new TransformComponent.Builder().setTranslate(0f, bottom).build(),
            new ObstacleCollisionComponent(new Rectangle(screenW, wallSize))));
        entities.add(new Entity(new TransformComponent.Builder().setTranslate(0f, top).build(),
            new ObstacleCollisionComponent(new Rectangle(screenW, wallSize))));
    }

    private void addMovingRockEntities() {
        final int numRocks = 4;
        final float scaleX = 120;
        final float scaleY = 90;
        final float percent = .2f;
        for (int i = 0; i < numRocks; ++i) {
            float circleDistance = (float)i / (float)numRocks * Angle.TWO_PI;
            float xTo = scaleX * cos(circleDistance);
            float yTo = scaleY * sin(circleDistance);
            float xFrom = xTo * percent;
            float yFrom = yTo * percent;
            AddMovingRockEntity(xTo, yTo, xFrom, yFrom);
        }
    }

    private void update() {
        Duration elapsedTime = Pools.durations.grabNew();
        elapsedTime.setSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));
        int numEntities = entities.size(); // Simple iteration to avoid Iterator allocation
        for (int i = 0; i < numEntities; ++i) {
            entities.get(i).update(elapsedTime);
        }
        Pools.durations.free(elapsedTime);

        collisionSystem.triggerCollisions();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        if (Settings.IN_DEV_MODE) {
            shapeRenderer.setProjectionMatrix(camera.combined);
        }
    }

    private Entity addPlayerEntity() {
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
        components.add(new MovingUnitDisplayComponent(animUp, animDown, animLeft, animRight));
        components.add(new PlayerCollisionComponent(new Circle(Tiles.PLAYERUP1.getRegionWidth() / 2)));

        Entity playerEntity = new Entity(components);
        entities.add(playerEntity);

        return playerEntity;
    }

    private void addOctoEnemy(final float x, final float y) {
        Duration animDuration = Duration.fromSeconds(.1f);
        Animation animUp = new Animation(animDuration.getSeconds(), Tiles.OCTOUP1, Tiles.OCTOUP2);
        animUp.setPlayMode(Animation.PlayMode.LOOP);
        Animation animDown = new Animation(animDuration.getSeconds(), Tiles.OCTODOWN1, Tiles.OCTODOWN2);
        animDown.setPlayMode(Animation.PlayMode.LOOP);
        Animation animLeft = new Animation(animDuration.getSeconds(), Tiles.OCTOLEFT1, Tiles.OCTOLEFT2);
        animLeft.setPlayMode(Animation.PlayMode.LOOP);
        Animation animRight = new Animation(animDuration.getSeconds(), Tiles.OCTORIGHT1, Tiles.OCTORIGHT2);
        animRight.setPlayMode(Animation.PlayMode.LOOP);

        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent());
        components.add(SizeComponent.from(Tiles.OCTODOWN1));
        components.add(new TransformComponent.Builder().setTranslate(x, y).build());
        components.add(new MotionComponent());
        components.add(new OctoBehaviorComponent());
        components.add(new MovingUnitDisplayComponent(animUp, animDown, animLeft, animRight));
        components.add(new EnemyCollisionComponent(new Circle(Tiles.OCTOUP1.getRegionWidth() / 2)));

        entities.add(new Entity(components));
    }

    private void AddMovingRockEntity(final float xFrom, final float yFrom, final float xTo, final float yTo) {
        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent(Tiles.ROCK));
        components.add(SizeComponent.from(Tiles.ROCK));
        components.add(new TransformComponent());
        components.add(new OscillationBehaviorComponent(xFrom, yFrom, xTo, yTo, Duration.fromSeconds(2f)));
        components.add(new ObstacleCollisionComponent(
            new Rectangle(Tiles.ROCK.getRegionWidth() / 2, Tiles.ROCK.getRegionHeight() / 2)));

        final Entity rockEntity = new Entity(components);
        entities.add(rockEntity);
    }

    private void addFpsEntity() {
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()).build();
        FpsDisplayComponent fpsDisplayComponent = new FpsDisplayComponent(font);
        entities.add(new Entity(transformComponent, fpsDisplayComponent));
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        float margin = 5f;
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(VIEWPORT_WIDTH / 2 - Tiles.RODRIGHT.getRegionWidth() - margin,
                VIEWPORT_HEIGHT / 2 - Tiles.RODRIGHT.getRegionHeight() - margin).build();

        SpriteComponent spriteComponent = new SpriteComponent();
        SizeComponent sizeComponent = SizeComponent.from(Tiles.RODRIGHT);
        TiltDisplayComponent tiltDisplayComponent = new TiltDisplayComponent(Tiles.RODRIGHT, playerEntity);
        entities.add(new Entity(spriteComponent, sizeComponent, transformComponent, tiltDisplayComponent));
    }

}
