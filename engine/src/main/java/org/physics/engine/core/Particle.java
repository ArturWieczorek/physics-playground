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

  // A pinned particle is nailed in place: it still feels forces, but the world never moves it. We
  // use it for a spring's fixed anchor (ch05) and for the corners a piece of cloth hangs from
  // (ch12).
  private boolean pinned = false;

  // How big the particle is, for collisions (ch06). A point has no size, but real bumping needs a
  // radius so two particles can touch. Defaults to something small.
  private double radius = 0.2;

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

  public double radius() {
    return radius;
  }

  /**
   * Sets the collision radius and returns the particle, for easy chaining when building a scene.
   */
  public Particle radius(double radius) {
    if (radius < 0) {
      throw new IllegalArgumentException("radius must not be negative: " + radius);
    }
    this.radius = radius;
    return this;
  }

  /**
   * The inverse of the mass, treating a pinned particle as infinitely heavy (inverse mass zero).
   * Collision maths is written in terms of inverse mass because "how much does a push move this?"
   * is what matters, and an immovable wall is simply something with an inverse mass of zero.
   */
  public double inverseMass() {
    return pinned ? 0.0 : 1.0 / mass;
  }

  public boolean isPinned() {
    return pinned;
  }

  /** Nails the particle in place. It keeps its current position no matter what forces act on it. */
  public Particle pin() {
    this.pinned = true;
    return this;
  }

  /** Releases a pinned particle so it can move again. */
  public Particle unpin() {
    this.pinned = false;
    return this;
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
