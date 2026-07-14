package org.physics.engine.quantum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HydrogenOrbital: the shapes of electron clouds")
class HydrogenOrbitalTest {

  @Test
  @DisplayName("the 1s orbital is spherical: it depends only on distance from the nucleus")
  void groundStateIsSpherical() {
    double alongX = HydrogenOrbital.ONE_S.value(2, 0, 0);
    double alongY = HydrogenOrbital.ONE_S.value(0, 2, 0);
    double alongZ = HydrogenOrbital.ONE_S.value(0, 0, 2);
    assertEquals(alongX, alongY, 1e-12);
    assertEquals(alongX, alongZ, 1e-12);
    // and it fades with distance
    assertTrue(HydrogenOrbital.ONE_S.value(0, 0, 0) > HydrogenOrbital.ONE_S.value(5, 0, 0));
  }

  @Test
  @DisplayName("the 2s orbital has a spherical node: it changes sign across r = 2")
  void twoSHasARadialNode() {
    double inside = HydrogenOrbital.TWO_S.value(1, 0, 0); // r = 1, (2 - r) > 0
    double outside = HydrogenOrbital.TWO_S.value(3, 0, 0); // r = 3, (2 - r) < 0
    assertTrue(inside > 0 && outside < 0, "2s should flip sign across its node");
  }

  @Test
  @DisplayName("the 2p_z orbital is two opposite lobes with a node in the xy plane")
  void twoPzIsTwoLobesWithAPlanarNode() {
    // Zero everywhere the electron is level with the nucleus (z = 0).
    assertEquals(0.0, HydrogenOrbital.TWO_P_Z.value(1.5, -0.7, 0), 1e-12);
    // Opposite sign above and below.
    double above = HydrogenOrbital.TWO_P_Z.value(0, 0, 2);
    double below = HydrogenOrbital.TWO_P_Z.value(0, 0, -2);
    assertEquals(above, -below, 1e-12);
    assertTrue(above > 0);
  }

  @Test
  @DisplayName("the 3d_z2 orbital points up the z axis but is negative around the middle ring")
  void threeDz2HasLobesAndARing() {
    double alongZ = HydrogenOrbital.THREE_D_Z2.value(0, 0, 2); // 3z^2 - r^2 = 2 r^2 > 0
    double inPlane = HydrogenOrbital.THREE_D_Z2.value(2, 0, 0); // 3*0 - r^2 < 0
    assertTrue(alongZ > 0, "the z lobes are positive");
    assertTrue(inPlane < 0, "the middle ring is the opposite sign");
  }

  @Test
  @DisplayName("density is the square of the wavefunction and never negative")
  void densityIsNonNegative() {
    for (HydrogenOrbital orbital : HydrogenOrbital.values()) {
      assertTrue(orbital.density(1.3, -0.8, 0.5) >= 0);
    }
  }
}
