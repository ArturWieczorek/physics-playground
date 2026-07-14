package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A few small drawing helpers shared by the scenes. Their job is to make lines actually visible:
 * plain one-pixel lines look thin and jagged, so instead we draw them as thin filled rectangles
 * with {@code rectLine}, which have real width and get smoothed by anti-aliasing.
 *
 * <p>All methods assume the {@link ShapeRenderer} is already between {@code begin(Filled)} and
 * {@code end()}, and that the colour has been set by the caller.
 */
final class Draw {

  private Draw() {}

  /**
   * Formats a number to a fixed number of decimals without pulling in locale machinery. We avoid
   * String.format here on purpose: on the web build it would drag in a large pile of locale and
   * currency data we do not need.
   */
  static String num(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    return Double.toString(Math.round(value * factor) / factor);
  }

  /** A line with real thickness, in world units. */
  static void line(ShapeRenderer shapes, double x1, double y1, double x2, double y2, float width) {
    shapes.rectLine((float) x1, (float) y1, (float) x2, (float) y2, width);
  }

  /** A thick line with an arrowhead at the (x2, y2) end. */
  static void arrow(
      ShapeRenderer shapes, double x1, double y1, double x2, double y2, float width, double head) {
    shapes.rectLine((float) x1, (float) y1, (float) x2, (float) y2, width);
    double dx = x2 - x1;
    double dy = y2 - y1;
    double len = Math.hypot(dx, dy);
    if (len < 1e-9) {
      return;
    }
    double ux = dx / len;
    double uy = dy / len;
    double cos = Math.cos(Math.toRadians(30));
    double sin = Math.sin(Math.toRadians(30));
    // Two short strokes back from the tip, rotated either side of the incoming direction.
    double bx = -ux;
    double by = -uy;
    shapes.rectLine(
        (float) x2,
        (float) y2,
        (float) (x2 + (bx * cos - by * sin) * head),
        (float) (y2 + (bx * sin + by * cos) * head),
        width);
    shapes.rectLine(
        (float) x2,
        (float) y2,
        (float) (x2 + (bx * cos + by * sin) * head),
        (float) (y2 + (-bx * sin + by * cos) * head),
        width);
  }

  /** The outline of a rectangle, drawn as four thick lines. Used to show a scene's walls. */
  static void box(
      ShapeRenderer shapes, double left, double bottom, double right, double top, float width) {
    shapes.rectLine((float) left, (float) bottom, (float) right, (float) bottom, width);
    shapes.rectLine((float) right, (float) bottom, (float) right, (float) top, width);
    shapes.rectLine((float) right, (float) top, (float) left, (float) top, width);
    shapes.rectLine((float) left, (float) top, (float) left, (float) bottom, width);
  }
}
