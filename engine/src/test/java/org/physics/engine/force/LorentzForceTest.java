package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

@DisplayName("Lorentz force: a moving charge in a magnetic field")
class LorentzForceTest {

  @Test
  @DisplayName("the magnetic force is always perpendicular to the velocity")
  void magneticForceIsPerpendicular() {
    Particle body = new Particle(new Vector2(0, 0), new Vector2(2, 1), 1.0).charge(1.0);
    new LorentzForce(Vector2.ZERO, 1.5).apply(List.of(body));
    // Perpendicular means the dot product of force and velocity is zero.
    assertEquals(0.0, body.force().dot(body.velocity()), 1e-9);
  }

  @Test
  @DisplayName("the magnetic force has magnitude |q| * v * B")
  void magneticForceMagnitude() {
    Particle body = new Particle(new Vector2(0, 0), new Vector2(3, 4), 1.0).charge(2.0);
    new LorentzForce(Vector2.ZERO, 0.5).apply(List.of(body));
    // |q| v B = 2 * 5 * 0.5 = 5.
    assertEquals(5.0, body.force().length(), 1e-9);
  }

  @Test
  @DisplayName("a charge at rest still feels the electric part of the force")
  void electricPartActsAtRest() {
    Particle body = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(3.0);
    new LorentzForce(new Vector2(2, 0), 1.0).apply(List.of(body));
    // Only qE acts (v is zero, so no magnetic part): 3 * 2 = 6 in x.
    assertEquals(6.0, body.force().x(), 1e-9);
    assertEquals(0.0, body.force().y(), 1e-9);
  }

  @Test
  @DisplayName("in a pure magnetic field the charge circles at constant speed (cyclotron motion)")
  void chargeMovesInACircle() {
    // q = m = B = 1, v = 1, so the cyclotron radius m v / (q B) = 1 and the centre sits at (1, 0).
    Particle body = new Particle(new Vector2(0, 0), new Vector2(0, 1), 1.0).charge(1.0);
    World world = new World();
    world.add(body);
    world.addForce(new LorentzForce(Vector2.ZERO, 1.0));

    Vector2 centre = new Vector2(1, 0);
    double startSpeed = body.velocity().length();
    double dt = 0.0005;
    int steps = (int) Math.round(2 * Math.PI / dt); // one full circle
    double worstRadiusError = 0;
    for (int i = 0; i < steps; i++) {
      world.step(dt);
      double radius = body.position().distanceTo(centre);
      worstRadiusError = Math.max(worstRadiusError, Math.abs(radius - 1.0));
    }

    // It stays on a circle of radius 1 (it neither spirals out nor flies off straight).
    assertTrue(worstRadiusError < 0.05, "radius drifted by " + worstRadiusError);
    // A magnetic force does no work, so the speed is essentially unchanged.
    assertEquals(startSpeed, body.velocity().length(), 0.01);
  }
}
