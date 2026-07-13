package org.physics.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The application itself, independent of how it is started. A launcher (desktop or web) hands this
 * to LibGDX, which then calls {@link #create()} once at startup and {@link #render()} every frame.
 *
 * <p>For now this is just an empty, dark window. That is on purpose: this chapter is about getting
 * the build, the formatter, and the tests working. From ch05 onward this class grows into a menu of
 * physics scenes.
 */
public class PlaygroundApp extends ApplicationAdapter {

  private static final float BACKGROUND_RED = 0.06f;
  private static final float BACKGROUND_GREEN = 0.07f;
  private static final float BACKGROUND_BLUE = 0.10f;

  @Override
  public void create() {
    // Nothing to set up yet.
  }

  @Override
  public void render() {
    // Called once per frame. All it does today is paint the window a dark blue-grey.
    ScreenUtils.clear(BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE, 1f);
  }

  @Override
  public void dispose() {
    // Nothing to release yet.
  }
}
