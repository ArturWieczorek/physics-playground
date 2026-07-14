package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

@DisplayName("Boris pusher: energy-exact motion of a charge in a field")
class BorisPusherTest {

  @Test
  @DisplayName("in a pure magnetic field the speed is conserved to machine precision")
  void magneticFieldConservesSpeedExactly() {
    Particle p = new Particle(new Vector2(0, 0), new Vector2(0, 1), 1.0).charge(1.0);
    double startSpeed = p.velocity().length();
    for (int i = 0; i < 100000; i++) {
      BorisPusher.step(p, Vector2.ZERO, 1.0, 0.001);
    }
    // Unlike semi-implicit Euler, the Boris rotation does not change the speed at all.
    assertEquals(startSpeed, p.velocity().length(), 1e-9);
  }

  @Test
  @DisplayName("the charge stays on a circle of the expected cyclotron radius")
  void staysOnTheCyclotronCircle() {
    // q = m = B = 1, v = 1, so radius = m v / (q B) = 1, centre at (1, 0).
    Particle p = new Particle(new Vector2(0, 0), new Vector2(0, 1), 1.0).charge(1.0);
    Vector2 centre = new Vector2(1, 0);
    double worst = 0;
    for (int i = 0; i < 6283; i++) {
      BorisPusher.step(p, Vector2.ZERO, 1.0, 0.001);
      worst = Math.max(worst, Math.abs(p.position().distanceTo(centre) - 1.0));
    }
    assertTrue(worst < 0.01, "radius wandered by " + worst);
  }

  @Test
  @DisplayName("with no magnetic field, an electric field accelerates the charge steadily")
  void electricFieldAccelerates() {
    Particle p = new Particle(new Vector2(0, 0), Vector2.ZERO, 2.0).charge(3.0);
    double dt = 0.001;
    int steps = 1000; // one second
    for (int i = 0; i < steps; i++) {
      BorisPusher.step(p, new Vector2(4, 0), 0.0, dt);
    }
    // a = qE/m = 3*4/2 = 6, so after 1 second v = 6.
    assertEquals(6.0, p.velocity().x(), 1e-9);
  }
}
