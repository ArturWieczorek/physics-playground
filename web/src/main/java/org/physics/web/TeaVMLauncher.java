package org.physics.web;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import org.physics.app.PlaygroundApp;

/**
 * The entry point when the app runs in a browser. It is the web counterpart of the desktop's {@code
 * DesktopLauncher}: it configures the drawing surface (an HTML canvas) and hands the very same
 * {@link PlaygroundApp} to the TeaVM backend, which drives it in JavaScript.
 *
 * <p>A width and height of zero tell the backend to fill the whole canvas.
 */
public class TeaVMLauncher {

  public static void main(String[] args) {
    TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
    config.width = 0;
    config.height = 0;
    config.antialiasing = true; // ask the browser for a smoothed WebGL canvas
    new TeaApplication(new PlaygroundApp(), config);
  }
}
