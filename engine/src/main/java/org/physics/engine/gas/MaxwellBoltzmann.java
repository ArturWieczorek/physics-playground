package org.physics.engine.gas;

import java.util.List;
import org.physics.engine.core.Particle;

/**
 * The statistics of a gas. A box of particles bouncing at random has no single speed: some are
 * fast, some slow, and the spread of speeds settles into a famous shape, the Maxwell-Boltzmann
 * distribution. This class holds the two facts that connect the jiggling of individual particles to
 * the everyday idea of temperature.
 *
 * <p>We work in two dimensions and choose units where Boltzmann's constant is 1, which keeps the
 * formulas clean. In 2D a particle has two ways to move (across and up), and each carries an
 * average energy of half the temperature. Add them and you get the tidy headline result:
 *
 * <pre>
 *   average kinetic energy per particle = temperature
 * </pre>
 *
 * <p>So "temperature" here is simply the average energy of motion. Heat the gas and the particles
 * speed up; cool it and they slow down. That is all temperature ever was.
 */
public final class MaxwellBoltzmann {

  private MaxwellBoltzmann() {}

  /**
   * The average kinetic energy of the particles. In 2D units with k = 1, this is the temperature.
   */
  public static double temperature(List<Particle> bodies) {
    if (bodies.isEmpty()) {
      throw new IllegalArgumentException("cannot take the temperature of an empty gas");
    }
    double total = 0;
    for (Particle body : bodies) {
      total += body.kineticEnergy();
    }
    return total / bodies.size();
  }

  /**
   * How likely a particle is to have a given speed, for a gas at the given temperature. In 2D this
   * is the Rayleigh distribution:
   *
   * <pre>
   *   f(v) = (m v / T) * exp(-m v^2 / (2 T))
   * </pre>
   *
   * It is zero at rest, rises to a peak, then tails off: very slow and very fast particles are both
   * rare, and there is a most-common speed in between. This is the bell-like curve we draw over the
   * live histogram in the scene.
   */
  public static double speedProbabilityDensity(double speed, double temperature, double mass) {
    if (speed < 0) {
      return 0;
    }
    return (mass * speed / temperature) * Math.exp(-mass * speed * speed / (2 * temperature));
  }

  /**
   * The average speed of particles in a gas at this temperature, {@code sqrt(pi T / (2 m))}. Note
   * this is not the same as the most likely speed nor the root-mean-square speed; a lopsided
   * distribution has several different "typical" values.
   */
  public static double meanSpeed(double temperature, double mass) {
    return Math.sqrt(Math.PI * temperature / (2 * mass));
  }
}
