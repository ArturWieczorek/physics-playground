package org.physics.engine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.force.Force;
import org.physics.engine.force.UniformGravity;
import org.physics.engine.math.Vector2;

@DisplayName("Forces and the World: Newton's second law in action")
class WorldTest {

  // A constant push to the right, ignoring mass, for testing F = m a directly.
  private static Force constantPush(Vector2 newtons) {
    return bodies -> bodies.forEach(b -> b.addForce(newtons));
  }

  @Test
  @DisplayName("a = F / m: a 10 N push on a 2 kg body gives an acceleration of 5")
  void accelerationIsForceOverMass() {
    Particle body = new Particle(new Vector2(0, 0), Vector2.ZERO, 2.0);
    body.addForce(new Vector2(10, 0));
    assertEquals(5.0, body.acceleration().x(), 1e-9);
    assertEquals(0.0, body.acceleration().y(), 1e-9);
  }

  @Test
  @DisplayName("under the same force, the lighter body ends up moving faster")
  void samePushMovesLightThingsMore() {
    World world = new World();
    Particle light = world.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0));
    Particle heavy = world.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 4.0));
    world.addForce(constantPush(new Vector2(8, 0)));

    world.step(0.1);

    assertTrue(
        light.velocity().x() > heavy.velocity().x(),
        "lighter body should be faster: light="
            + light.velocity().x()
            + " heavy="
            + heavy.velocity().x());
    // Speeds should be in inverse proportion to mass: 4x the mass, 1/4 the speed.
    assertEquals(light.velocity().x(), heavy.velocity().x() * 4.0, 1e-9);
  }

  @Test
  @DisplayName("under gravity, a heavy body and a light body fall exactly together")
  void everythingFallsAtTheSameRate() {
    World world = new World();
    Particle feather = world.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 0.01));
    Particle hammer = world.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 100.0));
    world.addForce(new UniformGravity(new Vector2(0, -9.81)));

    for (int i = 0; i < 200; i++) {
      world.step(0.01);
    }

    assertEquals(feather.position().y(), hammer.position().y(), 1e-9);
    assertEquals(feather.velocity().y(), hammer.velocity().y(), 1e-9);
  }

  @Test
  @DisplayName("a dropped body reaches speed g*t under gravity")
  void droppedBodyReachesExpectedSpeed() {
    World world = new World();
    Particle body = world.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 3.0));
    world.addForce(new UniformGravity(new Vector2(0, -9.81)));

    double dt = 0.001;
    int steps = 1000; // one second
    for (int i = 0; i < steps; i++) {
      world.step(dt);
    }

    // Semi-implicit Euler is exact for the velocity under a constant force: v = g * t.
    assertEquals(-9.81, body.velocity().y(), 1e-9);
  }
}
