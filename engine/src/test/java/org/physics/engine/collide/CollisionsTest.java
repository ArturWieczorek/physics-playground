package org.physics.engine.collide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

@DisplayName("Collisions: momentum and energy when things bump")
class CollisionsTest {

  @Test
  @DisplayName("two equal masses in a head-on elastic hit swap velocities (Newton's cradle)")
  void equalMassesSwapVelocities() {
    Particle a = new Particle(new Vector2(0, 0), new Vector2(1, 0), 1.0).radius(0.5);
    Particle b = new Particle(new Vector2(0.9, 0), new Vector2(0, 0), 1.0).radius(0.5);

    new ParticleCollisions(1.0).resolve(List.of(a, b));

    assertEquals(0.0, a.velocity().x(), 1e-9); // the mover stops
    assertEquals(1.0, b.velocity().x(), 1e-9); // the target takes off
  }

  @Test
  @DisplayName("momentum is conserved in a collision of unequal masses")
  void momentumIsConserved() {
    Particle a = new Particle(new Vector2(0, 0), new Vector2(3, 0), 2.0).radius(0.5);
    Particle b = new Particle(new Vector2(0.8, 0), new Vector2(-1, 0), 5.0).radius(0.5);
    List<Particle> pair = List.of(a, b);

    double before = totalMomentumX(pair);
    new ParticleCollisions(1.0).resolve(pair);
    double after = totalMomentumX(pair);

    assertEquals(before, after, 1e-9);
  }

  @Test
  @DisplayName("a perfectly elastic collision conserves kinetic energy")
  void elasticCollisionConservesEnergy() {
    Particle a = new Particle(new Vector2(0, 0), new Vector2(3, 0), 2.0).radius(0.5);
    Particle b = new Particle(new Vector2(0.8, 0), new Vector2(-1, 0), 5.0).radius(0.5);
    List<Particle> pair = List.of(a, b);

    double before = a.kineticEnergy() + b.kineticEnergy();
    new ParticleCollisions(1.0).resolve(pair);
    double after = a.kineticEnergy() + b.kineticEnergy();

    assertEquals(before, after, 1e-9);
  }

  @Test
  @DisplayName("a fully inelastic collision keeps momentum but loses kinetic energy")
  void inelasticCollisionLosesEnergy() {
    Particle a = new Particle(new Vector2(0, 0), new Vector2(4, 0), 1.0).radius(0.5);
    Particle b = new Particle(new Vector2(0.8, 0), new Vector2(0, 0), 1.0).radius(0.5);
    List<Particle> pair = List.of(a, b);

    double momentumBefore = totalMomentumX(pair);
    double energyBefore = a.kineticEnergy() + b.kineticEnergy();
    new ParticleCollisions(0.0).resolve(pair);

    assertEquals(momentumBefore, totalMomentumX(pair), 1e-9);
    // With no bounce they move together, so the closing speed is gone.
    assertEquals(a.velocity().x(), b.velocity().x(), 1e-9);
    assertTrue(a.kineticEnergy() + b.kineticEnergy() < energyBefore);
  }

  @Test
  @DisplayName("a wall bounce reverses the velocity into it, scaled by restitution")
  void wallBounceRespectsRestitution() {
    BoxBounds box = new BoxBounds(0, 0, 10, 10, 0.5);
    Particle body = new Particle(new Vector2(9.9, 5), new Vector2(4, 0), 1.0).radius(0.5);

    box.resolve(List.of(body));

    assertEquals(9.5, body.position().x(), 1e-9); // pushed back to touch the wall
    assertEquals(-2.0, body.velocity().x(), 1e-9); // reversed and halved by restitution 0.5
  }

  @Test
  @DisplayName("particles are kept inside the box")
  void particlesStayInsideTheBox() {
    BoxBounds box = new BoxBounds(0, 0, 10, 10, 1.0);
    Particle body = new Particle(new Vector2(-3, 12), new Vector2(-1, 1), 1.0).radius(0.5);

    box.resolve(List.of(body));

    assertTrue(body.position().x() >= 0.5 - 1e-9);
    assertTrue(body.position().y() <= 9.5 + 1e-9);
  }

  private static double totalMomentumX(List<Particle> bodies) {
    double sum = 0;
    for (Particle p : bodies) {
      sum += p.mass() * p.velocity().x();
    }
    return sum;
  }
}
