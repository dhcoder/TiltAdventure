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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.entity.EntityManager;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.render.RenderSystem;
import dhcoder.support.collection.ArrayMap;
import dhcoder.support.memory.Pool;
import dhcoder.support.time.Duration;
import tiltadv.assets.AnimationDatastore;
import tiltadv.assets.ImageDatastore;
import tiltadv.assets.Scene;
import tiltadv.assets.SceneDatastore;
import tiltadv.assets.TileDatastore;
import tiltadv.assets.TilesetDatastore;
import tiltadv.components.behavior.PlayerBehaviorComponent;
import tiltadv.components.body.FollowCameraComponent;
import tiltadv.components.body.HeadingComponent;
import tiltadv.components.body.SimplePositionComponent;
import tiltadv.components.body.TiltComponent;
import tiltadv.components.box2d.BodyComponent;
import tiltadv.components.combat.DefenseComponent;
import tiltadv.components.combat.HealthComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.display.CharacterDisplayComponent;
import tiltadv.components.display.FpsDisplayComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.display.TiltDisplayComponent;
import tiltadv.components.hierarchy.children.PlayerChildrenComponent;
import tiltadv.components.input.AccelerometerInputComponent;
import tiltadv.components.input.KeyboardInputComponent;
import tiltadv.globals.Animations;
import tiltadv.globals.DevSettings;
import tiltadv.globals.EntityId;
import tiltadv.globals.Physics;
import tiltadv.globals.RenderLayer;
import tiltadv.globals.Services;
import tiltadv.globals.Tiles;
import tiltadv.input.TouchSystem;
import tiltadv.input.Vibrator;
import tiltadv.memory.Pools;
import tiltadv.serialization.AnimationsLoader;
import tiltadv.serialization.SceneLoader;
import tiltadv.serialization.TilesLoader;
import tiltadv.serialization.TilesetLoader;

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

    //    private Shape octoBounds;
//    private Shape playerBounds;
//    private Shape gravityWellBounds;
//    private Shape playerSensorBounds;
//    private Shape playerSwordBounds;
//    private Shape octoRockBounds;
//    private Shape boulderBounds;
    private TouchSystem touchSystem;
    private PhysicsSystem physicsSystem;

    public void create() {
        if (DevSettings.IN_DEV_MODE) {
            Pool.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            ArrayMap.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
            Scene.RUN_SANITY_CHECKS = DevSettings.RUN_SANITY_CHECKS;
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
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f); // Desert-ish color, for testing!
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
        Services.get(ImageDatastore.class).dispose();
        renderSystem.dispose();
        physicsSystem.dispose();
    }

    private void initializeServices() {
        Services.register(ImageDatastore.class, new ImageDatastore());
        Services.register(TilesetDatastore.class, new TilesetDatastore());
        Services.register(TileDatastore.class, new TileDatastore());
        Services.register(AnimationDatastore.class, new AnimationDatastore());
        Services.register(SceneDatastore.class, new SceneDatastore());
        Services.register(Json.class, new Json());

        physicsSystem = new PhysicsSystem();
        Services.register(PhysicsSystem.class, physicsSystem);

        physicsSystem.setPostUpdateMethod(new PhysicsSystem.PostUpdateMethod() {
            @Override
            public void postUpdate(final Array<Body> bodies) {
                for (int i = 0; i < bodies.size; i++) {
                    Body body = bodies.get(i);
                    BodyComponent bodyComponent = (BodyComponent)body.getUserData();
                    bodyComponent.sync();
                }
            }
        });

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

        {
            final FileHandle[] sceneFiles = Gdx.files.internal("data/scenes").list();
            for (int i = 0; i < sceneFiles.length; ++i) {
                SceneLoader.load(sceneFiles[i].path());
            }
        }
    }

    private void initializeEntities() {

        entities = new EntityManager(ENTITY_COUNT);

//        gravityWellBounds = new Circle(8.0f);
//        octoBounds = new Circle(8.0f);
//        playerBounds = new Circle(6.5f);
//        playerSensorBounds = new Circle(8.0f);
//        playerSwordBounds = new Circle(5f);
//        octoRockBounds = new Circle(Tiles.ROCK.getRegionWidth() / 2f);
//        boulderBounds = new Circle(Tiles.BOULDER.getRegionWidth() / 2f);

        entities.registerTemplate(EntityId.BOUNDARY, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(BodyComponent.class);
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
//                entity.addComponent(GravityWellCollisionComponent.class).setShape(gravityWellBounds);
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR);
//            }
//        });

        entities.registerTemplate(EntityId.PLAYER, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(PlayerChildrenComponent.class);
                entity.addComponent(KnockbackComponent.class);
                entity.addComponent(DefenseComponent.class);
                Vector2 offset = Pools.vector2s.grabNew().set(0f, 5f);
                entity.addComponent(SpriteComponent.class).setOffset(offset);
                Pools.vector2s.freeCount(1);
                entity.addComponent(HeadingComponent.class);
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(8f * Physics.PIXELS_TO_METERS);
                entity.addComponent(BodyComponent.class).setBodyType(BodyDef.BodyType.DynamicBody)
                    .setShape(circleShape);
                entity.addComponent(TiltComponent.class);
                entity.addComponent(Gdx.app.getType() == ApplicationType.Android ? AccelerometerInputComponent.class :
                    KeyboardInputComponent.class);
                entity.addComponent(PlayerBehaviorComponent.class);
                entity.addComponent(HealthComponent.class).setHealth(10);
                entity.addComponent(CharacterDisplayComponent.class)
                    .set(Animations.PLAYER_S, Animations.PLAYER_E, Animations.PLAYER_N, Animations.PLAYER_SE,
                        Animations.PLAYER_NW);
//                entity.addComponent(PlayerCollisionComponent.class).setShape(playerBounds);
            }
        });

//        entities.registerTemplate(EntityId.PLAYER_SWORD, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(ParentComponent.class); // Child of Player Entity
//                entity.addComponent(OffsetComponent.class);
//                entity.addComponent(ChildOffsetComponent.class);
//                entity.addComponent(SwordCollisionComponent.class).setShape(playerSwordBounds);
//                entity.addComponent(SwordBehaviorComponent.class);
//                entity.addComponent(AttackComponent.class);
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
//                entity.addComponent(PositionComponent.class);
//            }
//        });

//        entities.registerTemplate(EntityId.PLAYER_SENSOR, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(ParentComponent.class);  // Child of Player Entity
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(OffsetComponent.class);
//                entity.addComponent(ChildOffsetComponent.class);
//                entity.addComponent(PlayerSensorCollisionComponent.class).setShape(playerSensorBounds);
//                entity.addComponent(PlayerSensorBehaviorComponent.class);
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.SENSOR);
//            }
//        });

//        entities.registerTemplate(EntityId.BOULDER, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.BOULDER);
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(OscillationBehaviorComponent.class);
//                entity.addComponent(ObstacleCollisionComponent.class).setShape(boulderBounds);
//
//            }
//        });

//        entities.registerTemplate(EntityId.OCTO, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(KnockbackComponent.class).setMultiplier(2f);
//                entity.addComponent(AttackComponent.class);
//                entity.addComponent(DefenseComponent.class);
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(SpriteComponent.class);
//                entity.addComponent(HeadingComponent.class);
//                entity.addComponent(MotionComponent.class);
//                entity.addComponent(TargetTouchableComponent.class);
//                entity.addComponent(OctoBehaviorComponent.class);
//                entity.addComponent(HealthComponent.class).setHealth(3)
//                    .setInvicibilityDuration(ENEMY_INVINCIBILITY_DURATION);
//                entity.addComponent(CharacterDisplayComponent.class)
//                    .set(Animations.OCTODOWN, Animations.OCTORIGHT, Animations.OCTOUP);
//                entity.addComponent(EnemyCollisionComponent.class).setShape(octoBounds);
//            }
//        });

//        entities.registerTemplate(EntityId.OCTO_ROCK, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(AttackComponent.class);
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
//                entity.addComponent(HeadingComponent.class);
//                entity.addComponent(MotionComponent.class);
//                entity.addComponent(EnemyProjectileCollisionComponent.class).setShape(octoRockBounds);
//            }
//        });

//        entities.registerTemplate(EntityId.TARGET_INDICATOR, new EntityManager.EntityCreator() {
//            @Override
//            public void initialize(final Entity entity) {
//                entity.addComponent(PositionComponent.class);
//                entity.addComponent(SpriteComponent.class);
//                entity.addComponent(TargetDisplayComponent.class).setTextureRegion(Tiles.ROCK);
//                entity.addComponent(OffsetComponent.class);
//            }
//        });

        entities.registerTemplate(EntityId.FPS_INDICATOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                entity.addComponent(SimplePositionComponent.class)
                    .setPosition(new Vector2(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()));
                entity.addComponent(FpsDisplayComponent.class).set(font);
            }
        });

        entities.registerTemplate(EntityId.TILT_INDICATOR, new EntityManager.EntityCreator() {
            @Override
            public void initialize(final Entity entity) {
                final float MARGIN = 5f;
                entity.addComponent(TiltDisplayComponent.class).setTextureRegion(Tiles.SWORDRIGHT);
                entity.addComponent(SimplePositionComponent.class).setPosition(
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
        addTiltIndicatorEntity(playerEntity);
        addFpsEntity();
        addBoundaryWalls();
//        addTargetEntity();

        Scene demoScene = Services.get(SceneDatastore.class).get("demo");
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

//    private void addOctoEnemies() {
//        addOctoEnemy(-80, 20);
//        addOctoEnemy(-30, 20);
//        addOctoEnemy(-80, 80);
//        addOctoEnemy(-30, 80);
//        addOctoEnemy(80, 20);
//        addOctoEnemy(30, 20);
//        addOctoEnemy(30, 80);
//        addOctoEnemy(80, 80);
//        addOctoEnemy(-80, -20);
//        addOctoEnemy(-30, -20);
//        addOctoEnemy(-80, -80);
//        addOctoEnemy(-30, -80);
//        addOctoEnemy(30, -80);
//        addOctoEnemy(80, -80);
//        addOctoEnemy(30, -20);
//        addOctoEnemy(80, -20);
//    }

    private void addBoundaryWalls() {
        Scene scene = Services.get(SceneDatastore.class).get("demo");
        float halfSceneW = (scene.getWidth() / 2f) * Physics.PIXELS_TO_METERS;
        float halfSceneH = (scene.getHeight() / 2f) * Physics.PIXELS_TO_METERS;
        float halfWallSize = 10f * Physics.PIXELS_TO_METERS;
        float top = (halfSceneH + halfWallSize) * Physics.PIXELS_TO_METERS;
        float bottom = (-halfSceneH - halfWallSize) * Physics.PIXELS_TO_METERS;
        float left = (-halfSceneW - halfWallSize) * Physics.PIXELS_TO_METERS;
        float right = (halfSceneW + halfWallSize) * Physics.PIXELS_TO_METERS;

        Entity wallLeft = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallRight = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallBottom = entities.newEntityFromTemplate(EntityId.BOUNDARY);
        Entity wallTop = entities.newEntityFromTemplate(EntityId.BOUNDARY);

        final PolygonShape verticalWall = new PolygonShape();
        verticalWall.setAsBox(halfWallSize, halfSceneH);
        final PolygonShape horizontalWall = new PolygonShape();
        horizontalWall.setAsBox(halfSceneW, halfWallSize);

        Vector2 wallPos = Pools.vector2s.grabNew();
        wallPos.set(left * Physics.METERS_TO_PIXELS, 0f);
        wallLeft.requireComponent(BodyComponent.class).setPosition(wallPos).setShape(verticalWall);

        wallPos.set(right * Physics.METERS_TO_PIXELS, 0f);
        wallRight.requireComponent(BodyComponent.class).setPosition(wallPos).setShape(verticalWall);

        wallPos.set(0f, bottom * Physics.METERS_TO_PIXELS);
        wallBottom.requireComponent(BodyComponent.class).setPosition(wallPos).setShape(horizontalWall);

        wallPos.set(0f, top * Physics.METERS_TO_PIXELS);
        wallTop.requireComponent(BodyComponent.class).setPosition(wallPos).setShape(horizontalWall);

        Pools.vector2s.free(wallPos);
    }

//    private void addMovingBoulderEntities() {
//        final int numBoulders = 4;
//        final float scaleX = 120;
//        final float scaleY = 90;
//        final float percent = .4f;
//        for (int i = 0; i < numBoulders; ++i) {
//            float circleDistance = (float)i / (float)numBoulders * Angle.TWO_PI;
//            float xTo = scaleX * cos(circleDistance);
//            float yTo = scaleY * sin(circleDistance);
//            float xFrom = xTo * percent;
//            float yFrom = yTo * percent;
//            AddMovingBoulderEntity(xTo, yTo, xFrom, yFrom);
//        }
//    }

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

//    private void addOctoEnemy(final float x, final float y) {
//        Entity octoEnemy = entities.newEntityFromTemplate(EntityId.OCTO);
//        PositionComponent positionComponent = octoEnemy.requireComponent(PositionComponent.class);
//
//        Vector2 position = Pools.vector2s.grabNew().set(x, y);
//        positionComponent.setPosition(position);
//        Pools.vector2s.free(position);
//    }

//    private void AddMovingBoulderEntity(final float xFrom, final float yFrom, final float xTo, final float yTo) {
//        Entity boulderEntity = entities.newEntityFromTemplate(EntityId.BOULDER);
//
//        int mark = Pools.vector2s.mark();
//        Vector2 from = Pools.vector2s.grabNew().set(xFrom, yFrom);
//        Vector2 to = Pools.vector2s.grabNew().set(xTo, yTo);
//
//        boulderEntity.requireComponent(OscillationBehaviorComponent.class)
//            .setOscillation(from, to, BOULDER_OSCILLATION_DURATION);
//
//        Pools.vector2s.freeToMark(mark);
//    }

    private void addFpsEntity() {
        entities.newEntityFromTemplate(EntityId.FPS_INDICATOR);
    }

    private void addTiltIndicatorEntity(final Entity playerEntity) {
        Entity tiltIndicatorEntity = entities.newEntityFromTemplate(EntityId.TILT_INDICATOR);
        tiltIndicatorEntity.requireComponent(TiltDisplayComponent.class).setTargetEntity(playerEntity);
    }

//    private void addTargetEntity() {
//        entities.newEntityFromTemplate(EntityId.TARGET_INDICATOR);
//    }
}
