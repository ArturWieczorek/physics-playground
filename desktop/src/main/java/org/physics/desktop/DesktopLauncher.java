package org.physics.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.physics.app.PlaygroundApp;

/**
 * Entry point for the native desktop build. It configures a window and then hands control to
 * LibGDX, which runs our {@link PlaygroundApp}.
 */
public final class DesktopLauncher {

  private DesktopLauncher() {
    // Not meant to be instantiated; this class only holds main().
  }

  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Physics Playground");
    config.setWindowedMode(1280, 720);
    config.useVsync(true);
    config.setForegroundFPS(60);
    // 4x multisampling smooths the edges of every shape and line we draw.
    config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
    new Lwjgl3Application(new PlaygroundApp(), config);
  }
}
