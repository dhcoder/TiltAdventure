package tiltadv.tools.font;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public final class FontTool extends ApplicationAdapter {

    public static final String OUTPUT_PATH = "ui/fonts/generated";

    @Override
    public void create() {
//        FileHandle droidRegularFont = Gdx.files.internal("ui/fonts/source/DroidSans.ttf");
        FileHandle droidFixedFont = Gdx.files.internal("ui/fonts/source/DroidSansMono.ttf");
        FileHandle robotoBoldFont = Gdx.files.internal("ui/fonts/source/Roboto-Bold.ttf");
//        FileHandle robotoBoldItalicFont = Gdx.files.internal("ui/fonts/source/Roboto-BoldItalic.ttf");
        FileHandle robotoItalicFont = Gdx.files.internal("ui/fonts/source/Roboto-Italic.ttf");
//        FileHandle robotoLightFont = Gdx.files.internal("ui/fonts/source/Roboto-Light.ttf");
//        FileHandle robotoLightItalicFont = Gdx.files.internal("ui/fonts/source/Roboto-LightItalic.ttf");
        FileHandle robotoRegularFont = Gdx.files.internal("ui/fonts/source/Roboto-Regular.ttf");

        FileHandle outputPath = Gdx.files.local(OUTPUT_PATH);
        outputPath.mkdirs();
        outputPath.emptyDirectory();

        FileHandle readme = Gdx.files.local(OUTPUT_PATH + "/README.txt");
        readme.file();
        readme.writeString("This directory is auto-generated and regularly cleared. Don't add your own files in here!",
            false);

        FontFileGenerator.generate("ui-10", robotoRegularFont, 10, 128, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-12", robotoRegularFont, 12, 256, 64, OUTPUT_PATH);
        FontFileGenerator.generate("ui-16", robotoRegularFont, 16, 256, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-36", robotoRegularFont, 36, 256, 256, OUTPUT_PATH);
        FontFileGenerator.generate("ui-bold-10", robotoBoldFont, 10, 128, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-bold-12", robotoBoldFont, 12, 256, 64, OUTPUT_PATH);
        FontFileGenerator.generate("ui-bold-16", robotoBoldFont, 16, 256, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-bold-36", robotoBoldFont, 36, 256, 256, OUTPUT_PATH);
        FontFileGenerator.generate("ui-italic-10", robotoItalicFont, 10, 128, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-italic-12", robotoItalicFont, 12, 256, 64, OUTPUT_PATH);
        FontFileGenerator.generate("ui-italic-16", robotoItalicFont, 16, 256, 128, OUTPUT_PATH);
        FontFileGenerator.generate("ui-italic-36", robotoItalicFont, 36, 512, 256, OUTPUT_PATH);

        FontFileGenerator.generate("fixed-10", droidFixedFont, 10, 128, 128, OUTPUT_PATH);
        FontFileGenerator.generate("fixed-12", droidFixedFont, 12, 256, 64, OUTPUT_PATH);
        FontFileGenerator.generate("fixed-16", droidFixedFont, 16, 256, 128, OUTPUT_PATH);
        FontFileGenerator.generate("fixed-36", droidFixedFont, 36, 256, 256, OUTPUT_PATH);

        Gdx.app.exit();
    }
}
