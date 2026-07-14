package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import org.physics.engine.cloth.Cloth;
import org.physics.engine.cloth.ClothPoint;
import org.physics.engine.cloth.Stick;
import org.physics.engine.math.Vector2;

/**
 * A hanging sheet of cloth you can play with, and the first of the two showpieces. It is a grid of
 * points held together by threads (ch12), pinned along the top so it drapes like a curtain. Grab it
 * and it swings and folds; yank a fold hard and the threads snap and it tears.
 *
 * <p>Two modes, switched with the space bar. In grab mode (the default) dragging pulls the nearest
 * bit of cloth around. In cut mode dragging slices through the threads like scissors. Press R to
 * hang a fresh sheet.
 */
public class ClothScene implements Scene {

  private static final float FIXED_DT = 1f / 60f;
  private static final int ITERATIONS = 6;
  private static final int COLUMNS = 30;
  private static final int ROWS = 20;
  private static final double SPACING = 0.35;
  private static final Vector2 GRAVITY = new Vector2(0, -9.8);
  private static final double CUT_RADIUS = 0.4;

  private Cloth cloth;
  private float timeBudget;
  private boolean cutMode;

  private ClothPoint grabbed;
  private boolean grabbedWasPinned;

  @Override
  public String title() {
    return "Cloth: tearable Verlet sheet";
  }

  @Override
  public String controls() {
    return "space: " + (cutMode ? "[cut mode]" : "[grab mode]") + "   drag: pull or slash";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    double startX = (16 - (COLUMNS - 1) * SPACING) / 2.0;
    cloth = new Cloth(COLUMNS, ROWS, SPACING, new Vector2(startX, 8.4), 4.0);
    cloth.pinTopRow();
    timeBudget = 0f;
    grabbed = null;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      cloth.step(GRAVITY, FIXED_DT, ITERATIONS);
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.SPACE) {
      cutMode = !cutMode;
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    Vector2 p = new Vector2(worldX, worldY);
    if (cutMode) {
      cloth.tearNear(p, CUT_RADIUS);
      return;
    }
    grabbed = cloth.nearest(p);
    grabbedWasPinned = grabbed.isPinned();
    grabbed.pin(); // hold it still while we drag it
    moveGrabbed(p);
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    Vector2 p = new Vector2(worldX, worldY);
    if (cutMode) {
      cloth.tearNear(p, CUT_RADIUS);
    } else if (grabbed != null) {
      moveGrabbed(p);
    }
  }

  private void moveGrabbed(Vector2 p) {
    grabbed.setPosition(p);
    grabbed.setPrevious(p); // no built-up velocity while held
  }

  @Override
  public void pointerUp() {
    if (grabbed != null) {
      if (!grabbedWasPinned) {
        grabbed.unpin(); // let it fall again unless it was a top pin
      }
      grabbed = null;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    for (Stick stick : cloth.sticks()) {
      if (stick.isBroken()) {
        continue;
      }
      Vector2 a = stick.a().position();
      Vector2 b = stick.b().position();
      // Colour by strain: calm blue at rest, warming toward red as a thread stretches.
      double strain = Math.min(1.0, (a.distanceTo(b) / stick.restLength() - 1.0) * 2.0);
      strain = Math.max(0.0, strain);
      shapes.setColor(
          (float) (0.45 + 0.55 * strain),
          (float) (0.75 - 0.45 * strain),
          (float) (0.95 - 0.75 * strain),
          1f);
      Draw.line(shapes, a.x(), a.y(), b.x(), b.y(), 0.03f);
    }
    shapes.end();
  }
}
