package org.physics.engine.integrate;

import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A more accurate integrator that looks at the acceleration both before and after the move, and
 * averages them:
 *
 * <pre>
 *   position += velocity * dt + 0.5 * a_old * dt*dt
 *   a_new     = acceleration at the new position
 *   velocity += 0.5 * (a_old + a_new) * dt
 * </pre>
 *
 * <p>Checking the acceleration again at the new position is what makes it good: it catches how the
 * force changed during the step. For a constant force such as gravity it is exact, matching the
 * schoolbook 0.5 * g * t^2 to the last digit. It costs two acceleration lookups per step instead of
 * one, which is why we reach for it when accuracy matters (orbits, molecules).
 */
public class VelocityVerlet implements Integrator {

  @Override
  public void step(Particle particle, AccelerationField field, double dt) {
    Vector2 accelerationOld = field.at(particle);
    Vector2 newPosition =
        particle
            .position()
            .add(particle.velocity().scale(dt))
            .add(accelerationOld.scale(0.5 * dt * dt));
    particle.setPosition(newPosition);

    Vector2 accelerationNew = field.at(particle);
    Vector2 newVelocity =
        particle.velocity().add(accelerationOld.add(accelerationNew).scale(0.5 * dt));
    particle.setVelocity(newVelocity);
  }
}
