package org.physics.engine.pendulum;

import org.physics.engine.math.Vector2;

/**
 * A double pendulum: one pendulum hanging off the end of another. It is the classic example of
 * chaos. Its motion is completely determined by Newton's laws, with no randomness anywhere, yet the
 * tiniest change in where it starts leads to a wildly different path within seconds. It is
 * predictable in principle and unpredictable in practice.
 *
 * <p>Unlike the rest of the engine, this is not built from particles and forces. The two rigid rods
 * make that awkward, so instead we track the two angles directly and advance them with the
 * pendulum's known equations of motion, using the accurate fourth-order Runge-Kutta method (a more
 * careful cousin of the integrators in ch03) so the energy stays steady over long runs.
 */
public class DoublePendulum {

  private final double m1;
  private final double m2;
  private final double l1;
  private final double l2;
  private final double gravity;

  private double angle1; // measured from straight down
  private double speed1; // angular velocity of the first rod
  private double angle2;
  private double speed2;

  public DoublePendulum(
      double m1, double m2, double l1, double l2, double gravity, double angle1, double angle2) {
    this.m1 = m1;
    this.m2 = m2;
    this.l1 = l1;
    this.l2 = l2;
    this.gravity = gravity;
    this.angle1 = angle1;
    this.angle2 = angle2;
  }

  /** Sets both rods to given angles and stops them dead. */
  public void setAngles(double angle1, double angle2) {
    this.angle1 = angle1;
    this.angle2 = angle2;
    this.speed1 = 0;
    this.speed2 = 0;
  }

  /** Advances the pendulum by {@code dt} using fourth-order Runge-Kutta. */
  public void step(double dt) {
    double[] state = {angle1, speed1, angle2, speed2};
    double[] k1 = derivative(state);
    double[] k2 = derivative(addScaled(state, k1, dt / 2));
    double[] k3 = derivative(addScaled(state, k2, dt / 2));
    double[] k4 = derivative(addScaled(state, k3, dt));
    for (int i = 0; i < 4; i++) {
      state[i] += dt / 6 * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
    }
    angle1 = state[0];
    speed1 = state[1];
    angle2 = state[2];
    speed2 = state[3];
  }

  // The equations of motion: given [angle1, speed1, angle2, speed2], return their rates of change.
  private double[] derivative(double[] s) {
    double a1 = s[0];
    double w1 = s[1];
    double a2 = s[2];
    double w2 = s[3];
    double delta = a1 - a2;
    double denom = 2 * m1 + m2 - m2 * Math.cos(2 * a1 - 2 * a2);

    double acc1 =
        (-gravity * (2 * m1 + m2) * Math.sin(a1)
                - m2 * gravity * Math.sin(a1 - 2 * a2)
                - 2 * Math.sin(delta) * m2 * (w2 * w2 * l2 + w1 * w1 * l1 * Math.cos(delta)))
            / (l1 * denom);
    double acc2 =
        (2
                * Math.sin(delta)
                * (w1 * w1 * l1 * (m1 + m2)
                    + gravity * (m1 + m2) * Math.cos(a1)
                    + w2 * w2 * l2 * m2 * Math.cos(delta)))
            / (l2 * denom);
    return new double[] {w1, acc1, w2, acc2};
  }

  private static double[] addScaled(double[] s, double[] d, double h) {
    return new double[] {s[0] + d[0] * h, s[1] + d[1] * h, s[2] + d[2] * h, s[3] + d[3] * h};
  }

  /** Position of the first (upper) bob, given the pivot. */
  public Vector2 bob1(Vector2 pivot) {
    return pivot.add(new Vector2(l1 * Math.sin(angle1), -l1 * Math.cos(angle1)));
  }

  /** Position of the second (lower) bob, given the pivot. */
  public Vector2 bob2(Vector2 pivot) {
    return bob1(pivot).add(new Vector2(l2 * Math.sin(angle2), -l2 * Math.cos(angle2)));
  }

  /** Total mechanical energy, kinetic plus gravitational. Should stay nearly constant. */
  public double energy() {
    double v1Squared = l1 * l1 * speed1 * speed1;
    double v2Squared =
        l1 * l1 * speed1 * speed1
            + l2 * l2 * speed2 * speed2
            + 2 * l1 * l2 * speed1 * speed2 * Math.cos(angle1 - angle2);
    double kinetic = 0.5 * m1 * v1Squared + 0.5 * m2 * v2Squared;
    double y1 = -l1 * Math.cos(angle1);
    double y2 = y1 - l2 * Math.cos(angle2);
    double potential = m1 * gravity * y1 + m2 * gravity * y2;
    return kinetic + potential;
  }
}
