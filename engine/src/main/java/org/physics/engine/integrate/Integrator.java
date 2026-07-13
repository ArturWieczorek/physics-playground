package org.physics.engine.integrate;

import org.physics.engine.core.Particle;

/**
 * A rule for advancing a particle by one small step of time. This is the "adding up tiny steps"
 * from ch00, made concrete. Different rules trade accuracy, stability, and cost differently, and
 * ch03 shows those differences with your own eyes.
 */
public interface Integrator {

  /**
   * Move the particle forward by {@code dt} seconds, using {@code field} to find the acceleration
   * it feels. The particle's position and velocity are updated in place.
   */
  void step(Particle particle, AccelerationField field, double dt);
}
