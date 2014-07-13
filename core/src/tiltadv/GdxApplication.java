package tiltadv;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiltadv.entity.Entity;
import tiltadv.entity.components.SizeComponent;
import tiltadv.entity.components.TransformComponent;
import tiltadv.entity.components.behavior.PlayerBehaviorComponent;
import tiltadv.entity.components.sprite.SpriteComponent;
import tiltadv.entity.components.util.FpsComponent;
import tiltadv.entity.components.util.TiltComponent;

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
        float deltaTime = Math.min(Gdx.graphics.getRawDeltaTime(), MAX_DELTA_TIME_SECS);
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void AddPlayerEntity() {
        Sprite playerDown = new Sprite(tiles, 0, 0, 16, 16);
        SpriteComponent spriteComponent = new SpriteComponent(playerDown);

        SizeComponent sizeComponent = new SizeComponent(playerDown.getWidth(), playerDown.getHeight());
        TransformComponent transformComponent = new TransformComponent();
        PlayerBehaviorComponent behaviorComponent = new PlayerBehaviorComponent();

        entities.add(new Entity(transformComponent, sizeComponent, spriteComponent, behaviorComponent));
    }

    private void AddFpsEntity() {
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(-VIEWPORT_WIDTH / 2, -VIEWPORT_HEIGHT / 2 + font.getLineHeight()).build();
        FpsComponent fpsComponent = new FpsComponent(font);
        entities.add(new Entity(transformComponent, fpsComponent));
    }

    private void AddTiltIndicatorEntity() {
        Sprite rodUp = new Sprite(tiles, 64, 113, 7, 13);
        SpriteComponent spriteComponent = new SpriteComponent(rodUp);
        SizeComponent sizeComponent = new SizeComponent(rodUp.getWidth(), rodUp.getHeight());

        float margin = 5f;
        TransformComponent transformComponent = new TransformComponent.Builder()
            .setTranslate(VIEWPORT_WIDTH / 2 - rodUp.getWidth() - margin,
                VIEWPORT_HEIGHT / 2 - rodUp.getHeight() - margin).build();

        TiltComponent tiltComponent = new TiltComponent();
        entities.add(new Entity(spriteComponent, sizeComponent, transformComponent, tiltComponent));
    }

}
