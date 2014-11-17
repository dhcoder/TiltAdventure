package tiltadv.tools.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dhcoder.libgdx.tool.action.ActionManager;
import dhcoder.libgdx.tool.action.InputHandler;
import tiltadv.tools.scene.action.Actions;

import static dhcoder.support.text.StringUtils.format;

public final class SceneTool extends ApplicationAdapter {

    private static final boolean SHOW_DEBUG_SHAPES = true;
    // For debug rendering
    private Stage stage;
    private Skin skin;
    private InputHandler inputHandler;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        ActionManager actionManager = new ActionManager();
        Actions.registerWith(actionManager);
        inputHandler = new InputHandler(actionManager);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("ui/skin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/skin.json"), skinAtlas);

        Label firstRunLabel =
            new Label(format("Press {0} to open the action window", Actions.ShowActionWindow.getShortcutOpt().getValue()),
                skin);

        table.add(firstRunLabel);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(final InputEvent event, final int keycode) {
                return inputHandler.handleKeyDown(keycode);
            }

            @Override
            public boolean keyUp(final InputEvent event, final int keycode) {
                return inputHandler.handleKeyUp(keycode);
            }
        });

        if (SHOW_DEBUG_SHAPES) {
            table.setDebug(true);
        }
    }

    @Override
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(.18f, .18f, .18f, 1f); // Classy dark gray background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
