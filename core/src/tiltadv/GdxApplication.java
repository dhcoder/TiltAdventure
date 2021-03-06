package tiltadv;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import dhcoder.libgdx.assets.ImageDatastore;
import dhcoder.libgdx.assets.Scene;
import dhcoder.libgdx.assets.serialization.AnimationsLoader;
import dhcoder.libgdx.assets.serialization.SceneLoader;
import dhcoder.libgdx.assets.serialization.TilesLoader;
import dhcoder.libgdx.assets.serialization.TilesetLoader;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.math.Angle;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;
import tiltadv.collision.EnemyPlayerCollisionHandler;
import tiltadv.collision.EnemyProjectileDieOnCollisionHandler;
import tiltadv.collision.EnemyProjectilePlayerCollisionHandler;
import tiltadv.collision.SensorEnemyCollisionHandler;
import tiltadv.collision.SwordEnemyCollisionHandler;
import tiltadv.components.behavior.OctoBehaviorComponent;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.behavior.SwordBehaviorComponent;
import tiltadv.components.behavior.TargetIndicatorBehaviorComponent;
import tiltadv.components.behavior.TiltIndicatorBehaviorComponent;
import tiltadv.components.body.FollowCameraComponent;
import tiltadv.components.combat.AttackComponent;
import tiltadv.components.combat.DefenseComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.display.CharacterDisplayComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.dynamics.PositionComponent;
import tiltadv.components.dynamics.TiltComponent;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.components.dynamics.box2d.FixtureComponent;
import tiltadv.components.dynamics.box2d.OffsetComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.hierarchy.children.PlayerChildrenComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.components.input.touchables.TargetTouchableComponent;
import tiltadv.components.math.OscillationComponent;
import tiltadv.globals.Animations;
import tiltadv.globals.Category;
import tiltadv.globals.DevSettings;
import tiltadv.globals.EntityId;
import tiltadv.globals.GameData;
import tiltadv.globals.Physics;
import tiltadv.globals.RenderLayer;
import tiltadv.globals.Scenes;
import tiltadv.globals.Services;
import tiltadv.globals.Tiles;
import tiltadv.input.TouchSystem;
import tiltadv.input.Vibrator;
import tiltadv.memory.Pools;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

public final class GdxApplication extends ApplicationAdapter {

    public static final int ENTITY_COUNT = 200;
    public static final int GROUND_LAYER_COUNT = 1;
    public static final int MAIN_LAYER_COUNT = 50;
    public static final int UI_LAYER_COUNT = 10;
    private static final int VIEWPORT_WIDTH = 320;
    private static final int VIEWPORT_HEIGHT = 240;
    // When you hit a breakpoint while debugging an app, or if the phone you're using is just simply being slow, the
    // delta times between frames can be HUGE. Let's clamp to a reasonable max here. This also prevents physics update
    // logic from dealing with time steps that are too large (at which point, objects start going through walls, etc.)
    private static final float MAX_DELTA_TIME_SECS = 1f / 30f;
    private static final Duration ENEMY_INVINCIBILITY_DURATION = Duration.zero();
    private static final Duration BOULDER_OSCILLATION_DURATION = Duration.fromSeconds(2f);
    private BitmapFont font;
    private EntityManager entities;
    private RenderSystem renderSystem;

    private CircleShape playerBounds;
    //    private Shape gravityWellBounds;
    private CircleShape playerSensorBounds;
    private CircleShape handleBounds;
    private PolygonShape swordBounds;
    private CircleShape octoBounds;
    private CircleShape octoRockBounds;
    private CircleShape boulderBounds;
    private TouchSystem touchSystem;
    private PhysicsSystem physicsSystem;

    @Override
    public void create() {
        if (DevSettings.IN_DEV_MODE) {
            Pool.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            ArrayMap.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            Scene.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            BodyComponent.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
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

        if (!DevSettings.IN_DEV_MODE) {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        }
        else {
            Gdx.gl.glClearColor(1f, 0f, 1f, 1f); // Nasty pink in debug mode
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderSystem.render();

        if (DevSettings.IN_DEV_MODE && DevSettings.SHOW_COLLISION_SHAPES) {
            physicsSystem.debugRender(renderSystem.getCamera().combined, Physics.METERS_TO_PIXELS);
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        GameData.images.dispose();
        renderSystem.dispose();
        physicsSystem.dispose();
    }

    private void initializeServices() {
        initializePhysics();
        Services.register(PhysicsSystem.class, physicsSystem);

        // TODO: Tune the application for the best batch size for render system
        // https://github.com/libgdx/libgdx/wiki/Spritebatch,-Textureregions,-and-Sprites#performance-tuning
        renderSystem = new RenderSystem(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, DevSettings.IN_DEV_MODE ? 1000 : 600);
        renderSystem.addLayer(GROUND_LAYER_COUNT).setBlending(false).setSorted(false); // ground layer
        renderSystem.addLayer(MAIN_LAYER_COUNT);
        renderSystem.addLayer(UI_LAYER_COUNT).setUiLayer(true).setSorted(false);
        Services.register(RenderSystem.class, renderSystem);

        touchSystem = new TouchSystem(ENTITY_COUNT / 2);
        Services.register(TouchSystem.class, touchSystem);

        Services.register(Vibrator.class, new Vibrator());

    }

    private void initializePhysics() {
        physicsSystem = new PhysicsSystem(ENTITY_COUNT);

        physicsSystem.registerCollidable(Category.ENEMY, Category.PLAYER | Category.OBSTACLES);
        physicsSystem.registerCollidable(Category.ENEMY_PROJECTILE, Category.PLAYER | Category.OBSTACLES);
        physicsSystem.registerCollidable(Category.PLAYER, Category.OBSTACLES);
        physicsSystem.registerCollidable(Category.PLAYER_SENSOR, Category.ENEMY);
        physicsSystem.registerCollidable(Category.SWORD, Category.ENEMY);

        physicsSystem.addCollisionHandler(Category.ENEMY_PROJECTILE, Category.PLAYER,
            new EnemyProjectilePlayerCollisionHandler());
        physicsSystem.addCollisionHandler(Category.ENEMY_PROJECTILE, Category.PLAYER | Category.OBSTACLES,
            new EnemyProjectileDieOnCollisionHandler());
        physicsSystem.addCollisionHandler(Category.ENEMY, Category.PLAYER, new EnemyPlayerCollisionHandler());
        physicsSystem.addCollisionHandler(Category.PLAYER_SENSOR, Category.ENEMY, new SensorEnemyCollisionHandler());
        physicsSystem.addCollisionHandler(Category.SWORD, Category.ENEMY, new SwordEnemyCollisionHandler());
    }

    private void initializeAssets() {

        Json json = new Json();

        {
            final FileHandle[] tilesetFiles = Gdx.files.internal("data/tilesets").list();
            for (int i = 0; i < tilesetFiles.length; ++i) {
                TilesetLoader.load(json, GameData.images, GameData.tilesets, tilesetFiles[i].path());
            }
        }

        {
            final FileHandle[] tileFiles = Gdx.files.internal("data/tiles").list();
            for (int i = 0; i < tileFiles.length; ++i) {
                TilesLoader.load(json, GameData.tilesets, GameData.tiles, tileFiles[i].path());
            }
        }

        {
            final FileHandle[] animationFiles = Gdx.files.internal("data/animations").list();
            for (int i = 0; i < animationFiles.length; ++i) {
                AnimationsLoader.load(json, GameData.tilesets, GameData.animations, animationFiles[i].path());
            }
        }

        {
            final FileHandle[] sceneFiles = Gdx.files.internal("data/scenes").list();
            for (int i = 0; i < sceneFiles.length; ++i) {
                SceneLoader.load(json, GameData.tilesets, GameData.scenes, sceneFiles[i].path());
            }
        }
    }

    private void initializeEntities() {

        entities = new EntityManager(ENTITY_COUNT);

//        gravityWellBounds = new Circle(8.0f);
        playerBounds = Physics.newCircle(5.5f);
        playerSensorBounds = Physics.newCircle(8.0f);
        swordBounds =
            Physics.newRectangle(Tiles.SWORDRIGHT.getRegionWidth() / 2f, Tiles.SWORDRIGHT.getRegionHeight() / 2f);
        octoBounds = Physics.newCircle(8.0f);
        octoRockBounds = Physics.newCircle(Tiles.ROCK.getRegionWidth() / 2f);
        boulderBounds = Physics.newCircle(8f);

        entities.registerTemplate(EntityId.BOUNDARY, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(BodyComponent.class);
                entity.addComponent(FixtureComponent.class).setCategory(Category.OBSTACLES);
            }
        });

        entities.registerTemplate(EntityId.CAMERA_ENTITY, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(FollowCameraComponent.class);
            }
        });

//        entities.registerTemplate(EntityId.GRAVITY_WELL, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR);
//            }
//        });

        entities.registerTemplate(EntityId.PLAYER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.DynamicBody);
                entity.addComponent(FixtureComponent.class).setShape(playerBounds).setCategory(Category.PLAYER);
                entity.addComponent(PlayerChildrenComponent.class);
                entity.addComponent(KnockbackComponent.class);
                entity.addComponent(DefenseComponent.class);
                Vector2 offset = Pools.vector2s.grabNew().set(0f, 5f);
                entity.addComponent(SpriteComponent.class).setOffset(offset);
                Pools.vector2s.freeCount(1);
                entity.addComponent(TiltComponent.class);
                entity.addComponent(Gdx.app.getType() == ApplicationType.Android ? AccelerometerInputComponent.class :
                    KeyboardInputComponent.class);
                entity.addComponent(PlayerBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(10);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.PLAYER_S, Animations.PLAYER_E, Animations.PLAYER_N, Animations.PLAYER_SE,
                        Animations.PLAYER_NW);
            }
        });

        entities.registerTemplate(EntityId.PLAYER_SWORD, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(ParentComponent.class); // Child of Player Entity
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.KinematicBody);
                entity.addComponent(FixtureComponent.class).setShape(swordBounds).setCategory(Category.SWORD)
                    .setSensor(true);
                entity.addComponent(OffsetComponent.class).setOffset(new Vector2(10f, 0f));
                entity.addComponent(SwordBehaviorComponent.class);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
            }
        });

        entities.registerTemplate(EntityId.PLAYER_SENSOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(ParentComponent.class);  // Child of Player Entity
                entity.addComponent(PositionComponent.class);
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.KinematicBody);
                entity.addComponent(FixtureComponent.class).setShape(playerSensorBounds).setSensor(true)
                    .setCategory(Category.PLAYER_SENSOR);
                entity.addComponent(OffsetComponent.class).setOffset(new Vector2(10f, 0));
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR)
                    .setZ(SpriteComponent.ALWAYS_BELOW);
            }
        });

        entities.registerTemplate(EntityId.BOULDER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.BOULDER);
                entity.addComponent(OscillationComponent.class);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.KinematicBody);
                entity.addComponent(FixtureComponent.class).setShape(boulderBounds).setCategory(Category.OBSTACLES);
            }
        });

        entities.registerTemplate(EntityId.OCTO, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.DynamicBody);
                entity.addComponent(FixtureComponent.class).setShape(octoBounds).setCategory(Category.ENEMY);
                entity.addComponent(KnockbackComponent.class).setMultiplier(2f);
                entity.addComponent(AttackComponent.class);
                entity.addComponent(DefenseComponent.class);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(TargetTouchableComponent.class);
                entity.addComponent(OctoBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(3)
                    .setInvicibilityDuration(ENEMY_INVINCIBILITY_DURATION);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP);
            }
        });

        entities.registerTemplate(EntityId.OCTO_ROCK, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(AttackComponent.class);
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
                entity.addComponent(BodyComponent.class).setBodyType(BodyType.DynamicBody).setFastMoving(true)
                    .setDamping(Physics.NO_DAMPING);
                entity.addComponent(FixtureComponent.class).setShape(octoRockBounds)
                    .setCategory(Category.ENEMY_PROJECTILE);
            }
        });

        entities.registerTemplate(EntityId.TARGET_INDICATOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(SpriteComponent.class);
                entity.addComponent(TargetIndicatorBehaviorComponent.class).setTextureRegion(Tiles.ROCK);
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
                entity.addComponent(TiltIndicatorBehaviorComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
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
//        addOctoEnemies();
//        addMovingBoulderEntities();
//        addTiltIndicatorEntity(playerEntity);
        addFpsEntity();
        addBoundaryWalls();
        addTargetEntity();

        Scene demoScene = Scenes.DEMO;
        renderSystem.add(RenderLayer.Ground, demoScene);
    }

    private void addMainCamera(final Entity playerEntity) {
        entities.newEntityFromTemplate(EntityId.CAMERA_ENTITY).requireComponent(FollowCameraComponent.class)
            .setTargetEntity(playerEntity);
    }

//    private void addGravityWell(final float x, final float y) {
//        Entity gravityWellEntity = entities.newEntityFromTemplate(EntityId.GRAVITY_WELL);
//
//        Vector2 position = Pools.vector2s.grabNew().set(x, y);
//        gravityWellEntity.requireComponent(PositionComponent.class).setPosition(position);
//        Pools.vector2s.freeCount(1);
//
//    }

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
        Scene scene = Scenes.DEMO;
        float halfSceneW = (scene.getWidth() / 2f);
        float halfSceneH = (scene.getHeight() / 2f);
        float halfWallSize = 10f;
        float bottom = (-halfSceneH - halfWallSize);
        float top = (halfSceneH + halfWallSize);
        float left = (-halfSceneW - halfWallSize);
        float right = (halfSceneW + halfWallSize);

        Entity wallLeft = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallRight = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallBottom = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallTop = entities.newEntityFromTemplate(EntityId.BOUNDARY);

        final PolygonShape verticalWall = Physics.newRectangle(halfWallSize, halfSceneH);
        final PolygonShape horizontalWall = Physics.newRectangle(halfSceneW, halfWallSize);

        Vector2 wallPos = Pools.vector2s.grabNew();
        wallPos.set(left, 0f);
        wallLeft.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallLeft.requireComponent(FixtureComponent.class).setShape(verticalWall);

        wallPos.set(right, 0f);
        wallRight.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallRight.requireComponent(FixtureComponent.class).setShape(verticalWall);

        wallPos.set(0f, bottom);
        wallBottom.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallBottom.requireComponent(FixtureComponent.class).setShape(horizontalWall);

        wallPos.set(0f, top);
        wallTop.requireComponent(PositionComponent.class).setPosition(wallPos);
        wallTop.requireComponent(FixtureComponent.class).setShape(horizontalWall);

        Pools.vector2s.free(wallPos);
    }

    private void addMovingBoulderEntities() {
        final int numBoulders = 15;
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
        {
            int mark = Pools.durations.mark();
            Duration elapsedTime = Pools.durations.grabNew();
            elapsedTime.setSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));
            if (DevSettings.IN_DEV_MODE) {
                elapsedTime.setSeconds(elapsedTime.getSeconds() / DevSettings.SLOW_MO_FACTOR);
            }

            entities.update(elapsedTime);
            physicsSystem.update(elapsedTime);
            Pools.durations.freeToMark(mark);
        }

        renderSystem.update();
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

        boulderEntity.requireComponent(OscillationComponent.class)
            .setOscillation(from, to, BOULDER_OSCILLATION_DURATION);

        Pools.vector2s.freeToMark(mark);
    }

    private void addFpsEntity() {
        entities.newEntityFromTemplate(EntityId.FPS_INDICATOR);
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntityFromTemplate(EntityId.TILT_INDICATOR);
        tiltIndicatorEntity.requireComponent(TiltIndicatorBehaviorComponent.class).setTargetEntity(playerEntity);
    }

    private void addTargetEntity() {
        entities.newEntityFromTemplate(EntityId.TARGET_INDICATOR);
    }
}
