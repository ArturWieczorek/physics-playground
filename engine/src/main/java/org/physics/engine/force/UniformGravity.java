package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The everyday gravity near the ground: a constant downward pull, the same everywhere. The force on
 * a body is its mass times the gravitational acceleration, F = m g.
 *
 * <p>Because the force is proportional to mass but acceleration is force divided by mass, the mass
 * cancels: everything falls with the same acceleration, whatever its weight. That is Galileo's
 * famous observation, and a test in this chapter checks that a heavy body and a light one fall
 * exactly together.
 */
public class UniformGravity implements Force {

  private final Vector2 gravity;

  /** A common Earth-like value is {@code new Vector2(0, -9.81)} (metres per second squared). */
  public UniformGravity(Vector2 gravity) {
    this.gravity = gravity;
  }

  @Override
  public void apply(List<Particle> bodies) {
    for (Particle body : bodies) {
      body.addForce(gravity.scale(body.mass()));
    }
  }
}
