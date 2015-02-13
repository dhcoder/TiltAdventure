package tiltadv.tools.scene;

import java.io.File;

/**
* TODO: HEADER COMMENT HERE.
*/
public final class AppSettings {

    public static final int ZOOM_FACTOR = 2;

    private int[] size;
    private String assetDir;

    public int getWidth() { return size[0]; }

    public int getHeight() { return size[1]; }

    public File getAssetDir() { return new File(assetDir); }

    public File getTilesetDir() { return new File(getAssetDir(), "data/tilesets"); }
}
