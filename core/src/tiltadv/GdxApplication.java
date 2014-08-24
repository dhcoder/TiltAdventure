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
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.OscillationBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.collision.EnemyCollisionComponent;
import tiltadv.components.collision.EnemyProjectileCollisionComponent;
import tiltadv.components.collision.ObstacleCollisionComponent;
import tiltadv.components.collision.PlayerCollisionComponent;
import tiltadv.components.display.CharacterDisplayComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.components.model.*;
import tiltadv.globals.*;
import tiltadv.memory.Pools;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

public final class GdxApplication extends ApplicationAdapter {

    public static final int ENTITY_COUNT = 200;
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

    private Shape octoBounds;
    private Shape playerBounds;
    private Shape octoRockBounds;
    private Shape boulderBounds;

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
        collisionSystem = new CollisionSystem(ENTITY_COUNT);
        Services.register(CollisionSystem.class, collisionSystem);

        entities = new EntityManager(ENTITY_COUNT);
        Services.register(EntityManager.class, entities);
    }

    private void initializeEntities() {

        octoBounds = new Circle(Tiles.OCTOUP1.getRegionWidth() / 2f);
        playerBounds = new Circle(Tiles.LINKUP1.getRegionWidth() / 2f);
        octoRockBounds = new Circle(Tiles.ROCK.getRegionWidth() / 2f);
        boulderBounds = new Rectangle(Tiles.BOULDER.getRegionWidth() / 2f, Tiles.BOULDER.getRegionHeight() / 2f);

        entities.preallocate(TransformComponent.class, ENTITY_COUNT);
        entities.preallocate(MotionComponent.class, ENTITY_COUNT);
        entities.preallocate(SizeComponent.class, ENTITY_COUNT);
        entities.preallocate(SpriteComponent.class, ENTITY_COUNT);
        entities.preallocate(EnemyProjectileCollisionComponent.class, 20); // TODO: DON'T CHECK IN

        entities.registerTemplate(EntityId.PLAYER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(entities.newComponent(TransformComponent.class));
                entity.addComponent(entities.newComponent(SpriteComponent.class));
                entity.addComponent(entities.newComponent(HeadingComponent.class));
                SizeComponent sizeComponent = entities.newComponent(SizeComponent.class).setSizeFrom(Tiles.LINKDOWN1);
                entity.addComponent(sizeComponent);
                entity.addComponent(entities.newComponent(MotionComponent.class));
                entity.addComponent(entities.newComponent(TiltComponent.class));
                entity.addComponent(Gdx.app.getType() == ApplicationType.Android ?
                    entities.newComponent(AccelerometerInputComponent.class) :
                    entities.newComponent(KeyboardInputComponent.class));
                entity.addComponent(entities.newComponent(PlayerBehaviorComponent.class));
                entity.addComponent(entities.newComponent(CharacterDisplayComponent.class)
                    .set(Animations.PLAYER_S, Animations.PLAYER_E, Animations.PLAYER_N, Animations.PLAYER_W));
                entity.addComponent(entities.newComponent(PlayerCollisionComponent.class).setShape(playerBounds));
            }
        });

        entities.registerTemplate(EntityId.OCTO, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(entities.newComponent(TransformComponent.class));
                entity.addComponent(entities.newComponent(SpriteComponent.class));
                entity.addComponent(entities.newComponent(HeadingComponent.class));
                entity.addComponent(entities.newComponent(SizeComponent.class).setSizeFrom(Tiles.OCTODOWN1));
                entity.addComponent(entities.newComponent(MotionComponent.class));
                entity.addComponent(entities.newComponent(OctoBehaviorComponent.class));
                entity.addComponent(entities.newComponent(CharacterDisplayComponent.class)
                    .set(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP, Animations.OCTOLEFT));
                entity.addComponent(entities.newComponent(EnemyCollisionComponent.class).setShape(octoBounds));
            }
        });

        entities.registerTemplate(EntityId.OCTO_ROCK, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(entities.newComponent(TransformComponent.class));
                entity.addComponent(entities.newComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK));
                entity.addComponent(entities.newComponent(HeadingComponent.class));
                entity.addComponent(entities.newComponent(SizeComponent.class).setSizeFrom(Tiles.ROCK));
                entity.addComponent(entities.newComponent(MotionComponent.class));
                entity.addComponent(
                    entities.newComponent(EnemyProjectileCollisionComponent.class).setShape(octoRockBounds));
            }
        });

        addOctoEnemies();

        Entity playerEntity = addPlayerEntity();
        addMovingBoulderEntities();
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

        Vector2 wallPos = Pools.vector2s.grabNew();
        final Shape verticalWall = new Rectangle(wallSize, screenH);
        final Shape horizontalWall = new Rectangle(screenW, wallSize);

        wallPos.set(left, 0f);
        wallLeft.addComponent(entities.newComponent(TransformComponent.class).setTranslate(wallPos));
        wallLeft.addComponent(entities.newComponent(ObstacleCollisionComponent.class).setShape(verticalWall));

        wallPos.set(right, 0f);
        wallRight.addComponent(entities.newComponent(TransformComponent.class).setTranslate(wallPos));
        wallRight.addComponent(entities.newComponent(ObstacleCollisionComponent.class).setShape(verticalWall));

        wallPos.set(0f, bottom);
        wallBottom.addComponent(entities.newComponent(TransformComponent.class).setTranslate(wallPos));
        wallBottom.addComponent(entities.newComponent(ObstacleCollisionComponent.class).setShape(horizontalWall));

        wallPos.set(0f, top);
        wallTop.addComponent(entities.newComponent(TransformComponent.class).setTranslate(wallPos));
        wallTop.addComponent(entities.newComponent(ObstacleCollisionComponent.class).setShape(horizontalWall));

        Pools.vector2s.free(wallPos);
    }

    private void addMovingBoulderEntities() {
        final int numBoulders = 4;
        final float scaleX = 120;
        final float scaleY = 90;
        final float percent = .4f;
        for (int i = 0; i < numBoulders; ++i) {
            float circleDistance = (float)i / (float)numBoulders * Angle.TWO_PI;
            float xTo = scaleX * cos(circleDistance);
            float yTo = scaleY * sin(circleDistance);
            float xFrom = xTo * percent;
            float yFrom = yTo * percent;
            AddMovingBoulderEntity(xTo, yTo, xFrom, yFrom);
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
        return entities.newEntityFromTemplate(EntityId.PLAYER);
    }

    private void addOctoEnemy(final float x, final float y) {
        Entity octoEnemy = entities.newEntityFromTemplate(EntityId.OCTO);
        TransformComponent transformComponent = octoEnemy.requireComponent(TransformComponent.class);

        Vector2 position = Pools.vector2s.grabNew().set(x, y);
        transformComponent.setTranslate(position);
        Pools.vector2s.free(position);
    }

    private void AddMovingBoulderEntity(final float xFrom, final float yFrom, final float xTo, final float yTo) {

        Entity boulderEntity = entities.newEntity();
        boulderEntity.addComponent(entities.newComponent(SpriteComponent.class).setTextureRegion(Tiles.BOULDER));
        boulderEntity.addComponent(entities.newComponent(SizeComponent.class).setSizeFrom(Tiles.BOULDER));
        boulderEntity.addComponent(entities.newComponent(TransformComponent.class));
        boulderEntity.addComponent(entities.newComponent(OscillationBehaviorComponent.class)
            .set(new Vector2(xFrom, yFrom), new Vector2(xTo, yTo), Duration.fromSeconds(2f)));
        boulderEntity.addComponent(entities.newComponent(ObstacleCollisionComponent.class)
            .setShape(new Rectangle(Tiles.BOULDER.getRegionWidth() / 2, Tiles.BOULDER.getRegionHeight() / 2)));
    }

    private void addFpsEntity() {
        Entity fpsEntity = entities.newEntity();
        fpsEntity.addComponent(entities.newComponent(TransformComponent.class)
            .setTranslate(new Vector2(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight())));
        fpsEntity.addComponent(entities.newComponent(FpsDisplayComponent.class).set(font));
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntity();

        final float MARGIN = 5f;
        tiltIndicatorEntity.addComponent(entities.newComponent(TransformComponent.class).setTranslate(
            new Vector2(VIEWPORT_WIDTH / 2 - Tiles.RODRIGHT.getRegionWidth() - MARGIN,
                VIEWPORT_HEIGHT / 2 - Tiles.RODRIGHT.getRegionHeight() - MARGIN)));
        tiltIndicatorEntity.addComponent(entities.newComponent(SpriteComponent.class));
        tiltIndicatorEntity.addComponent(entities.newComponent(SizeComponent.class).setSizeFrom(Tiles.RODRIGHT));
        tiltIndicatorEntity.addComponent(new TiltDisplayComponent(Tiles.RODRIGHT, playerEntity));
    }
}
