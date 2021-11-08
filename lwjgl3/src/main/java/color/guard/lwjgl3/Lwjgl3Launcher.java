package color.guard.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import color.guard.ColorGuardGame;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new ColorGuardGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("ColorGuard");
        configuration.useVsync(true);
        configuration.disableAudio(true);
        configuration.setIdleFPS(60);
        configuration.setForegroundFPS(60);
//        configuration.setHdpiMode(HdpiMode.Logical);
        configuration.setWindowedMode(800, 450);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}