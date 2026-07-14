package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The electric force between charges, Coulomb's law. It has the very same inverse-square shape as
 * gravity, but with one decisive difference: it can push as well as pull.
 *
 * <pre>
 *   force = k * q1 * q2 / distance^2
 * </pre>
 *
 * <p>Charges come with a sign. Two positives or two negatives give a positive product, and the
 * force pushes them apart: like charges repel. A positive and a negative give a negative product,
 * and the force pulls them together: opposite charges attract. Gravity only ever attracts because
 * mass is never negative; electricity does both because charge can be either sign. That single fact
 * is why matter holds together and why sparks jump.
 *
 * <p>As with gravity we add a small softening length so a very close pair does not produce an
 * infinite force.
 */
public class Coulomb implements Force {

  private final double coulombConstant;
  private final double softening;

  public Coulomb(double coulombConstant, double softening) {
    this.coulombConstant = coulombConstant;
    this.softening = softening;
  }

  @Override
  public void apply(List<Particle> bodies) {
    for (int i = 0; i < bodies.size(); i++) {
      Particle a = bodies.get(i);
      for (int j = i + 1; j < bodies.size(); j++) {
        Particle b = bodies.get(j);

        Vector2 delta = b.position().subtract(a.position()); // points from a to b
        double distanceSquared = delta.lengthSquared() + softening * softening;
        double distance = Math.sqrt(distanceSquared);

        // Positive when the charges are alike (a repulsion), negative when opposite (an
        // attraction).
        double magnitude = coulombConstant * a.charge() * b.charge() / distanceSquared;

        // A repulsion pushes a away from b, which is the direction opposite to delta.
        Vector2 forceOnA = delta.scale(-magnitude / distance);
        a.addForce(forceOnA);
        b.addForce(forceOnA.negate());
      }
    }
  }
}
