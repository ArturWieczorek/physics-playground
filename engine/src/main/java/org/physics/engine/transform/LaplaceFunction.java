package org.physics.engine.transform;

/**
 * The Laplace transform of a few simple signals, seen as a landscape over the complex plane. The
 * Laplace transform turns a function of time {@code f(t)} into a function {@code F(s)} of a complex
 * variable {@code s = sigma + i*omega}:
 *
 * <pre>
 *   F(s) = integral from 0 to infinity of f(t) e^(-s t) dt
 * </pre>
 *
 * <p>The magic is in the poles: the special points of {@code s} where {@code F(s)} blows up to
 * infinity. They pin down everything about the signal. An oscillation at frequency 1, like {@code
 * cos(t)}, has poles exactly at {@code s = +/- i} (on the vertical axis, at height 1); a decaying
 * signal like {@code e^(-t)} has its pole out on the negative real axis at {@code s = -1}, and the
 * further left the pole, the faster the decay. Plotting the height of {@code |F(s)|} over the plane
 * turns those poles into spikes shooting to the sky, which is exactly what the scene draws.
 *
 * <p>Each transform below has a well-known closed form, and we return its magnitude {@code |F(s)|}
 * directly.
 */
public enum LaplaceFunction {

  /** cos(t): transform s / (s^2 + 1), with poles at s = +/- i. */
  COSINE("cos(t)") {
    @Override
    public double magnitude(double sigma, double omega) {
      return complexAbs(sigma, omega) / absOfSquarePlusOne(sigma, omega);
    }
  },

  /** sin(t): transform 1 / (s^2 + 1), same poles at s = +/- i. */
  SINE("sin(t)") {
    @Override
    public double magnitude(double sigma, double omega) {
      return 1.0 / absOfSquarePlusOne(sigma, omega);
    }
  },

  /** e^(-t): transform 1 / (s + 1), a single pole at s = -1. */
  DECAY("e^-t") {
    @Override
    public double magnitude(double sigma, double omega) {
      return 1.0 / complexAbs(sigma + 1, omega);
    }
  },

  /** the constant 1: transform 1 / s, a single pole at the origin. */
  UNIT("1") {
    @Override
    public double magnitude(double sigma, double omega) {
      return 1.0 / complexAbs(sigma, omega);
    }
  };

  private final String label;

  LaplaceFunction(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  /** The size of the transform, {@code |F(s)|}, at {@code s = sigma + i*omega}. */
  public abstract double magnitude(double sigma, double omega);

  static double complexAbs(double real, double imaginary) {
    return Math.sqrt(real * real + imaginary * imaginary);
  }

  // |s^2 + 1| where s = sigma + i*omega. s^2 = (sigma^2 - omega^2) + i(2 sigma omega).
  static double absOfSquarePlusOne(double sigma, double omega) {
    double real = sigma * sigma - omega * omega + 1;
    double imaginary = 2 * sigma * omega;
    return complexAbs(real, imaginary);
  }
}
