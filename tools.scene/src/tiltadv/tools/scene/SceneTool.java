package tiltadv.tools.scene;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import dhcoder.libgdx.tool.command.CommandManager;
import tiltadv.tools.scene.commands.Commands;

import static dhcoder.support.text.StringUtils.format;

public final class SceneTool extends ApplicationAdapter {

    private static final boolean SHOW_DEBUG_SHAPES = true;
    // For debug rendering
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        initializeCommands();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("ui/skin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/skin.json"), skinAtlas);

        Label firstRunLabel =
            new Label(format("Press {0} to open the action window", Commands.RunAction.getShortcutOpt().getValue()),
                skin);

        table.add(firstRunLabel);

        if (SHOW_DEBUG_SHAPES) {
            table.setDebug(true);
        }
    }

    @Override
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void initializeCommands() {
        CommandManager commandManager = new CommandManager();
        Commands.registerWith(commandManager);
    }
}