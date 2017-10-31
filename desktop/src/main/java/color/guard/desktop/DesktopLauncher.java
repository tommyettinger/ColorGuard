package color.guard.desktop;

import color.guard.ColorGuardGame;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
//        TexturePacker.Settings settings = new TexturePacker.Settings();
//        settings.format = Pixmap.Format.Alpha;
//        settings.combineSubdirectories = true;
//        settings.stripWhitespaceX = true;
//        settings.stripWhitespaceY = true;
//        settings.filterMag = Texture.TextureFilter.Nearest;
//        settings.filterMin = Texture.TextureFilter.Nearest;
//        settings.maxHeight = 4096;
//        settings.maxWidth = 4096;
//        settings.flattenPaths = true;
//        settings.fast = true;
//        TexturePacker.process(settings, "../../ColorGuardAssets/Blank_Wargame_Iso_Mini_Packable", "../assets", "Iso_Mini_Alpha");
        createApplication();
    }

    private static LwjglApplication createApplication() {
        return new LwjglApplication(new ColorGuardGame(), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "ColorGuard";
        configuration.width = 800;
        configuration.height = 450;
        configuration.vSyncEnabled = false;
        configuration.foregroundFPS = 0;
        configuration.addIcon("libgdx" + 128 + ".png", FileType.Internal);
        configuration.addIcon("libgdx" + 64  + ".png", FileType.Internal);
        configuration.addIcon("libgdx" + 32  + ".png", FileType.Internal);
        configuration.addIcon("libgdx" + 16  + ".png", FileType.Internal);
        return configuration;
    }
}