package org.physics.engine.fluid;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A fluid made of particles, using smoothed-particle hydrodynamics. This is the most ambitious
 * simulation in the course, yet it rests on ideas we have already met: measure something over the
 * neighbours (ch07's averaging), push where it is too crowded, drag toward the neighbours' motion.
 *
 * <p>Each step works in three passes:
 *
 * <ol>
 *   <li><b>Density</b>: for each particle, blur the masses of its neighbours with the poly6 kernel
 *       to get how tightly packed it is right there.
 *   <li><b>Pressure</b>: turn density into a pressure. Where the fluid is denser than it likes to
 *       be, pressure is high; that pressure is what will push the crowd apart.
 *   <li><b>Forces</b>: from those pressures, push each particle away from crowded neighbours
 *       (pressure), nudge it toward its neighbours' velocity (viscosity, the source of gooeyness),
 *       and add gravity. Then move everything.
 * </ol>
 *
 * <p>The pressure and viscosity forces are written in their symmetric form, so each pair of
 * particles pushes on each other equally and oppositely. That is Newton's third law again, and it
 * means the fluid conserves momentum, which a test checks directly.
 */
public class Sph {

  private final double smoothingRadius;
  private final double particleMass;
  private final double stiffness;
  private final double viscosity;
  private double restDensity;

  public Sph(
      double smoothingRadius,
      double particleMass,
      double restDensity,
      double stiffness,
      double viscosity) {
    this.smoothingRadius = smoothingRadius;
    this.particleMass = particleMass;
    this.restDensity = restDensity;
    this.stiffness = stiffness;
    this.viscosity = viscosity;
  }

  public double restDensity() {
    return restDensity;
  }

  /**
   * Lets a scene tune the rest density to match its starting layout, so the fluid begins at rest.
   */
  public void setRestDensity(double restDensity) {
    this.restDensity = restDensity;
  }

  /**
   * The density at each particle: how tightly packed the fluid is right there. Exposed on its own
   * so a scene can measure a starting layout, and so it can be tested directly.
   */
  public double[] densities(List<Particle> bodies) {
    int n = bodies.size();
    double[] density = new double[n];
    for (int i = 0; i < n; i++) {
      double sum = 0;
      Vector2 pi = bodies.get(i).position();
      for (int j = 0; j < n; j++) {
        double r = pi.distanceTo(bodies.get(j).position());
        sum += particleMass * Kernels.poly6(r, smoothingRadius);
      }
      density[i] = sum;
    }
    return density;
  }

  /**
   * Advances the fluid by {@code dt} seconds under {@code gravity}. Does not handle walls; a caller
   * applies bounds afterwards (we reuse BoxBounds from ch06).
   */
  public void step(List<Particle> bodies, Vector2 gravity, double dt) {
    int n = bodies.size();
    double[] density = densities(bodies);
    double[] pressure = new double[n];
    for (int i = 0; i < n; i++) {
      // A simple equation of state: pressure rises with how much denser than rest the fluid is.
      // Clamped at zero so the fluid pushes apart when crowded but does not suck together when
      // sparse.
      pressure[i] = Math.max(0.0, stiffness * (density[i] - restDensity));
    }

    Vector2[] force = new Vector2[n];
    for (int i = 0; i < n; i++) {
      force[i] = gravity.scale(particleMass); // body force (weight) on each particle
    }

    for (int i = 0; i < n; i++) {
      Particle pi = bodies.get(i);
      for (int j = i + 1; j < n; j++) {
        Particle pj = bodies.get(j);
        Vector2 rij = pi.position().subtract(pj.position()); // from j to i
        double r = rij.length();
        if (r == 0 || r >= smoothingRadius) {
          continue;
        }
        Vector2 unit = rij.scale(1.0 / r);
        Vector2 gradient = unit.scale(Kernels.spikyGradient(r, smoothingRadius)); // gradient at i

        // Symmetric pressure force: pushes the crowded pair apart, equal and opposite.
        double pressureCoeff =
            -particleMass
                * particleMass
                * (pressure[i] / (density[i] * density[i])
                    + pressure[j] / (density[j] * density[j]));
        Vector2 pressureForce = gradient.scale(pressureCoeff);

        // Symmetric viscosity force: drags the pair toward a common velocity, equal and opposite.
        double lap = Kernels.viscosityLaplacian(r, smoothingRadius);
        Vector2 relativeVelocity = pj.velocity().subtract(pi.velocity());
        Vector2 viscosityForce =
            relativeVelocity.scale(
                viscosity * particleMass * particleMass / (density[i] * density[j]) * lap);

        Vector2 pair = pressureForce.add(viscosityForce);
        force[i] = force[i].add(pair);
        force[j] = force[j].subtract(pair);
      }
    }

    // Move everything with semi-implicit Euler, the same stable step from ch03.
    for (int i = 0; i < n; i++) {
      Particle body = bodies.get(i);
      Vector2 acceleration = force[i].scale(1.0 / particleMass);
      Vector2 velocity = body.velocity().add(acceleration.scale(dt));
      body.setVelocity(velocity);
      body.setPosition(body.position().add(velocity.scale(dt)));
    }
  }
}
