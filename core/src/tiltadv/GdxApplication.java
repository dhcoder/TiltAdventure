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
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.OscillationBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.behavior.SwordBehaviorComponent;
import tiltadv.components.collision.EnemyCollisionComponent;
import tiltadv.components.collision.EnemyProjectileCollisionComponent;
import tiltadv.components.collision.ObstacleCollisionComponent;
import tiltadv.components.collision.PlayerCollisionComponent;
import tiltadv.components.collision.PlayerSensorCollisionComponent;
import tiltadv.components.collision.SwordCollisionComponent;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.DefenseComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.display.CharacterDisplayComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.components.model.TiltComponent;
import tiltadv.components.model.TransformComponent;
import tiltadv.globals.Animations;
import tiltadv.globals.DevSettings;
import tiltadv.globals.EntityId;
import tiltadv.globals.Events;
import tiltadv.globals.Services;
import tiltadv.globals.Tiles;
import tiltadv.input.Vibrator;
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
    private static final Duration ENEMY_INVINCIBILITY_DURATION = Duration.zero();
    private BitmapFont font;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private CollisionSystem collisionSystem;
    private EntityManager entities;
    private Shape octoBounds;
    private Shape playerBounds;
    private Shape playerSensorBounds;
    private Shape playerSwordBounds;
    private Shape octoRockBounds;
    private Shape boulderBounds;

    public void create() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();
        if (DevSettings.IN_DEV_MODE) {
            shapeRenderer = new ShapeRenderer();
            Pool.RUN_SANITY_CHECKS = true;
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

        if (DevSettings.IN_DEV_MODE && DevSettings.SHOW_COLLISION_SHAPES) {
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

        if (DevSettings.IN_DEV_MODE) {
            shapeRenderer.dispose();
        }
    }

    private void initializeServices() {
        collisionSystem = new CollisionSystem(ENTITY_COUNT);
        Services.register(CollisionSystem.class, collisionSystem);

        Services.register(Vibrator.class, new Vibrator());
    }

    private void initializeEntities() {

        entities = new EntityManager(ENTITY_COUNT);

        octoBounds = new Circle(Tiles.OCTOUP1.getRegionWidth() / 2f);
        playerBounds = new Circle(Tiles.LINKUP1.getRegionWidth() / 2f);
        playerSensorBounds = new Circle(Tiles.SWORDRIGHT.getRegionWidth() / 2f);
        playerSwordBounds = new Circle(5f);
        octoRockBounds = new Circle(Tiles.ROCK.getRegionWidth() / 2f);
        boulderBounds = new Circle(Tiles.BOULDER.getRegionWidth() / 2f);

        entities.preallocate(TransformComponent.class, ENTITY_COUNT);
        entities.preallocate(MotionComponent.class, ENTITY_COUNT);
        entities.preallocate(SizeComponent.class, ENTITY_COUNT);
        entities.preallocate(SpriteComponent.class, ENTITY_COUNT);

        entities.registerTemplate(EntityId.PLAYER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(KnockbackComponent.class);
                entity.addComponent(DefenseComponent.class);
                entity.addComponent(TransformComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(SizeComponent.class).setSizeFrom(Tiles.LINKDOWN1);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(TiltComponent.class);
                entity.addComponent(Gdx.app.getType() == ApplicationType.Android ? AccelerometerInputComponent.class :
                    KeyboardInputComponent.class);
                entity.addComponent(PlayerBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(10);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.PLAYER_S, Animations.PLAYER_E, Animations.PLAYER_N, Animations.PLAYER_W);
                entity.addComponent(PlayerCollisionComponent.class).setShape(playerBounds);
                entity.addComponent(PlayerSensorCollisionComponent.class).setShape(playerSensorBounds);
            }
        });

        entities.registerTemplate(EntityId.PLAYER_SWORD, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(ParentComponent.class); // Set by the PlayerBehaviorComponent
                entity.addComponent(SwordCollisionComponent.class).setShape(playerSwordBounds);
                entity.addComponent(SwordBehaviorComponent.class);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
                entity.addComponent(TransformComponent.class);
                entity.addComponent(SizeComponent.class).setSizeFrom(Tiles.SWORDRIGHT);
            }
        });

        entities.registerTemplate(EntityId.OCTO, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(KnockbackComponent.class).setMultiplier(2f);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(DefenseComponent.class);
                entity.addComponent(TransformComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(SizeComponent.class).setSizeFrom(Tiles.OCTODOWN1);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(OctoBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(3)
                    .setInvicibilityDuration(ENEMY_INVINCIBILITY_DURATION);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP, Animations.OCTOLEFT);
                entity.addComponent(EnemyCollisionComponent.class).setShape(octoBounds);
            }
        });

        entities.registerTemplate(EntityId.OCTO_ROCK, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(AttackComponent.class);
                entity.addComponent(TransformComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(SizeComponent.class).setSizeFrom(Tiles.ROCK);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(EnemyProjectileCollisionComponent.class).setShape(octoRockBounds);
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
        float halfScreenW = (float)VIEWPORT_WIDTH / 2f;
        float halfScreenH = (float)VIEWPORT_HEIGHT / 2f;
        float halfWallSize = 20f;
        float top = halfScreenH + halfWallSize;
        float bottom = -halfScreenH - halfWallSize;
        float left = -halfScreenW - halfWallSize;
        float right = halfScreenW + halfWallSize;

        Entity wallLeft = entities.newEntity();
        Entity wallRight = entities.newEntity();
        Entity wallBottom = entities.newEntity();
        Entity wallTop = entities.newEntity();

        Vector2 wallPos = Pools.vector2s.grabNew();
        final Shape verticalWall = new Rectangle(halfWallSize, halfScreenH);
        final Shape horizontalWall = new Rectangle(halfScreenW, halfWallSize);

        wallPos.set(left, 0f);
        wallLeft.addComponent(TransformComponent.class).setTranslate(wallPos);
        wallLeft.addComponent(ObstacleCollisionComponent.class).setShape(verticalWall);

        wallPos.set(right, 0f);
        wallRight.addComponent(TransformComponent.class).setTranslate(wallPos);
        wallRight.addComponent(ObstacleCollisionComponent.class).setShape(verticalWall);

        wallPos.set(0f, bottom);
        wallBottom.addComponent(TransformComponent.class).setTranslate(wallPos);
        wallBottom.addComponent(ObstacleCollisionComponent.class).setShape(horizontalWall);

        wallPos.set(0f, top);
        wallTop.addComponent(TransformComponent.class).setTranslate(wallPos);
        wallTop.addComponent(ObstacleCollisionComponent.class).setShape(horizontalWall);

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
        int mark = Pools.durations.mark();
        Duration elapsedTime = Pools.durations.grabNew();
        elapsedTime.setSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));
        entities.update(elapsedTime);
        Pools.durations.freeToMark(mark);

        collisionSystem.triggerCollisions();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        if (DevSettings.IN_DEV_MODE) {
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
        boulderEntity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.BOULDER);
        boulderEntity.addComponent(SizeComponent.class).setSizeFrom(Tiles.BOULDER);
        boulderEntity.addComponent(TransformComponent.class);
        boulderEntity.addComponent(OscillationBehaviorComponent.class)
            .set(new Vector2(xFrom, yFrom), new Vector2(xTo, yTo), Duration.fromSeconds(2f));
        boulderEntity.addComponent(ObstacleCollisionComponent.class).setShape(boulderBounds);
    }

    private void addFpsEntity() {
        Entity fpsEntity = entities.newEntity();
        fpsEntity.addComponent(TransformComponent.class)
            .setTranslate(new Vector2(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()));
        fpsEntity.addComponent(FpsDisplayComponent.class).set(font);
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntity();

        final float MARGIN = 5f;
        tiltIndicatorEntity.addComponent(TransformComponent.class).setTranslate(
            new Vector2(VIEWPORT_WIDTH / 2 - Tiles.RODRIGHT.getRegionWidth() - MARGIN,
                VIEWPORT_HEIGHT / 2 - Tiles.RODRIGHT.getRegionHeight() - MARGIN));
        tiltIndicatorEntity.addComponent(SpriteComponent.class);
        tiltIndicatorEntity.addComponent(SizeComponent.class).setSizeFrom(Tiles.RODRIGHT);
        tiltIndicatorEntity.addComponent(TiltDisplayComponent.class).set(Tiles.RODRIGHT, playerEntity);
    }
}
