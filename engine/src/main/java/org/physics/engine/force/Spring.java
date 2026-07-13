package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A spring joining two particles. It has a natural length it "wants" to be, and it pushes or pulls
 * to get back there. Stretch it and it pulls the ends together; squash it and it pushes them apart.
 *
 * <p>The strength of that pull follows Hooke's law: the force is proportional to how far the spring
 * is from its rest length.
 *
 * <pre>
 *   force = stiffness * (currentLength - restLength)
 * </pre>
 *
 * <p>A real spring also loses energy, or it would bounce forever. We add a damping term that
 * resists the two ends moving apart or together, proportional to how fast they are doing so. With a
 * bit of damping the spring settles; with none it oscillates without end.
 *
 * <p>By Newton's third law the two ends feel equal and opposite forces, which is exactly what the
 * code applies.
 */
public class Spring implements Force {

  private final Particle a;
  private final Particle b;
  private final double restLength;
  private final double stiffness;
  private final double damping;

  public Spring(Particle a, Particle b, double restLength, double stiffness, double damping) {
    if (restLength < 0) {
      throw new IllegalArgumentException("restLength must not be negative: " + restLength);
    }
    if (stiffness <= 0) {
      throw new IllegalArgumentException("stiffness must be positive: " + stiffness);
    }
    if (damping < 0) {
      throw new IllegalArgumentException("damping must not be negative: " + damping);
    }
    this.a = a;
    this.b = b;
    this.restLength = restLength;
    this.stiffness = stiffness;
    this.damping = damping;
  }

  @Override
  public void apply(List<Particle> bodies) {
    Vector2 delta = b.position().subtract(a.position());
    double length = delta.length();
    if (length == 0) {
      // The ends sit on top of each other; there is no direction to push along.
      return;
    }
    Vector2 direction = delta.scale(1.0 / length);

    // Hooke's law: positive when stretched (pull the ends together), negative when compressed.
    double springForce = stiffness * (length - restLength);

    // Damping resists the ends closing or opening, along the spring's direction.
    Vector2 relativeVelocity = b.velocity().subtract(a.velocity());
    double dampingForce = damping * relativeVelocity.dot(direction);

    Vector2 forceOnA = direction.scale(springForce + dampingForce);
    a.addForce(forceOnA);
    b.addForce(forceOnA.negate());
  }
}
