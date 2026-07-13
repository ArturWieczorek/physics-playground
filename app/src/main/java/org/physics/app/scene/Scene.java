package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * One experiment you can look at and poke: the spring, the gas, the orbits, and so on. The
 * application keeps a current scene, updates it every frame, and draws it. Scenes share this small
 * shape so the app can treat them all the same way.
 *
 * <p>Positions are in world units. The app hands pointer coordinates already converted from screen
 * pixels into those same world units, so a scene never has to think about the window size.
 */
public interface Scene {

  /** A short name for the scene, shown to the user once we add a menu. */
  String title();

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

  /** Releases any resources when the scene goes away. */
  default void dispose() {}
}
