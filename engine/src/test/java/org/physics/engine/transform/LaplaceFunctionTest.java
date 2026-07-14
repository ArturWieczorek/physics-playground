package org.physics.engine.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LaplaceFunction: transforms and their poles")
class LaplaceFunctionTest {

  @Test
  @DisplayName("cos(t) transform matches s/(s^2+1) on the real axis")
  void cosineOnRealAxis() {
    // At s = 2 (omega = 0): |F| = 2 / (4 + 1) = 0.4.
    assertEquals(0.4, LaplaceFunction.COSINE.magnitude(2, 0), 1e-9);
  }

  @Test
  @DisplayName("sin(t) transform matches 1/(s^2+1) on the real axis")
  void sineOnRealAxis() {
    // At s = 2: |F| = 1 / (4 + 1) = 0.2.
    assertEquals(0.2, LaplaceFunction.SINE.magnitude(2, 0), 1e-9);
  }

  @Test
  @DisplayName("cos(t) has a pole at s = i: the magnitude explodes near omega = 1")
  void cosineHasPoleAtImaginaryOne() {
    double nearPole = LaplaceFunction.COSINE.magnitude(0, 0.999);
    double farAway = LaplaceFunction.COSINE.magnitude(2, 0);
    assertTrue(nearPole > 100, "should be huge near the pole, was " + nearPole);
    assertTrue(nearPole > farAway * 100);
  }

  @Test
  @DisplayName("e^-t has its pole on the negative real axis at s = -1")
  void decayPoleIsOnNegativeRealAxis() {
    double nearPole = LaplaceFunction.DECAY.magnitude(-0.999, 0);
    assertTrue(nearPole > 100, "should be huge near s = -1, was " + nearPole);
    // Well away from the pole it is modest.
    assertTrue(LaplaceFunction.DECAY.magnitude(2, 0) < 1.0);
  }

  @Test
  @DisplayName("all magnitudes are non-negative")
  void magnitudesAreNonNegative() {
    for (LaplaceFunction f : LaplaceFunction.values()) {
      assertTrue(f.magnitude(1.3, -0.7) >= 0);
    }
  }
}
