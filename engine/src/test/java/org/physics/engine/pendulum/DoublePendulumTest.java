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
  @DisplayName("released from small angles it swings gently and stays near the bottom (stable)")
  void smallOscillationsStayBounded() {
    // Near hanging-straight-down, the double pendulum is a stable pair of coupled oscillators, so a
    // small release should stay small. A broken integrator would let it grow and fling out.
    DoublePendulum p = new DoublePendulum(1, 1, 1, 1, 9.8, 0.15, 0.15);
    Vector2 pivot = new Vector2(0, 0);
    Vector2 bottom = new Vector2(0, -2); // both rods hanging straight down (l1 + l2)
    double worst = 0;
    for (int i = 0; i < 6000; i++) {
      p.step(0.005);
      worst = Math.max(worst, p.bob2(pivot).distanceTo(bottom));
    }
    assertTrue(worst < 0.7, "small oscillations should stay near the bottom, worst was " + worst);
  }
}
