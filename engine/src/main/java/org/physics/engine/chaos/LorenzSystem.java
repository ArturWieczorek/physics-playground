package org.physics.engine.chaos;

/**
 * The Lorenz system, the birthplace of chaos theory. In 1963 Edward Lorenz was modelling convection
 * in the atmosphere with three simple equations and noticed that starting them from almost the same
 * place led to completely different weather. That was the discovery of the butterfly effect: tiny
 * changes in the start snowball into huge changes later, so long-range prediction is hopeless even
 * for a perfectly known system.
 *
 * <p>The three equations are:
 *
 * <pre>
 *   dx/dt = sigma (y - x)
 *   dy/dt = x (rho - z) - y
 *   dz/dt = x y - beta z
 * </pre>
 *
 * <p>Left to run, the point never settles and never repeats, yet it never flies off either: it
 * loops forever around two centres, tracing the famous butterfly-wing shape called a strange
 * attractor. We advance it with the accurate fourth-order Runge-Kutta method from ch16.
 */
public class LorenzSystem {

  private final double sigma;
  private final double rho;
  private final double beta;

  private double x;
  private double y;
  private double z;

  /** The classic parameters (10, 28, 8/3) that give the butterfly. */
  public LorenzSystem(double x, double y, double z) {
    this(10.0, 28.0, 8.0 / 3.0, x, y, z);
  }

  public LorenzSystem(double sigma, double rho, double beta, double x, double y, double z) {
    this.sigma = sigma;
    this.rho = rho;
    this.beta = beta;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  /** Advances the system by {@code dt} using fourth-order Runge-Kutta. */
  public void step(double dt) {
    double[] s = {x, y, z};
    double[] k1 = derivative(s);
    double[] k2 = derivative(addScaled(s, k1, dt / 2));
    double[] k3 = derivative(addScaled(s, k2, dt / 2));
    double[] k4 = derivative(addScaled(s, k3, dt));
    x += dt / 6 * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
    y += dt / 6 * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);
    z += dt / 6 * (k1[2] + 2 * k2[2] + 2 * k3[2] + k4[2]);
  }

  private double[] derivative(double[] s) {
    return new double[] {
      sigma * (s[1] - s[0]), s[0] * (rho - s[2]) - s[1], s[0] * s[1] - beta * s[2]
    };
  }

  private static double[] addScaled(double[] s, double[] d, double h) {
    return new double[] {s[0] + d[0] * h, s[1] + d[1] * h, s[2] + d[2] * h};
  }
}
