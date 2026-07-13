package org.physics.engine.math;

/**
 * A vector in the plane: a pair of numbers (x, y). One object, two meanings. It can stand for a
 * position (a point in space, measured from the origin) or for a direction with a size, such as a
 * velocity or a force. Almost everything in the engine is built from these.
 *
 * <p>A vector is immutable: every operation returns a new {@code Vector2} rather than changing this
 * one. That may seem wasteful, but it removes a whole class of bugs where two parts of the program
 * accidentally share and modify the same vector. It also makes the code read like the maths: {@code
 * a.add(b)} produces a new vector, exactly as {@code a + b} would on paper.
 */
public record Vector2(double x, double y) {

  /** The zero vector, (0, 0). Useful as a starting point for sums and as "no movement". */
  public static final Vector2 ZERO = new Vector2(0, 0);

  /** A readable alternative to the constructor: {@code Vector2.of(3, 4)}. */
  public static Vector2 of(double x, double y) {
    return new Vector2(x, y);
  }

  /** Adds two vectors component by component. Combining a position and a movement, for example. */
  public Vector2 add(Vector2 other) {
    return new Vector2(x + other.x, y + other.y);
  }

  /**
   * Subtracts another vector from this one. {@code target.subtract(source)} is the vector that
   * points from {@code source} to {@code target}, which we use constantly to find directions.
   */
  public Vector2 subtract(Vector2 other) {
    return new Vector2(x - other.x, y - other.y);
  }

  /** Multiplies both components by a single number, stretching or shrinking the vector. */
  public Vector2 scale(double factor) {
    return new Vector2(x * factor, y * factor);
  }

  /** Returns a vector of the same length pointing the opposite way. */
  public Vector2 negate() {
    return new Vector2(-x, -y);
  }

  /**
   * The dot product. It is large and positive when the two vectors point the same way, zero when
   * they are perpendicular, and negative when they point apart. We use it to project one vector
   * onto another, which is central to bouncing objects off surfaces later.
   */
  public double dot(Vector2 other) {
    return x * other.x + y * other.y;
  }

  /**
   * The 2D cross product, a single number giving the signed area of the parallelogram formed by the
   * two vectors. Its sign tells us whether {@code other} lies to the left (positive) or right
   * (negative) of this vector, which is a cheap way to ask "which way does this turn?".
   */
  public double cross(Vector2 other) {
    return x * other.y - y * other.x;
  }

  /**
   * The squared length. Cheaper than {@link #length()} because it skips the square root, so we use
   * it whenever we only need to compare distances rather than know the exact one.
   */
  public double lengthSquared() {
    return x * x + y * y;
  }

  /** The length (magnitude) of the vector, by Pythagoras. */
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  /** The straight-line distance from this point to another. */
  public double distanceTo(Vector2 other) {
    return subtract(other).length();
  }

  /**
   * A vector pointing the same way but exactly one unit long, which is how we express a pure
   * direction. The zero vector has no direction, so normalizing it is undefined and rejected rather
   * than silently returning something wrong.
   */
  public Vector2 normalized() {
    double length = length();
    if (length == 0) {
      throw new ArithmeticException("cannot normalize a zero-length vector");
    }
    return new Vector2(x / length, y / length);
  }
}
