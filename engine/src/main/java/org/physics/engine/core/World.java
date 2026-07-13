package org.physics.engine.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.physics.engine.collide.Constraint;
import org.physics.engine.force.Force;
import org.physics.engine.math.Vector2;

/**
 * A little universe: a bag of particles and the forces acting on them. Calling {@link
 * #step(double)} advances the whole thing by one slice of time. Every scene in the course is,
 * underneath, a World with some particles and some forces added to it.
 *
 * <p>The step follows the same three beats every time:
 *
 * <ol>
 *   <li>Forget last step's forces (reset each particle's total to zero).
 *   <li>Ask every force to add its push to the particles it affects.
 *   <li>Turn each particle's total force into acceleration and move it.
 * </ol>
 *
 * <p>The move uses semi-implicit Euler, the stable workhorse from ch03: update velocity from the
 * acceleration, then update position from the new velocity.
 */
public class World {

  private final List<Particle> bodies = new ArrayList<>();
  private final List<Force> forces = new ArrayList<>();
  private final List<Constraint> constraints = new ArrayList<>();

  /** Adds a particle and returns it, so callers can keep a handle on it. */
  public Particle add(Particle particle) {
    bodies.add(particle);
    return particle;
  }

  /** Registers a force that will act on the particles each step. */
  public void addForce(Force force) {
    forces.add(force);
  }

  /** Registers a constraint, resolved after the particles have moved each step. */
  public void addConstraint(Constraint constraint) {
    constraints.add(constraint);
  }

  /** The particles, as a read-only list. */
  public List<Particle> bodies() {
    return Collections.unmodifiableList(bodies);
  }

  /** Advances the world by {@code dt} seconds. */
  public void step(double dt) {
    for (Particle body : bodies) {
      body.resetForce();
    }
    for (Force force : forces) {
      force.apply(bodies);
    }
    for (Particle body : bodies) {
      if (body.isPinned()) {
        // A pinned body never moves, so leave its position alone and keep it still.
        body.setVelocity(Vector2.ZERO);
        continue;
      }
      Vector2 acceleration = body.acceleration();
      Vector2 newVelocity = body.velocity().add(acceleration.scale(dt));
      body.setVelocity(newVelocity);
      body.setPosition(body.position().add(newVelocity.scale(dt)));
    }

    // Now that everything has moved, fix anything that broke: overlaps, escapes from the box, and
    // so on. Constraints run in the order they were added.
    for (Constraint constraint : constraints) {
      constraint.resolve(bodies);
    }
  }
}
