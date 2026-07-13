package org.physics.engine.collide;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * Makes the particles bump into each other instead of passing through. When two of them overlap, we
 * do two things: push them apart so they only just touch, and change their velocities so they
 * bounce off in a way that respects the laws of motion.
 *
 * <p>The bounce is worked out with an impulse: a sudden kick along the line joining the two
 * centres. Its size is chosen so that momentum is always conserved (the total of mass times
 * velocity is the same before and after), and, when the restitution is 1, so is kinetic energy.
 * That is what "elastic" means: a perfect collision that loses no energy, like idealised billiard
 * balls.
 *
 * <p>This checks every pair, which is fine for the modest numbers we use. A big simulation would
 * first sort particles into a grid so it only checks near neighbours, but the physics of each
 * bounce would be exactly this.
 */
public class ParticleCollisions implements Constraint {

  private final double restitution;

  public ParticleCollisions(double restitution) {
    this.restitution = restitution;
  }

  @Override
  public void resolve(List<Particle> bodies) {
    for (int i = 0; i < bodies.size(); i++) {
      for (int j = i + 1; j < bodies.size(); j++) {
        resolvePair(bodies.get(i), bodies.get(j));
      }
    }
  }

  private void resolvePair(Particle a, Particle b) {
    Vector2 delta = b.position().subtract(a.position());
    double distance = delta.length();
    double minDistance = a.radius() + b.radius();
    if (distance >= minDistance || distance == 0) {
      return; // not touching (or exactly on top of each other, which has no direction)
    }

    double inverseMassSum = a.inverseMass() + b.inverseMass();
    if (inverseMassSum == 0) {
      return; // both are immovable
    }

    Vector2 normal = delta.scale(1.0 / distance);

    // Push them apart so they just touch, sharing the move by inverse mass: the lighter one moves
    // more, an immovable one not at all.
    double overlap = minDistance - distance;
    a.setPosition(a.position().subtract(normal.scale(overlap * a.inverseMass() / inverseMassSum)));
    b.setPosition(b.position().add(normal.scale(overlap * b.inverseMass() / inverseMassSum)));

    // How fast are they closing along the line between them?
    Vector2 relativeVelocity = b.velocity().subtract(a.velocity());
    double closingSpeed = relativeVelocity.dot(normal);
    if (closingSpeed >= 0) {
      return; // already separating, no bounce needed
    }

    // The impulse that produces the desired bounce, split between the two by inverse mass.
    double impulseMagnitude = -(1 + restitution) * closingSpeed / inverseMassSum;
    Vector2 impulse = normal.scale(impulseMagnitude);
    a.setVelocity(a.velocity().subtract(impulse.scale(a.inverseMass())));
    b.setVelocity(b.velocity().add(impulse.scale(b.inverseMass())));
  }
}
