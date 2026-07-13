package org.physics.engine.integrate;

import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The most obvious integrator, and a cautionary tale. It moves the particle using its current
 * velocity, then updates the velocity using the current acceleration:
 *
 * <pre>
 *   position += velocity * dt
 *   velocity += acceleration * dt
 * </pre>
 *
 * <p>It is simple and easy to believe, but it quietly adds energy to oscillating systems: a spring
 * or an orbit slowly spirals outward. ch03's test proves this happens. We keep it mostly so you can
 * see why the better methods are worth it.
 */
public class ExplicitEuler implements Integrator {

  @Override
  public void step(Particle particle, AccelerationField field, double dt) {
    Vector2 acceleration = field.at(particle);
    Vector2 velocityBefore = particle.velocity();
    // Position uses the OLD velocity. This ordering is what makes the method drift.
    particle.setPosition(particle.position().add(velocityBefore.scale(dt)));
    particle.setVelocity(velocityBefore.add(acceleration.scale(dt)));
  }
}
