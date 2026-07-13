package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

@DisplayName("Gravitation: the inverse-square law and orbits")
class GravitationTest {

  @Test
  @DisplayName("two masses pull on each other with G*m1*m2/r^2, equal and opposite")
  void inverseSquareForce() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0);
    // G = 1, masses 1, distance 2, no softening: force = 1 / 4.
    new Gravitation(1.0, 0.0).apply(List.of(a, b));

    assertEquals(0.25, a.force().x(), 1e-9); // a pulled toward b (to the right)
    assertEquals(-0.25, b.force().x(), 1e-9); // b pulled toward a (to the left)
    assertEquals(0.0, a.force().y(), 1e-9);
  }

  @Test
  @DisplayName("the pull weakens with the square of distance")
  void pullFollowsInverseSquare() {
    double near = forceBetweenAtDistance(2.0);
    double far = forceBetweenAtDistance(4.0);
    // Twice as far apart should be one quarter the force.
    assertEquals(near / 4.0, far, 1e-9);
  }

  @Test
  @DisplayName("a body given circular speed keeps a steady orbit and conserves angular momentum")
  void circularOrbitIsStable() {
    double g = 1.0;
    double centralMass = 1000.0;
    double radius = 5.0;
    double speed = Math.sqrt(g * centralMass / radius); // the circular-orbit speed

    Particle star = new Particle(new Vector2(0, 0), Vector2.ZERO, centralMass).pin();
    Particle planet = new Particle(new Vector2(radius, 0), new Vector2(0, speed), 1.0);

    World world = new World();
    world.add(star);
    world.add(planet);
    world.addForce(new Gravitation(g, 0.01));

    double startRadius = planet.position().length();
    double startAngularMomentum = angularMomentum(planet);

    double period = 2 * Math.PI * radius / speed;
    double dt = 0.0005;
    int steps = (int) Math.round(period / dt);
    double minRadius = startRadius;
    double maxRadius = startRadius;
    for (int i = 0; i < steps; i++) {
      world.step(dt);
      double r = planet.position().length();
      minRadius = Math.min(minRadius, r);
      maxRadius = Math.max(maxRadius, r);
    }

    // The orbit should stay close to a circle: the radius barely wanders.
    assertTrue(maxRadius - minRadius < radius * 0.03, "orbit radius wandered too much");
    // Angular momentum is conserved throughout.
    assertEquals(
        startAngularMomentum, angularMomentum(planet), Math.abs(startAngularMomentum) * 0.01);
    // After one full period it comes back near where it began.
    assertEquals(radius, planet.position().x(), radius * 0.03);
  }

  private static double forceBetweenAtDistance(double distance) {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 3.0);
    Particle b = new Particle(new Vector2(distance, 0), Vector2.ZERO, 5.0);
    new Gravitation(1.0, 0.0).apply(List.of(a, b));
    return a.force().length();
  }

  // Angular momentum per unit mass about the origin, position cross velocity, times mass.
  private static double angularMomentum(Particle p) {
    return p.mass() * p.position().cross(p.velocity());
  }
}
