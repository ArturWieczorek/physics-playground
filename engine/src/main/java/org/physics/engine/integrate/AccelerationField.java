package org.physics.engine.integrate;

import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A way to ask "what acceleration does this particle feel right now?". Acceleration can depend on
 * where the particle is (a spring pulling it home, gravity from a planet) or how fast it is going
 * (air drag), so the whole particle is handed over.
 *
 * <p>This is what lets an integrator such as {@link VelocityVerlet} re-check the acceleration after
 * it has moved the particle, which is exactly what makes it accurate.
 */
@FunctionalInterface
public interface AccelerationField {

  Vector2 at(Particle particle);
}
