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

  // The running total of the forces pushing on this particle during the current step. Forces are
  // added here one by one (ch04), then the total is turned into acceleration and the particle is
  // moved. It is reset to zero at the start of every step.
  private Vector2 force = Vector2.ZERO;

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

  /** Forgets the forces from the previous step. Called at the start of each step. */
  public void resetForce() {
    force = Vector2.ZERO;
  }

  /** Adds one more push to this step's running total. */
  public void addForce(Vector2 contribution) {
    force = force.add(contribution);
  }

  /** The total force accumulated on this particle so far this step. */
  public Vector2 force() {
    return force;
  }

  /**
   * Acceleration is force divided by mass. This is Newton's second law, F = m a, rearranged to a =
   * F / m: the same push moves a light thing more than a heavy one.
   */
  public Vector2 acceleration() {
    return force.scale(1.0 / mass);
  }

  /** Kinetic energy, one half m v squared: the energy a thing has purely from moving. */
  public double kineticEnergy() {
    return 0.5 * mass * velocity.lengthSquared();
  }
}
