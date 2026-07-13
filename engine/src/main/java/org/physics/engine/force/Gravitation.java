package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The gravity that holds the solar system together, not the flat downward pull of ch04 but the
 * mutual attraction between every pair of bodies. Newton's law of universal gravitation says any
 * two masses pull on each other along the line joining them, harder when they are heavier and much
 * harder when they are closer:
 *
 * <pre>
 *   force = G * m1 * m2 / distance^2
 * </pre>
 *
 * <p>The "inverse square" (dividing by distance squared) is why gravity reaches across space yet
 * fades so fast: double the distance and the pull drops to a quarter. It is the same law for a
 * falling apple and an orbiting moon.
 *
 * <p>We add a small "softening" length so that if two bodies pass extremely close the force stays
 * finite instead of shooting to infinity and flinging them off the screen. Real point masses would
 * not need it, but it keeps the simulation well behaved.
 */
public class Gravitation implements Force {

  private final double gravitationalConstant;
  private final double softening;

  public Gravitation(double gravitationalConstant, double softening) {
    this.gravitationalConstant = gravitationalConstant;
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

        double magnitude = gravitationalConstant * a.mass() * b.mass() / distanceSquared;
        Vector2 forceOnA = delta.scale(magnitude / distance); // toward b

        a.addForce(forceOnA);
        b.addForce(forceOnA.negate()); // equal and opposite, toward a
      }
    }
  }
}
