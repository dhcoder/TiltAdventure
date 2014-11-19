package tiltadv.tools.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import tiltadv.tools.font.BitmapFontWriter.FontInfo;
import tiltadv.tools.font.BitmapFontWriter.OutputFormat;

/**
 * Load a true type font and generate a corresponding bitmap image and .fnt file.
 * Code based on http://ilearnsomethings.blogspot.com/2014/02/libgdx-generate-bitmap-fonts-for-any.html
 */
public final class FontFileGenerator {
    public static BitmapFont generate(final String fontName, final FileHandle fontFile, final int fontSize,
        final int pageWidth, final int pageHeight, final String outputPath) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        PixmapPacker packer = new PixmapPacker(pageWidth, pageHeight, Pixmap.Format.RGBA8888, 2, false);
        parameter.size = fontSize;
        parameter.characters =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\\\"!`?'.,;:()" + "[]{}<>|/@^$-%+=#_&~*";
        parameter.packer = packer;
        FreeTypeBitmapFontData fontData = generator.generateData(parameter);

        Array<PixmapPacker.Page> pages = packer.getPages();
        TextureRegion[] texRegions = new TextureRegion[pages.size];
        for (int i = 0; i < pages.size; i++) {
            PixmapPacker.Page p = pages.get(i);
            Texture tex =
                new Texture(new PixmapTextureData(p.getPixmap(), p.getPixmap().getFormat(), false, false, true)) {
                    @Override
                    public void dispose() {
                        super.dispose();
                        getTextureData().consumePixmap().dispose();
                    }
                };
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            texRegions[i] = new TextureRegion(tex);
        }
        BitmapFont font = new BitmapFont(fontData, texRegions, false);
        save(font, fontSize, outputPath, fontName, packer);
        generator.dispose();
        packer.dispose();
        return font;
    }

    private static boolean save(final BitmapFont font, final int fontSize, final String outputPath,
        final String fontName, final PixmapPacker packer) {
        FileHandle fontFile = getFullPath(outputPath, fontName + ".fnt"); // .fnt path
        FileHandle pixmapDir = getFullPath(outputPath, ""); // png dir path
        BitmapFontWriter.setOutputFormat(OutputFormat.Text);

        String[] pageRefs = BitmapFontWriter.writePixmaps(packer.getPages(), pixmapDir, fontName);
        // here we must add the png dir to the page refs
        for (int i = 0; i < pageRefs.length; i++) {
            pageRefs[i] = fontName + "/" + pageRefs[i];
        }
        BitmapFontWriter.writeFont(font.getData(), pageRefs, fontFile, new FontInfo(fontName, fontSize), 1, 1);
        return true;
    }

    private static FileHandle getFullPath(final String outputPath, final String filename) {
        return Gdx.files.local(outputPath + "/" + filename);
    }
}
