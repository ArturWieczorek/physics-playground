package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The force between two atoms, the Lennard-Jones interaction. It captures, in one tidy formula, the
 * two things atoms do to each other: they pull together when a little apart, and push hard when
 * squeezed too close. The potential energy is
 *
 * <pre>
 *   V(r) = 4 * eps * ( (sigma/r)^12 - (sigma/r)^6 )
 * </pre>
 *
 * <p>The {@code r^6} term is the gentle long-range attraction (the reason atoms stick at all); the
 * {@code r^12} term is the fierce short-range repulsion (atoms cannot overlap). They balance at one
 * special separation, {@code r = 2^(1/6) * sigma}, where the force is exactly zero. That is the
 * happy distance the atoms want to sit at, and it is why matter has a size and does not collapse.
 *
 * <p>{@code sigma} sets that preferred spacing and {@code eps} sets how strongly the atoms bond.
 * Beyond a cutoff distance the force is so weak we ignore it, which also keeps the simulation fast.
 *
 * <p>From this single rule, everything about states of matter follows. Cold atoms settle at their
 * happy distance and lock into a regular pattern: a solid. Warm them and the bonds keep breaking
 * and reforming: a liquid. Heat them more and they break free entirely: a gas. ch11 lets you watch
 * a lattice melt just by turning up the temperature.
 */
public class LennardJones implements Force {

  private final double epsilon;
  private final double sigma;
  private final double cutoff;
  private final double minSeparationSquared;

  public LennardJones(double epsilon, double sigma, double cutoff) {
    this.epsilon = epsilon;
    this.sigma = sigma;
    this.cutoff = cutoff;
    // Below this separation we stop the repulsion growing, so a rare very close approach cannot
    // produce a near-infinite force that would wreck the simulation. It is a numerical safety net,
    // set well inside the repulsive wall.
    double floor = 0.8 * sigma;
    this.minSeparationSquared = floor * floor;
  }

  @Override
  public void apply(List<Particle> bodies) {
    double cutoffSquared = cutoff * cutoff;
    for (int i = 0; i < bodies.size(); i++) {
      Particle a = bodies.get(i);
      for (int j = i + 1; j < bodies.size(); j++) {
        Particle b = bodies.get(j);

        Vector2 delta = b.position().subtract(a.position()); // from a to b
        double r2 = delta.lengthSquared();
        if (r2 > cutoffSquared || r2 == 0) {
          continue;
        }
        if (r2 < minSeparationSquared) {
          r2 = minSeparationSquared;
        }

        double sr2 = (sigma * sigma) / r2;
        double sr6 = sr2 * sr2 * sr2;
        double sr12 = sr6 * sr6;

        // Radial coefficient: positive pushes apart (repulsion), negative pulls together
        // (attraction). This is the Lennard-Jones force written to multiply straight onto the
        // vector between the atoms.
        double coefficient = 24 * epsilon * (2 * sr12 - sr6) / r2;

        // Force on a points from b to a (that is, -delta) when repulsive.
        Vector2 forceOnA = delta.scale(-coefficient);
        a.addForce(forceOnA);
        b.addForce(forceOnA.negate());
      }
    }
  }
}
