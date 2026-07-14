package org.physics.engine.track;

import java.util.ArrayList;
import java.util.List;
import org.physics.engine.math.Vector2;

/**
 * Builders for the three racing wires of the brachistochrone problem, each running from a high
 * start point to a lower end point:
 *
 * <ul>
 *   <li>a straight ramp, the obvious guess,
 *   <li>a gently bowed arc, and
 *   <li>a cycloid, the curve traced by a point on a rolling wheel.
 * </ul>
 *
 * <p>The astonishing answer, found by Johann Bernoulli in 1696, is that the cycloid is the fastest
 * of all possible shapes, faster than the straight line even though the straight line is shorter.
 * The bead on the cycloid drops steeply at first, trading height for speed early, and that early
 * speed more than pays for the longer path. ch15 lets you watch the race.
 */
public final class Curves {

  private Curves() {}

  /** A straight ramp from {@code start} to {@code end}. */
  public static Track straightLine(Vector2 start, Vector2 end, int samples) {
    List<Vector2> points = new ArrayList<>();
    for (int i = 0; i <= samples; i++) {
      double t = i / (double) samples;
      points.add(start.scale(1 - t).add(end.scale(t)));
    }
    return new Track(points);
  }

  /** A smooth arc bowing {@code sag} below the straight line between the two points. */
  public static Track arc(Vector2 start, Vector2 end, double sag, int samples) {
    // A quadratic curve with a control point pulled down below the midpoint gives a clean bow.
    Vector2 control = new Vector2((start.x() + end.x()) / 2, (start.y() + end.y()) / 2 - sag);
    List<Vector2> points = new ArrayList<>();
    for (int i = 0; i <= samples; i++) {
      double t = i / (double) samples;
      double a = (1 - t) * (1 - t);
      double b = 2 * (1 - t) * t;
      double c = t * t;
      double x = a * start.x() + b * control.x() + c * end.x();
      double y = a * start.y() + b * control.y() + c * end.y();
      points.add(new Vector2(x, y));
    }
    return new Track(points);
  }

  /**
   * The cycloid through the two points, with its cusp at {@code start} (which must be higher than
   * and to the left of {@code end}). This is the true brachistochrone.
   */
  public static Track cycloid(Vector2 start, Vector2 end, int samples) {
    double dx = end.x() - start.x();
    double drop = start.y() - end.y();
    if (dx <= 0 || drop <= 0) {
      throw new IllegalArgumentException("end must be below and to the right of start");
    }

    // Solve (theta - sin theta) / (1 - cos theta) = dx / drop for the end angle, by bisection.
    double target = dx / drop;
    double lo = 1e-6;
    double hi = 2 * Math.PI - 1e-6;
    for (int iter = 0; iter < 100; iter++) {
      double mid = (lo + hi) / 2;
      double ratio = (mid - Math.sin(mid)) / (1 - Math.cos(mid));
      if (ratio < target) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    double thetaEnd = (lo + hi) / 2;
    double radius = drop / (1 - Math.cos(thetaEnd));

    List<Vector2> points = new ArrayList<>();
    for (int i = 0; i <= samples; i++) {
      double theta = thetaEnd * i / samples;
      double x = start.x() + radius * (theta - Math.sin(theta));
      double y = start.y() - radius * (1 - Math.cos(theta));
      points.add(new Vector2(x, y));
    }
    return new Track(points);
  }
}
