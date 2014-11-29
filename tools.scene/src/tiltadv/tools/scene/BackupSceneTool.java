//package tiltadv.tools.scene;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.Table;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import dhcoder.libgdx.tool.command.CommandManager;
//import dhcoder.libgdx.tool.command.serialization.ShortcutsLoader;
//import dhcoder.libgdx.tool.scene2d.CommandListener;
//import dhcoder.libgdx.tool.scene2d.widget.CommandTree;
//import dhcoder.libgdx.tool.scene2d.widget.CommandWindow;
//import dhcoder.support.opt.Opt;
//
//import static dhcoder.support.text.StringUtils.format;
//
//public final class SceneTool extends ApplicationAdapter {
//
//    private static final String PATH_ASSETS = "ui/";
//    private static final String PATH_CONFIG = "config/scene/";
//
//    private final Opt<SceneContext> activeContextOpt = Opt.withNoValue();
//    // For debug rendering
//    private Stage stage;
//    private Skin skin;
//    private GlobalCommands globalCommands;
//    private CommandWindow commandWindow;
//    private CommandTree commandTree;
//
//    public CommandWindow getCommandWindow() {
//        return commandWindow;
//    }
//
//    public CommandTree getCommandTree() {
//        return commandTree;
//    }
//
//    public GlobalCommands getGlobalCommands() {
//        return globalCommands;
//    }
//
//    @Override
//    public void create() {
//        stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage);
//
//        CommandManager commandManager = new CommandManager();
//        globalCommands = new GlobalCommands(this, commandManager);
//        loadShortcutsInto(commandManager);
//
//        TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal(PATH_ASSETS + "skin.atlas"));
//        skin = new Skin(Gdx.files.internal(PATH_ASSETS + "skin.json"), skinAtlas);
//
//        Table backgroundTable = new Table();
//        backgroundTable.setFillParent(true);
//        stage.addActor(backgroundTable);
//
//        Label firstRunLabel = new Label(format("Press {0} to open the command window",
//            globalCommands.showCommandWindow.getShortcutOpt().getValue()), skin);
//        backgroundTable.add(firstRunLabel);
//        backgroundTable.row();
//
//        Table commandWindowTable = new Table();
//        commandWindowTable.setFillParent(true);
//        stage.addActor(commandWindowTable);
//
//        commandWindow = new CommandWindow(commandManager, skin);
//        commandWindow.setVisible(false);
//        commandWindowTable.add(commandWindow).expandY().fillY().width(400f).fillX().top().pad(20f, 0f, 0f, 0f);
//
//        commandTree = new CommandTree(commandManager, skin);
//        commandTree.setVisible(false);
//        stage.addActor(commandTree);
//
//        stage.addListener(new CommandListener(globalCommands.globalScope));
//    }
//
//    @Override
//    public void resize(final int width, final int height) {
//        stage.getViewport().update(width, height, true);
//    }
//
//    @Override
//    public void render() {
//
//        Gdx.gl.glClearColor(.18f, .18f, .18f, 1f); // Classy dark gray background
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(Gdx.graphics.getDeltaTime());
//        stage.draw();
//    }
//
//    @Override
//    public void dispose() {
//        stage.dispose();
//        skin.dispose();
//    }
//
//    private void loadShortcutsInto(final CommandManager commandManager) {
//        Json json = new Json();
//        ShortcutsLoader.load(json, commandManager, PATH_CONFIG + "shortcuts.json");
//    }
//}
