package tiltadv;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.OscillationBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.collision.EnemyCollisionComponent;
import tiltadv.components.collision.ObstacleCollisionComponent;
import tiltadv.components.collision.PlayerCollisionComponent;
import tiltadv.components.display.CharacterDisplayComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.components.model.TiltComponent;
import tiltadv.components.model.TransformComponent;
import tiltadv.globals.*;
import tiltadv.memory.Pools;

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

    private CollisionSystem collisionSystem;

    private EntityManager entities;

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
        entities.render(batch);
        batch.end();

        if (Settings.IN_DEV_MODE && DevSettings.SHOW_COLLISION_SHAPES) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            collisionSystem.render(shapeRenderer);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
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
        entities = new EntityManager(200);
        addOctoEnemies();

        Entity playerEntity = addPlayerEntity();
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
        float wallSize = .5f;

        Entity wallLeft = entities.newEntity();
        Entity wallRight = entities.newEntity();
        Entity wallBottom = entities.newEntity();
        Entity wallTop = entities.newEntity();

        wallLeft.addComponent(new TransformComponent.Builder().setTranslate(left, 0f).build());
        wallLeft.addComponent(new ObstacleCollisionComponent(new Rectangle(wallSize, screenH)));

        wallRight.addComponent(new TransformComponent.Builder().setTranslate(right, 0f).build());
        wallRight.addComponent(new ObstacleCollisionComponent(new Rectangle(wallSize, screenH)));

        wallBottom.addComponent(new TransformComponent.Builder().setTranslate(0f, bottom).build());
        wallBottom.addComponent(new ObstacleCollisionComponent(new Rectangle(screenW, wallSize)));

        wallTop.addComponent(new TransformComponent.Builder().setTranslate(0f, top).build());
        wallTop.addComponent(new ObstacleCollisionComponent(new Rectangle(screenW, wallSize)));
    }

    private void addMovingRockEntities() {
        final int numRocks = 4;
        final float scaleX = 120;
        final float scaleY = 90;
        final float percent = .4f;
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
        entities.update(elapsedTime);
        Pools.durations.free(elapsedTime);

        collisionSystem.triggerCollisions();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        if (Settings.IN_DEV_MODE) {
            shapeRenderer.setProjectionMatrix(camera.combined);
        }
    }

    private Entity addPlayerEntity() {
        Entity playerEntity = entities.newEntity();
        playerEntity.addComponent(new SpriteComponent());
        SizeComponent sizeComponent = SizeComponent.from(Tiles.LINKDOWN1);
        playerEntity.addComponent(sizeComponent);
        playerEntity.addComponent(new TransformComponent());
        playerEntity.addComponent(new MotionComponent());
        playerEntity.addComponent(new TiltComponent());
        playerEntity.addComponent(
            Gdx.app.getType() == ApplicationType.Android ? new AccelerometerInputComponent() :
                new KeyboardInputComponent());
        playerEntity.addComponent(new PlayerBehaviorComponent());
        playerEntity.addComponent(
            new CharacterDisplayComponent(Animations.PLAYER_S, Animations.PLAYER_SE, Animations.PLAYER_E,
                Animations.PLAYER_NE, Animations.PLAYER_N, Animations.PLAYER_NW, Animations.PLAYER_W,
                Animations.PLAYER_SW));
        playerEntity.addComponent(new PlayerCollisionComponent(new Circle(sizeComponent.getSize().x / 2)));

        return playerEntity;
    }

    private void addOctoEnemy(final float x, final float y) {
        Entity octoEnemy = entities.newEntity();

        octoEnemy.addComponent(new SpriteComponent());
        octoEnemy.addComponent(SizeComponent.from(Tiles.OCTODOWN1));
        octoEnemy.addComponent(new TransformComponent.Builder().setTranslate(x, y).build());
        octoEnemy.addComponent(new MotionComponent());
        octoEnemy.addComponent(new OctoBehaviorComponent());
        octoEnemy.addComponent(
            new CharacterDisplayComponent(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP,
                Animations.OCTOLEFT));
        octoEnemy.addComponent(new EnemyCollisionComponent(new Circle(Tiles.OCTOUP1.getRegionWidth() / 2)));
    }

    private void AddMovingRockEntity(final float xFrom, final float yFrom, final float xTo, final float yTo) {

        Entity rockEntity = entities.newEntity();
        rockEntity.addComponent(new SpriteComponent(Tiles.ROCK));
        rockEntity.addComponent(SizeComponent.from(Tiles.ROCK));
        rockEntity.addComponent(new TransformComponent());
        rockEntity.addComponent(new OscillationBehaviorComponent(xFrom, yFrom, xTo, yTo, Duration.fromSeconds(2f)));
        rockEntity.addComponent(new ObstacleCollisionComponent(
            new Rectangle(Tiles.ROCK.getRegionWidth() / 2, Tiles.ROCK.getRegionHeight() / 2)));
    }

    private void addFpsEntity() {
        Entity fpsEntity = entities.newEntity();
        fpsEntity.addComponent(new TransformComponent.Builder()
            .setTranslate(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()).build());
        fpsEntity.addComponent(new FpsDisplayComponent(font));
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntity();

        final float MARGIN = 5f;
        tiltIndicatorEntity.addComponent(new TransformComponent.Builder()
            .setTranslate(VIEWPORT_WIDTH / 2 - Tiles.RODRIGHT.getRegionWidth() - MARGIN,
                VIEWPORT_HEIGHT / 2 - Tiles.RODRIGHT.getRegionHeight() - MARGIN).build());
        tiltIndicatorEntity.addComponent(new SpriteComponent());
        tiltIndicatorEntity.addComponent(SizeComponent.from(Tiles.RODRIGHT));
        tiltIndicatorEntity.addComponent(new TiltDisplayComponent(Tiles.RODRIGHT, playerEntity));
    }
}
