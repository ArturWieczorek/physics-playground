package org.physics.engine.cloth;

import java.util.ArrayList;
import java.util.List;
import org.physics.engine.math.Vector2;

/**
 * A sheet of cloth: a grid of {@link ClothPoint}s joined by {@link Stick}s. It is the payoff of the
 * whole "constraints instead of forces" idea from ch06. Rather than computing a spring force for
 * every thread, we simply move the points until the threads are the right length again, and repeat
 * that a few times per step. Fake it enough times and it looks exactly right, and it never explodes
 * the way stiff springs would.
 *
 * <p>Each step has two phases:
 *
 * <ol>
 *   <li><b>Move</b> every free point by Verlet integration: carry it forward by however far it
 *       moved last step, plus a nudge from gravity.
 *   <li><b>Relax</b>: walk over the threads several times, each time tugging their two ends back
 *       toward the rest length. More passes make a stiffer, less stretchy cloth.
 * </ol>
 */
public class Cloth {

  private final List<ClothPoint> points = new ArrayList<>();
  private final List<Stick> sticks = new ArrayList<>();
  private final int columns;
  private final int rows;
  private static final double DAMPING = 0.99;

  /**
   * Builds a rectangular sheet. The grid hangs down and to the right from {@code topLeft}. A thread
   * snaps if stretched past {@code tearFactor} times its rest length.
   */
  public Cloth(int columns, int rows, double spacing, Vector2 topLeft, double tearFactor) {
    this.columns = columns;
    this.rows = rows;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        points.add(
            new ClothPoint(new Vector2(topLeft.x() + c * spacing, topLeft.y() - r * spacing)));
      }
    }
    double tearLength = spacing * tearFactor;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        if (c < columns - 1) {
          sticks.add(new Stick(at(c, r), at(c + 1, r), spacing, tearLength)); // horizontal thread
        }
        if (r < rows - 1) {
          sticks.add(new Stick(at(c, r), at(c, r + 1), spacing, tearLength)); // vertical thread
        }
      }
    }
  }

  private ClothPoint at(int column, int row) {
    return points.get(row * columns + column);
  }

  /** Pins the whole top edge so the cloth hangs from it like a curtain. */
  public void pinTopRow() {
    for (int c = 0; c < columns; c++) {
      at(c, 0).pin();
    }
  }

  public List<ClothPoint> points() {
    return points;
  }

  public List<Stick> sticks() {
    return sticks;
  }

  /**
   * Advances the cloth by one step: move the points, then relax the threads {@code iterations}
   * times.
   */
  public void step(Vector2 gravity, double dt, int iterations) {
    double dt2 = dt * dt;
    for (ClothPoint point : points) {
      if (point.isPinned()) {
        continue;
      }
      Vector2 current = point.position();
      Vector2 velocity = current.subtract(point.previous()).scale(DAMPING);
      point.setPrevious(current);
      point.setPosition(current.add(velocity).add(gravity.scale(dt2)));
    }
    relax(iterations);
  }

  /** Runs only the thread-length constraints, without moving anything under gravity. */
  public void relax(int iterations) {
    for (int i = 0; i < iterations; i++) {
      for (Stick stick : sticks) {
        satisfy(stick);
      }
    }
  }

  private void satisfy(Stick stick) {
    if (stick.isBroken()) {
      return;
    }
    ClothPoint a = stick.a();
    ClothPoint b = stick.b();
    Vector2 delta = b.position().subtract(a.position());
    double distance = delta.length();
    if (distance == 0) {
      return;
    }
    if (distance > stick.tearLength()) {
      stick.breakStick();
      return;
    }
    double totalWeight = a.weight() + b.weight();
    if (totalWeight == 0) {
      return; // both ends pinned
    }
    double difference = (distance - stick.restLength()) / distance;
    // Each end moves toward the other in proportion to how freely it can move.
    a.setPosition(a.position().add(delta.scale((a.weight() / totalWeight) * difference)));
    b.setPosition(b.position().subtract(delta.scale((b.weight() / totalWeight) * difference)));
  }

  /** The cloth point closest to a given position (used to grab the cloth with the mouse). */
  public ClothPoint nearest(Vector2 target) {
    ClothPoint best = null;
    double bestDistance = Double.MAX_VALUE;
    for (ClothPoint point : points) {
      double distance = point.position().distanceTo(target);
      if (distance < bestDistance) {
        bestDistance = distance;
        best = point;
      }
    }
    return best;
  }

  /**
   * Cuts any thread whose middle lies within {@code radius} of a point (used to slash the cloth).
   */
  public void tearNear(Vector2 target, double radius) {
    for (Stick stick : sticks) {
      if (stick.isBroken()) {
        continue;
      }
      Vector2 middle = stick.a().position().add(stick.b().position()).scale(0.5);
      if (middle.distanceTo(target) < radius) {
        stick.breakStick();
      }
    }
  }
}
