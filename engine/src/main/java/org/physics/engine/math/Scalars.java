package org.physics.engine.math;

/**
 * Small helpers for working with plain numbers (scalars, as opposed to vectors). These are used
 * throughout the engine, so they are the first thing we build and test.
 */
public final class Scalars {

  private Scalars() {
    // No instances: this is a bag of static helpers.
  }

  /**
   * Keeps a value inside a range. If it is below {@code min} we get {@code min}, if it is above
   * {@code max} we get {@code max}, otherwise the value is returned unchanged. Clamping shows up a
   * lot later, for example to stop a particle leaving the screen.
   */
  public static double clamp(double value, double min, double max) {
    if (min > max) {
      throw new IllegalArgumentException("min (" + min + ") must not exceed max (" + max + ")");
    }
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Tells whether two numbers are equal to within a small tolerance. Because a simulation works in
   * tiny approximate steps, we almost never compare floating point numbers with {@code ==}. This is
   * the honest way to ask "are these effectively the same?" and it is the backbone of our tests.
   */
  public static boolean approximatelyEqual(double a, double b, double tolerance) {
    if (tolerance < 0) {
      throw new IllegalArgumentException("tolerance must not be negative: " + tolerance);
    }
    return Math.abs(a - b) <= tolerance;
  }
}
