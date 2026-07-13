package org.physics.engine.integrate;

import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A tiny change to {@link ExplicitEuler} that makes a huge difference. It updates the velocity
 * first, then moves using that new velocity:
 *
 * <pre>
 *   velocity += acceleration * dt
 *   position += velocity * dt        (the already-updated velocity)
 * </pre>
 *
 * <p>Swapping the order turns an unstable method into a stable one. The energy of a spring or orbit
 * no longer grows without bound; it wobbles slightly but stays put. This is the default workhorse
 * for the rest of the course because it is cheap and well behaved. It is also known as symplectic
 * Euler.
 */
public class SemiImplicitEuler implements Integrator {

  @Override
  public void step(Particle particle, AccelerationField field, double dt) {
    Vector2 acceleration = field.at(particle);
    Vector2 newVelocity = particle.velocity().add(acceleration.scale(dt));
    particle.setVelocity(newVelocity);
    particle.setPosition(particle.position().add(newVelocity.scale(dt)));
  }
}
