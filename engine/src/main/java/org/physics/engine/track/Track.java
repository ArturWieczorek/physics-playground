package org.physics.engine.track;

import java.util.List;
import org.physics.engine.math.Vector2;

/**
 * A fixed, smooth wire that a bead can slide along, stored as a chain of closely spaced points (a
 * polyline). Given a distance travelled along the wire (an arc length), the track can say where
 * that is and which way the wire points there. That is all a sliding bead needs.
 *
 * <p>This is the geometry behind the brachistochrone in ch15: several wires of different shapes run
 * between the same two points, and a bead races down each.
 */
public class Track {

  private final Vector2[] points;
  private final double[] cumulative; // arc length from the start up to each point
  private final double length;

  public Track(List<Vector2> pointList) {
    if (pointList.size() < 2) {
      throw new IllegalArgumentException("a track needs at least two points");
    }
    points = pointList.toArray(new Vector2[0]);
    cumulative = new double[points.length];
    cumulative[0] = 0;
    for (int i = 1; i < points.length; i++) {
      cumulative[i] = cumulative[i - 1] + points[i].distanceTo(points[i - 1]);
    }
    length = cumulative[cumulative.length - 1];
  }

  /** The total length of the wire. */
  public double length() {
    return length;
  }

  /** The point on the wire a distance {@code s} along it. */
  public Vector2 positionAt(double s) {
    int i = segmentContaining(s);
    double segmentLength = cumulative[i + 1] - cumulative[i];
    double t = segmentLength == 0 ? 0 : (s - cumulative[i]) / segmentLength;
    return points[i].scale(1 - t).add(points[i + 1].scale(t));
  }

  /**
   * A unit vector pointing along the wire (in the direction of increasing {@code s}) at {@code s}.
   */
  public Vector2 tangentAt(double s) {
    int i = segmentContaining(s);
    Vector2 step = points[i + 1].subtract(points[i]);
    double len = step.length();
    return len == 0 ? new Vector2(1, 0) : step.scale(1 / len);
  }

  /** The raw points, for drawing the wire. */
  public Vector2[] points() {
    return points;
  }

  private int segmentContaining(double s) {
    double clamped = Math.max(0, Math.min(length, s));
    // Find the last point whose cumulative length does not exceed s.
    for (int i = points.length - 2; i >= 0; i--) {
      if (cumulative[i] <= clamped) {
        return i;
      }
    }
    return 0;
  }
}
