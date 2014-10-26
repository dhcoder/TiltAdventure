package tiltadv;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import dhcoder.libgdx.collision.CollisionSystem;
import dhcoder.libgdx.collision.shape.Circle;
import dhcoder.libgdx.collision.shape.Rectangle;
import dhcoder.libgdx.collision.shape.Shape;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.math.Angle;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;
import tiltadv.assets.AnimationDatastore;
import tiltadv.assets.ImageDatastore;
import tiltadv.assets.TileDatastore;
import tiltadv.assets.TilesetDatastore;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.OscillationBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.behavior.PlayerSensorBehaviorComponent;
import tiltadv.components.behavior.SwordBehaviorComponent;
import tiltadv.components.body.FollowCameraComponent;
import tiltadv.components.body.HeadingComponent;
import tiltadv.components.body.MotionComponent;
import tiltadv.components.body.PositionComponent;
import tiltadv.components.body.TiltComponent;
import tiltadv.components.collision.EnemyCollisionComponent;
import tiltadv.components.collision.EnemyProjectileCollisionComponent;
import tiltadv.components.collision.GravityWellCollisionComponent;
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
import tiltadv.components.display.TargetDisplayComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.hierarchy.ChildOffsetComponent;
import tiltadv.components.hierarchy.OffsetComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.hierarchy.children.PlayerChildrenComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.components.input.touchables.TargetTouchableComponent;
import tiltadv.globals.Animations;
import tiltadv.globals.DevSettings;
import tiltadv.globals.EntityId;
import tiltadv.globals.Services;
import tiltadv.globals.Tiles;
import tiltadv.input.TouchSystem;
import tiltadv.input.Vibrator;
import tiltadv.memory.Pools;
import tiltadv.serialization.AnimationsLoader;
import tiltadv.serialization.TilesLoader;
import tiltadv.serialization.TilesetLoader;

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
    private static final Duration BOULDER_OSCILLATION_DURATION = Duration.fromSeconds(2f);
    public static final int UI_ELEMENT_COUNT = 200;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private EntityManager entities;
    private CollisionSystem collisionSystem;
    private RenderSystem renderSystem;
    private Shape octoBounds;
    private Shape playerBounds;
    private Shape gravityWellBounds;
    private Shape playerSensorBounds;
    private Shape playerSwordBounds;
    private Shape octoRockBounds;
    private Shape boulderBounds;
    private TouchSystem touchSystem;

    public void create() {
        if (DevSettings.IN_DEV_MODE) {
            shapeRenderer = new ShapeRenderer();
            Pool.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            CollisionSystem.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            ArrayMap.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
        }
        font = new BitmapFont();

        initializeServices();
        initializeAssets();
        initializeEntities();

        Gdx.input.setInputProcessor(new InputAdapter() {
            private int numFingersDown;

            @Override
            public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
                if (numFingersDown == 0) {
                    final Vector3 localPosition = Pools.vector3s.grabNew();
                    localPosition.set(screenX, screenY, 0);
                    renderSystem.getCamera().unproject(localPosition);

                    touchSystem.handleTouch(localPosition.x, localPosition.y);

                    Pools.vector3s.freeCount(1);
                }
                ++numFingersDown;
                return true;
            }

            @Override
            public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
                --numFingersDown;
                return true;
            }
        });
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(1f, .88f, .66f, 1f); // Desert-ish color, for testing!
//        Gdx.gl.glClearColor(.22f, .22f, .22f, 1f); // Grey-ish color, for seeing collision shapes
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderSystem.render();

        if (DevSettings.IN_DEV_MODE && DevSettings.SHOW_COLLISION_SHAPES) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            collisionSystem.render(shapeRenderer);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        Services.get(ImageDatastore.class).dispose();
        renderSystem.dispose();

        if (DevSettings.IN_DEV_MODE) {
            shapeRenderer.dispose();
        }
    }

    private void initializeServices() {
        Services.register(ImageDatastore.class, new ImageDatastore());
        Services.register(TilesetDatastore.class, new TilesetDatastore());
        Services.register(TileDatastore.class, new TileDatastore());
        Services.register(AnimationDatastore.class, new AnimationDatastore());
        Services.register(Json.class, new Json());

        collisionSystem = new CollisionSystem(ENTITY_COUNT);
        Services.register(CollisionSystem.class, collisionSystem);

        // TODO: Tune the application for the best batch size for render system
        // https://github.com/libgdx/libgdx/wiki/Spritebatch,-Textureregions,-and-Sprites#performance-tuning
        renderSystem = new RenderSystem(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, DevSettings.IN_DEV_MODE ? 1000 : 200);
        renderSystem.addLayer(ENTITY_COUNT).setBlending(false).setSorted(false);
        renderSystem.addLayer(ENTITY_COUNT);
        renderSystem.addLayer(10).setUiLayer(true).setSorted(false);
        Services.register(RenderSystem.class, renderSystem);

        touchSystem = new TouchSystem(ENTITY_COUNT / 2);
        Services.register(TouchSystem.class, touchSystem);

        Services.register(Vibrator.class, new Vibrator());

    }

    private void initializeAssets() {
        {
            final FileHandle[] tilesetFiles = Gdx.files.internal("data/tilesets").list();
            for (int i = 0; i < tilesetFiles.length; ++i) {
                TilesetLoader.load(tilesetFiles[i].path());
            }
        }

        {
            final FileHandle[] tileFiles = Gdx.files.internal("data/tiles").list();
            for (int i = 0; i < tileFiles.length; ++i) {
                TilesLoader.load(tileFiles[i].path());
            }
        }

        {
            final FileHandle[] animationFiles = Gdx.files.internal("data/animations").list();
            for (int i = 0; i < animationFiles.length; ++i) {
                AnimationsLoader.load(animationFiles[i].path());
            }
        }
    }

    private void initializeEntities() {

        entities = new EntityManager(ENTITY_COUNT);

        gravityWellBounds = new Circle(8.0f);
        octoBounds = new Circle(8.0f);
        playerBounds = new Circle(6.5f);
        playerSensorBounds = new Circle(Tiles.SENSOR.getRegionWidth() / 2f);
        playerSwordBounds = new Circle(5f);
        octoRockBounds = new Circle(Tiles.ROCK.getRegionWidth() / 2f);
        boulderBounds = new Circle(Tiles.BOULDER.getRegionWidth() / 2f);

        entities.registerTemplate(EntityId.BOUNDARY, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(ObstacleCollisionComponent.class);
            }
        });

        entities.registerTemplate(EntityId.CAMERA_ENTITY, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(FollowCameraComponent.class);
            }
        });

        entities.registerTemplate(EntityId.GRAVITY_WELL, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(GravityWellCollisionComponent.class).setShape(gravityWellBounds);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR);
            }
        });

        entities.registerTemplate(EntityId.PLAYER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PlayerChildrenComponent.class);
                entity.addComponent(KnockbackComponent.class);
                entity.addComponent(DefenseComponent.class);
                entity.addComponent(PositionComponent.class);
                Vector2 offset = Pools.vector2s.grabNew().set(0f, 5f);
                entity.addComponent(SpriteComponent.class).setOffset(offset);
                Pools.vector2s.freeCount(1);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(TiltComponent.class);
                entity.addComponent(Gdx.app.getType() == ApplicationType.Android ? AccelerometerInputComponent.class :
                    KeyboardInputComponent.class);
                entity.addComponent(PlayerBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(10);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.PLAYER_S, Animations.PLAYER_E, Animations.PLAYER_N, Animations.PLAYER_SE,
                        Animations.PLAYER_NW);
                entity.addComponent(PlayerCollisionComponent.class).setShape(playerBounds);
            }
        });

        entities.registerTemplate(EntityId.PLAYER_SWORD, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(ParentComponent.class); // Child of Player Entity
                entity.addComponent(OffsetComponent.class);
                entity.addComponent(ChildOffsetComponent.class);
                entity.addComponent(SwordCollisionComponent.class).setShape(playerSwordBounds);
                entity.addComponent(SwordBehaviorComponent.class);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
                entity.addComponent(PositionComponent.class);
            }
        });

        entities.registerTemplate(EntityId.PLAYER_SENSOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(ParentComponent.class);  // Child of Player Entity
                entity.addComponent(PositionComponent.class);
                entity.addComponent(OffsetComponent.class);
                entity.addComponent(ChildOffsetComponent.class);
                entity.addComponent(PlayerSensorCollisionComponent.class).setShape(playerSensorBounds);
                entity.addComponent(PlayerSensorBehaviorComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR);
            }
        });

        entities.registerTemplate(EntityId.BOULDER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.BOULDER);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(OscillationBehaviorComponent.class);
                entity.addComponent(ObstacleCollisionComponent.class).setShape(boulderBounds);

            }
        });

        entities.registerTemplate(EntityId.OCTO, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(KnockbackComponent.class).setMultiplier(2f);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(DefenseComponent.class);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(TargetTouchableComponent.class);
                entity.addComponent(OctoBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(3)
                    .setInvicibilityDuration(ENEMY_INVINCIBILITY_DURATION);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP);
                entity.addComponent(EnemyCollisionComponent.class).setShape(octoBounds);
            }
        });

        entities.registerTemplate(EntityId.OCTO_ROCK, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(AttackComponent.class);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
                entity.addComponent(HeadingComponent.class);
                entity.addComponent(MotionComponent.class);
                entity.addComponent(EnemyProjectileCollisionComponent.class).setShape(octoRockBounds);
            }
        });

        entities.registerTemplate(EntityId.TARGET_INDICATOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(TargetDisplayComponent.class).setTextureRegion(Tiles.ROCK);
                entity.addComponent(OffsetComponent.class);
            }
        });

        entities.registerTemplate(EntityId.FPS_INDICATOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class)
                    .setPosition(new Vector2(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()));
                entity.addComponent(FpsDisplayComponent.class).set(font);
            }
        });

        entities.registerTemplate(EntityId.TILT_INDICATOR, new EntityManager.EntityCreator() {
                @Override
                public void initialize(final Entity entity) {
                    final float MARGIN = 5f;
                    entity.addComponent(TiltDisplayComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
                    entity.addComponent(PositionComponent.class).setPosition(
                        new Vector2(VIEWPORT_WIDTH / 2 - Tiles.SWORDRIGHT.getRegionWidth() - MARGIN,
                            VIEWPORT_HEIGHT / 2 - Tiles.SWORDRIGHT.getRegionHeight() - MARGIN));
                    entity.addComponent(SpriteComponent.class);
                }
            });


        Entity playerEntity = addPlayerEntity();
//        Vector2 dummyVector = new Vector2();
//        addPlayerEntity().requireComponent(PositionComponent.class).setPosition(dummyVector.set(30, 30));
//        addPlayerEntity().requireComponent(PositionComponent.class).setPosition(dummyVector.set(-30, -30));
//        addGravityWell(0, 0);
//        addGravityWell(50, 20);
//        addGravityWell(-30, 70);
//        addGravityWell(-90, -30);
        addMainCamera(playerEntity);
        addOctoEnemies();
        addMovingBoulderEntities();
        addTiltIndicatorEntity(playerEntity);
        addFpsEntity();
        addBoundaryWalls();
        addTargetEntity();
    }

    private void addMainCamera(final Entity playerEntity) {entities.newEntityFromTemplate(
        EntityId.CAMERA_ENTITY).requireComponent(FollowCameraComponent.class).setTargetEntity(playerEntity);}

    private void addGravityWell(final float x, final float y) {
        Entity gravityWellEntity = entities.newEntityFromTemplate(EntityId.GRAVITY_WELL);

        Vector2 position = Pools.vector2s.grabNew().set(x, y);
        gravityWellEntity.requireComponent(PositionComponent.class).setPosition(position);
        Pools.vector2s.freeCount(1);

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

        Entity wallLeft = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallRight = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallBottom = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallTop = entities.newEntityFromTemplate(EntityId.BOUNDARY);

        Vector2 wallPos = Pools.vector2s.grabNew();
        final Shape verticalWall = new Rectangle(halfWallSize, halfScreenH);
        final Shape horizontalWall = new Rectangle(halfScreenW, halfWallSize);

        wallPos.set(left, 0f);
        wallLeft.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallLeft.requireComponent(ObstacleCollisionComponent.class).setShape(verticalWall);

        wallPos.set(right, 0f);
        wallRight.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallRight.requireComponent(ObstacleCollisionComponent.class).setShape(verticalWall);

        wallPos.set(0f, bottom);
        wallBottom.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallBottom.requireComponent(ObstacleCollisionComponent.class).setShape(horizontalWall);

        wallPos.set(0f, top);
        wallTop.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallTop.requireComponent(ObstacleCollisionComponent.class).setShape(horizontalWall);

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
        if (DevSettings.IN_DEV_MODE) {
            elapsedTime.setSeconds(elapsedTime.getSeconds() / DevSettings.SLOW_MO_FACTOR);
        }

        entities.update(elapsedTime);
        Pools.durations.freeToMark(mark);

        collisionSystem.triggerCollisions();

        renderSystem.update();
        if (DevSettings.IN_DEV_MODE) {
            shapeRenderer.setProjectionMatrix(renderSystem.getCamera().combined);
        }
    }

    private Entity addPlayerEntity() {
        return entities.newEntityFromTemplate(EntityId.PLAYER);
    }

    private void addOctoEnemy(final float x, final float y) {
        Entity octoEnemy = entities.newEntityFromTemplate(EntityId.OCTO);
        PositionComponent positionComponent = octoEnemy.requireComponent(PositionComponent.class);

        Vector2 position = Pools.vector2s.grabNew().set(x, y);
        positionComponent.setPosition(position);
        Pools.vector2s.free(position);
    }

    private void AddMovingBoulderEntity(final float xFrom, final float yFrom, final float xTo, final float yTo) {
        Entity boulderEntity = entities.newEntityFromTemplate(EntityId.BOULDER);

        int mark = Pools.vector2s.mark();
        Vector2 from = Pools.vector2s.grabNew().set(xFrom, yFrom);
        Vector2 to = Pools.vector2s.grabNew().set(xTo, yTo);

        boulderEntity.requireComponent(OscillationBehaviorComponent.class).setOscillation(from, to,
            BOULDER_OSCILLATION_DURATION);

        Pools.vector2s.freeToMark(mark);
    }

    private void addFpsEntity() {
        entities.newEntityFromTemplate(EntityId.FPS_INDICATOR);
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntityFromTemplate(EntityId.TILT_INDICATOR);
        tiltIndicatorEntity.requireComponent(TiltDisplayComponent.class).setTargetEntity(playerEntity);
    }

    private void addTargetEntity() {
        entities.newEntityFromTemplate(EntityId.TARGET_INDICATOR);
    }
}
