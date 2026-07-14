package org.physics.engine.chaos;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LorenzSystem: the butterfly effect")
class LorenzSystemTest {

  @Test
  @DisplayName("the trajectory stays bounded: it loops forever without flying off")
  void trajectoryStaysBounded() {
    LorenzSystem s = new LorenzSystem(1, 1, 1);
    for (int i = 0; i < 20000; i++) {
      s.step(0.005);
      assertTrue(
          Math.abs(s.x()) < 100 && Math.abs(s.y()) < 100 && s.z() > -10 && s.z() < 120,
          "should stay on the attractor, not diverge");
    }
  }

  @Test
  @DisplayName("two almost-identical starts diverge completely (sensitive dependence)")
  void sensitiveDependence() {
    LorenzSystem a = new LorenzSystem(1, 1, 1);
    LorenzSystem b = new LorenzSystem(1, 1, 1.0001); // a ten-thousandth apart
    for (int i = 0; i < 6000; i++) {
      a.step(0.005);
      b.step(0.005);
    }
    double separation =
        Math.sqrt(
            Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2) + Math.pow(a.z() - b.z(), 2));
    assertTrue(separation > 5, "paths should have diverged, separation was " + separation);
  }

  @Test
  @DisplayName("the system actually moves (it does not sit still)")
  void systemEvolves() {
    LorenzSystem s = new LorenzSystem(1, 1, 1);
    double startX = s.x();
    for (int i = 0; i < 200; i++) {
      s.step(0.005);
    }
    assertTrue(Math.abs(s.x() - startX) > 1, "the point should have travelled");
  }
}
