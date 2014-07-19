package tiltadv;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dhcoder.support.immutable.ImmutableDuration;
import tiltadv.entity.Component;
import tiltadv.entity.Entity;
import tiltadv.entity.components.behavior.PlayerBehaviorComponent;
import tiltadv.entity.components.controllers.AccelerometerComponent;
import tiltadv.entity.components.controllers.KeyboardComponent;
import tiltadv.entity.components.data.MotionComponent;
import tiltadv.entity.components.data.SizeComponent;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.data.TransformComponent;
import tiltadv.entity.components.display.TiltDisplayComponent;
import tiltadv.entity.components.sprite.SpriteComponent;
import tiltadv.entity.components.display.FpsDisplayComponent;
import tiltadv.immutable.ImmutableSprite;

import java.util.ArrayList;
import java.util.List;

public class GdxApplication extends ApplicationAdapter {

    private static final int VIEWPORT_HEIGHT = 240;
    private static final int VIEWPORT_WIDTH = 320;

    // When you hit a breakpoint while debugging an app, this causes the delta time to be huge between the next frame
    // and this one. Clamping to a reasonable max value works around this issue.
    private static final float MAX_DELTA_TIME_SECS = .5f;

    private BitmapFont font;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture tiles;

    private List<Entity> entities;
    private Entity playerEntity;

    @Override
    public void create() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();
        font = new BitmapFont();
        tiles = new Texture("Tiles.png");

        entities = new ArrayList<Entity>();
        AddPlayerEntity();

        AddTiltIndicatorEntity();
        AddFpsEntity();
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
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
        tiles.dispose();
    }

    private void update() {
        ImmutableDuration elapsedTime =
            ImmutableDuration.fromSeconds(Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS));

        for (Entity entity : entities) {
            entity.update(elapsedTime);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void AddPlayerEntity() {
        Sprite playerUp = new Sprite(tiles, 60, 0, 16, 16);
        Sprite playerDown = new Sprite(tiles, 0, 0, 16, 16);
        Sprite playerLeft = new Sprite(tiles, 29, 0, 16, 16);
        Sprite playerRight = new Sprite(tiles, 90, 0, 16, 16);

        List<Component> components = new ArrayList<Component>();
        components.add(new SpriteComponent());
        components.add(SizeComponent.fromSprite(new ImmutableSprite(playerDown)));
        components.add(new TransformComponent());
        components.add(new MotionComponent());
        components.add(new TiltComponent());
        components.add(Gdx.app.getType() == Application.ApplicationType.Android ? new AccelerometerComponent() :
            new KeyboardComponent());
        components.add(new PlayerBehaviorComponent(playerUp, playerDown, playerLeft, playerRight));
        playerEntity = new Entity(components);
        entities.add(playerEntity);
    }

    private void AddFpsEntity() {
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()).build();
        FpsDisplayComponent fpsDisplayComponent = new FpsDisplayComponent(font);
        entities.add(new Entity(transformComponent, fpsDisplayComponent));
    }

    private void AddTiltIndicatorEntity() {
        Sprite rodRight = new Sprite(tiles, 98, 126, 13, 4);
        float margin = 5f;
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(VIEWPORT_WIDTH / 2 - rodRight.getWidth() - margin,
                VIEWPORT_HEIGHT / 2 - rodRight.getHeight() - margin).build();

        SpriteComponent spriteComponent = new SpriteComponent();
        SizeComponent sizeComponent = SizeComponent.fromSprite(new ImmutableSprite(rodRight));
        TiltDisplayComponent tiltDisplayComponent = new TiltDisplayComponent(rodRight, playerEntity);
        entities.add(new Entity(spriteComponent, sizeComponent, transformComponent, tiltDisplayComponent));
    }

}
