package org.physics.engine.core;

import org.physics.engine.math.Vector2;

/**
 * A single moving point: where it is, how fast it is going, and how heavy it is. Unlike {@link
 * Vector2}, a particle is mutable, because a simulation is all about changing its state a little on
 * every step. Its position and velocity are vectors; its mass is a plain number.
 *
 * <p>Mass matters because the same push moves a light thing more than a heavy one. We meet that
 * rule properly in ch04 (Newton's second law); here a particle simply carries its mass so it is
 * ready.
 */
public class Particle {

  private Vector2 position;
  private Vector2 velocity;
  private final double mass;

  public Particle(Vector2 position, Vector2 velocity, double mass) {
    if (mass <= 0) {
      throw new IllegalArgumentException("mass must be positive: " + mass);
    }
    this.position = position;
    this.velocity = velocity;
    this.mass = mass;
  }

  public Vector2 position() {
    return position;
  }

  public Vector2 velocity() {
    return velocity;
  }

  public double mass() {
    return mass;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public void setVelocity(Vector2 velocity) {
    this.velocity = velocity;
  }

  /** Kinetic energy, one half m v squared: the energy a thing has purely from moving. */
  public double kineticEnergy() {
    return 0.5 * mass * velocity.lengthSquared();
  }
}
