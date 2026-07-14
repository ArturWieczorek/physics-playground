package org.physics.engine.pendulum;

/**
 * A single pendulum swinging through small angles. Unlike the chaotic double pendulum of ch16, one
 * pendulum is perfectly regular: it is simple harmonic motion (the swing version of ch05's spring).
 * Its most striking feature is that the time for a swing, the period, depends only on the length
 * and gravity, not on how far it swings or how heavy the bob is:
 *
 * <pre>
 *   period = 2 * pi * sqrt(length / gravity)
 * </pre>
 *
 * <p>A longer pendulum swings more slowly. That is the one fact the pendulum-wave scene is built
 * on: a row of pendulums of carefully chosen lengths drift in and out of step with one another,
 * making travelling-wave patterns ripple along the row.
 */
public class SimplePendulum {

  private final double length;
  private final double gravity;
  private final double amplitude;

  public SimplePendulum(double length, double gravity, double amplitude) {
    if (length <= 0 || gravity <= 0) {
      throw new IllegalArgumentException("length and gravity must be positive");
    }
    this.length = length;
    this.gravity = gravity;
    this.amplitude = amplitude;
  }

  public double length() {
    return length;
  }

  /** How fast it swings, in radians per second: sqrt(gravity / length). */
  public double angularFrequency() {
    return Math.sqrt(gravity / length);
  }

  /** The time for one full swing there and back. */
  public double period() {
    return 2 * Math.PI / angularFrequency();
  }

  /** The angle from straight down at time t, released from the amplitude at rest. */
  public double angleAt(double t) {
    return amplitude * Math.cos(angularFrequency() * t);
  }
}
