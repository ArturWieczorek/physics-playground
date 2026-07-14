package org.physics.engine.fluid;

/**
 * The smoothing kernels at the heart of smoothed-particle hydrodynamics (ch13). In SPH a fluid is
 * made of particles, but a particle on its own is just a dot; the fluid's properties (how dense it
 * is, how it presses, how it drags) only make sense as a blur over all the particles nearby. A
 * kernel is the recipe for that blur: a bump-shaped weighting that counts close neighbours fully
 * and distant ones not at all, fading smoothly to zero at a radius {@code h}.
 *
 * <p>Different jobs want differently shaped bumps, so there are three, each a standard choice from
 * the SPH literature (the constants are the two-dimensional normalisations):
 *
 * <ul>
 *   <li>{@code poly6} for measuring density: smooth and rounded.
 *   <li>{@code spikyGradient} for pressure: sharp near the centre so crowded particles push back
 *       firmly and never pile up.
 *   <li>{@code viscosityLaplacian} for viscosity: gives the smearing that makes the fluid gooey.
 * </ul>
 */
public final class Kernels {

  private Kernels() {}

  /**
   * The poly6 kernel, used to add up density. Highest at the centre, zero at and beyond {@code h}.
   */
  public static double poly6(double r, double h) {
    if (r < 0 || r >= h) {
      return 0;
    }
    double diff = h * h - r * r;
    return (4.0 / Math.PI) / Math.pow(h, 8) * diff * diff * diff;
  }

  /**
   * The size of the spiky kernel's gradient at distance {@code r}. It is returned as a single
   * (negative) number meant to multiply the unit vector between two particles, giving the direction
   * and strength of the pressure push. It is sharpest near the centre, which is what stops
   * particles from collapsing into one another.
   */
  public static double spikyGradient(double r, double h) {
    if (r <= 0 || r >= h) {
      return 0;
    }
    double diff = h - r;
    return -(30.0 / Math.PI) / Math.pow(h, 5) * diff * diff;
  }

  /**
   * The viscosity kernel's Laplacian, which drives the smoothing of velocity between neighbours.
   */
  public static double viscosityLaplacian(double r, double h) {
    if (r < 0 || r >= h) {
      return 0;
    }
    return (40.0 / Math.PI) / Math.pow(h, 5) * (h - r);
  }
}
