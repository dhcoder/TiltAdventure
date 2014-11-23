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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.InputHandler;
import dhcoder.libgdx.tool.command.serialization.ShortcutsLoader;
import dhcoder.support.opt.Opt;

import static dhcoder.support.text.StringUtils.format;

public final class SceneTool extends ApplicationAdapter {

    private static final String PATH_ASSETS = "ui/";
    private static final String PATH_CONFIG = "config/scene/";

    private static final boolean SHOW_DEBUG_SHAPES = true;
    private final Opt<SceneContext> activeContextOpt = Opt.withNoValue();
    // For debug rendering
    private Stage stage;
    private Skin skin;
    private InputHandler inputHandler;
    private GlobalCommands globalCommands;

    public GlobalCommands getGlobalCommands() {
        return globalCommands;
    }

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        CommandManager commandManager = new CommandManager();
        globalCommands = new GlobalCommands(this, commandManager);
        loadShortcutsInto(commandManager);

        inputHandler = new InputHandler(commandManager);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal(PATH_ASSETS + "skin.atlas"));
        skin = new Skin(Gdx.files.internal(PATH_ASSETS + "skin.json"), skinAtlas);

        Label firstRunLabel = new Label(format("Press {0} to open the command window",
            globalCommands.showCommandWindow.getShortcutOpt().getValue()), skin);

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

    private void loadShortcutsInto(final CommandManager commandManager) {
        Json json = new Json();
        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
    }
}
