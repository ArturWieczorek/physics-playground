package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

@DisplayName("Spring: Hooke's law and simple harmonic motion")
class SpringTest {

  @Test
  @DisplayName("a spring at exactly its rest length exerts no force")
  void restingSpringIsSlack() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0);
    new Spring(a, b, 2.0, 10.0, 0.0).apply(List.of(a, b));
    assertEquals(0.0, a.force().length(), 1e-9);
    assertEquals(0.0, b.force().length(), 1e-9);
  }

  @Test
  @DisplayName("a stretched spring pulls its two ends toward each other")
  void stretchedSpringPullsInward() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(3, 0), Vector2.ZERO, 1.0);
    new Spring(a, b, 2.0, 10.0, 0.0).apply(List.of(a, b));
    // Stretched by 1 at stiffness 10, so a force of 10 pulling each end toward the other.
    assertEquals(10.0, a.force().x(), 1e-9); // a pulled right, toward b
    assertEquals(-10.0, b.force().x(), 1e-9); // b pulled left, toward a
  }

  @Test
  @DisplayName("the two ends always feel equal and opposite forces (Newton's third law)")
  void endsFeelEqualAndOppositeForces() {
    Particle a = new Particle(new Vector2(0, 0), new Vector2(0, 1), 1.0);
    Particle b = new Particle(new Vector2(1, 2), new Vector2(-1, 0), 1.0);
    new Spring(a, b, 1.0, 5.0, 0.5).apply(List.of(a, b));
    assertEquals(a.force().x(), -b.force().x(), 1e-9);
    assertEquals(a.force().y(), -b.force().y(), 1e-9);
  }

  @Test
  @DisplayName("a mass on a spring oscillates with the period 2*pi*sqrt(m/k)")
  void oscillatesWithTheExpectedPeriod() {
    double stiffness = 4.0;
    double mass = 1.0;
    double amplitude = 2.0;
    // Anchor at the origin, rest length zero, so the mass feels a = -(k/m) * x: pure SHM.
    Particle anchor = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).pin();
    Particle bob = new Particle(new Vector2(amplitude, 0), Vector2.ZERO, mass);

    World world = new World();
    world.add(anchor);
    world.add(bob);
    world.addForce(new Spring(anchor, bob, 0.0, stiffness, 0.0));

    double period = 2 * Math.PI * Math.sqrt(mass / stiffness);
    double dt = 0.0005;
    int steps = (int) Math.round(period / dt);
    for (int i = 0; i < steps; i++) {
      world.step(dt);
    }

    // After one full period the mass should be back where it started, at rest.
    assertEquals(amplitude, bob.position().x(), 0.02);
    assertEquals(0.0, bob.velocity().x(), 0.05);
  }

  @Test
  @DisplayName("damping bleeds energy out of the oscillation")
  void dampingRemovesEnergy() {
    Particle anchor = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).pin();
    Particle bob = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0);

    World world = new World();
    world.add(anchor);
    world.add(bob);
    world.addForce(new Spring(anchor, bob, 0.0, 4.0, 0.5));

    double startEnergy = mechanicalEnergy(bob, 4.0);
    for (int i = 0; i < 4000; i++) {
      world.step(0.005);
    }
    double endEnergy = mechanicalEnergy(bob, 4.0);

    assertTrue(endEnergy < startEnergy * 0.5, "damping should remove most of the energy");
  }

  // Kinetic energy plus spring energy for a rest-length-zero spring anchored at the origin.
  private static double mechanicalEnergy(Particle bob, double stiffness) {
    return bob.kineticEnergy() + 0.5 * stiffness * bob.position().lengthSquared();
  }
}
