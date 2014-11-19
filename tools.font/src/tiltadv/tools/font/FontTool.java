package tiltadv.tools.font;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public final class FontTool extends ApplicationAdapter {

    @Override
    public void create() {
        FileHandle robotoHandle = Gdx.files.internal("ui/fonts/source/Roboto-Regular.ttf");
        FontFileGenerator.generate("default-font", robotoHandle, 12, 256, 64, "ui/fonts/generated");

        Gdx.app.exit();
    }
}
