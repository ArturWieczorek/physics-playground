package org.physics.engine.quantum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TwoElectronState: identical particles and the Pauli exclusion principle")
class TwoElectronStateTest {

  private final TwoElectronState state = new TwoElectronState(1.0);

  @Test
  @DisplayName("the antisymmetric (electron) state vanishes when both electrons coincide")
  void antisymmetricIsZeroOnTheDiagonal() {
    for (double x = -0.9; x <= 0.9; x += 0.3) {
      assertEquals(0.0, state.value(x, x, true), 1e-12, "should be zero at x1 = x2 = " + x);
    }
  }

  @Test
  @DisplayName("swapping the two electrons flips the sign of the antisymmetric state")
  void antisymmetricFlipsUnderSwap() {
    assertEquals(-state.value(0.6, -0.2, true), state.value(-0.2, 0.6, true), 1e-12);
  }

  @Test
  @DisplayName("swapping leaves the symmetric state unchanged")
  void symmetricIsUnchangedUnderSwap() {
    assertEquals(state.value(0.6, -0.2, false), state.value(-0.2, 0.6, false), 1e-12);
  }

  @Test
  @DisplayName("the symmetric state is generally non-zero on the diagonal (bosons may coincide)")
  void symmetricSurvivesOnTheDiagonal() {
    assertTrue(Math.abs(state.value(0.4, 0.4, false)) > 1e-6);
  }
}
