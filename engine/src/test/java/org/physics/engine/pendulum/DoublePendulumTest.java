package org.physics.engine.pendulum;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.math.Vector2;

@DisplayName("Double pendulum: chaotic but energy-conserving")
class DoublePendulumTest {

  @Test
  @DisplayName("energy stays nearly constant over a long chaotic run")
  void energyIsConserved() {
    DoublePendulum p = new DoublePendulum(1, 1, 1, 1, 9.8, 2.2, 2.4);
    double startEnergy = p.energy();
    for (int i = 0; i < 4000; i++) {
      p.step(0.005); // 20 seconds of chaotic swinging
    }
    double drift = Math.abs(p.energy() - startEnergy) / Math.abs(startEnergy);
    assertTrue(drift < 0.01, "energy drifted by " + drift);
  }

  @Test
  @DisplayName("a tiny change in the start leads to a very different path (sensitive dependence)")
  void isSensitiveToInitialConditions() {
    DoublePendulum a = new DoublePendulum(1, 1, 1, 1, 9.8, 2.2, 2.4);
    DoublePendulum b = new DoublePendulum(1, 1, 1, 1, 9.8, 2.2, 2.4001); // barely different
    Vector2 pivot = new Vector2(0, 0);
    for (int i = 0; i < 2000; i++) {
      a.step(0.005);
      b.step(0.005);
    }
    // After ten seconds the two lower bobs are nowhere near each other.
    double separation = a.bob2(pivot).distanceTo(b.bob2(pivot));
    assertTrue(separation > 0.5, "paths should have diverged, separation was " + separation);
  }

  @Test
  @DisplayName("the bobs stay a fixed distance from their pivots (the rods are rigid)")
  void rodsKeepTheirLength() {
    DoublePendulum p = new DoublePendulum(1, 2, 1.5, 2.0, 9.8, 1.0, 0.5);
    Vector2 pivot = new Vector2(3, 4);
    for (int i = 0; i < 500; i++) {
      p.step(0.005);
    }
    double rod1 = p.bob1(pivot).distanceTo(pivot);
    double rod2 = p.bob2(pivot).distanceTo(p.bob1(pivot));
    assertTrue(Math.abs(rod1 - 1.5) < 1e-9 && Math.abs(rod2 - 2.0) < 1e-9);
  }
}
