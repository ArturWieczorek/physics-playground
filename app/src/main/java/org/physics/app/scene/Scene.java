package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.List;

/**
 * One experiment you can look at and poke: the spring, the gas, the orbits, and so on. The
 * application keeps a current scene, updates it every frame, and draws it. Scenes share this small
 * shape so the app can treat them all the same way.
 *
 * <p>Positions are in world units. The app hands pointer coordinates already converted from screen
 * pixels into those same world units, so a scene never has to think about the window size.
 */
public interface Scene {

  /** A short name for the scene, shown in the heads-up display. */
  String title();

  /**
   * A short hint describing this scene's own controls (the keys and mouse actions it responds to),
   * shown in the heads-up display. The app adds the shared controls (scene switching, reset, pause)
   * itself, so a scene only lists what is special to it.
   */
  default String controls() {
    return "";
  }

  /**
   * Live numbers to show on screen, one per line, such as temperature or total energy. Returning
   * these lets the app display the very quantities the physics is conserving, which is the whole
   * point of a physics demo. Empty by default.
   */
  default List<String> readouts() {
    return List.of();
  }

  /** Advances the simulation by {@code dt} seconds (the real time since the last frame). */
  void update(float dt);

  /** Draws the current state. The projection is already set to world units. */
  void render(ShapeRenderer shapes);

  /** Called once when the scene becomes active. Sets up its world. */
  default void show() {}

  /** Puts the scene back to its starting state. Bound to the R key by the app. */
  default void reset() {}

  /** The pointer was just pressed, at the given world position. */
  default void pointerDown(float worldX, float worldY) {}

  /** The pointer moved while held down. */
  default void pointerDrag(float worldX, float worldY) {}

  /** The pointer was released. */
  default void pointerUp() {}

  /** A key was pressed. The app forwards a handful of keys (the arrows and space) to the scene. */
  default void keyPressed(int keycode) {}

  /** Releases any resources when the scene goes away. */
  default void dispose() {}
}
