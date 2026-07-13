package org.physics.engine.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Scalars: number helpers used across the engine")
class ScalarsTest {

  @Test
  @DisplayName("clamp returns the value untouched when it is already inside the range")
  void clampLeavesValuesInsideRangeAlone() {
    assertEquals(5.0, Scalars.clamp(5.0, 0.0, 10.0));
  }

  @Test
  @DisplayName("clamp pulls values back to the nearest edge of the range")
  void clampPullsValuesToTheEdges() {
    assertEquals(0.0, Scalars.clamp(-3.0, 0.0, 10.0));
    assertEquals(10.0, Scalars.clamp(42.0, 0.0, 10.0));
  }

  @Test
  @DisplayName("clamp rejects a range whose minimum is above its maximum")
  void clampRejectsBackwardsRange() {
    assertThrows(IllegalArgumentException.class, () -> Scalars.clamp(1.0, 10.0, 0.0));
  }

  @Test
  @DisplayName("approximatelyEqual accepts numbers within the tolerance and rejects those outside")
  void approximatelyEqualRespectsTolerance() {
    assertTrue(Scalars.approximatelyEqual(1.0, 1.0000001, 1e-6));
    assertFalse(Scalars.approximatelyEqual(1.0, 1.5, 1e-6));
  }

  @Test
  @DisplayName("approximatelyEqual rejects a negative tolerance")
  void approximatelyEqualRejectsNegativeTolerance() {
    assertThrows(IllegalArgumentException.class, () -> Scalars.approximatelyEqual(1.0, 1.0, -0.1));
  }
}
