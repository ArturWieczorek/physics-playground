package org.physics.engine.fluid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

@DisplayName("SPH fluid: kernels, density, and conservation")
class SphTest {

  private static final double H = 1.0;

  @Test
  @DisplayName("the density kernel is highest at the centre and fades to zero at the radius")
  void poly6HasCompactSupportAndAPeak() {
    assertTrue(Kernels.poly6(0, H) > Kernels.poly6(0.5, H));
    assertTrue(Kernels.poly6(0.5, H) > 0);
    assertEquals(0.0, Kernels.poly6(H, H), 1e-12);
    assertEquals(0.0, Kernels.poly6(2 * H, H), 1e-12);
  }

  @Test
  @DisplayName("the pressure kernel gradient points inward and vanishes at the radius")
  void spikyGradientShape() {
    assertTrue(Kernels.spikyGradient(0.3, H) < 0); // negative: a push apart once combined
    assertEquals(0.0, Kernels.spikyGradient(H, H), 1e-12);
    assertEquals(0.0, Kernels.spikyGradient(2 * H, H), 1e-12);
  }

  @Test
  @DisplayName("a lone particle's density is just its own mass times the kernel at zero")
  void singleParticleDensityIsSelfContribution() {
    Sph sph = new Sph(H, 2.0, 0.0, 1.0, 0.0);
    Particle only = new Particle(new Vector2(0, 0), Vector2.ZERO, 2.0);
    double[] density = sph.densities(List.of(only));
    assertEquals(2.0 * Kernels.poly6(0, H), density[0], 1e-12);
  }

  @Test
  @DisplayName("a crowded particle is denser than a lonely one")
  void crowdingRaisesDensity() {
    Sph sph = new Sph(H, 1.0, 0.0, 1.0, 0.0);
    List<Particle> crowd = new ArrayList<>();
    crowd.add(new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0));
    crowd.add(new Particle(new Vector2(0.3, 0), Vector2.ZERO, 1.0));
    crowd.add(new Particle(new Vector2(0, 0.3), Vector2.ZERO, 1.0));
    Particle lonely = new Particle(new Vector2(50, 50), Vector2.ZERO, 1.0);
    crowd.add(lonely);

    double[] density = sph.densities(crowd);
    assertTrue(density[0] > density[3], "the clustered particle should be denser than the far one");
  }

  @Test
  @DisplayName(
      "with no gravity the fluid's total momentum is conserved (equal and opposite forces)")
  void momentumIsConserved() {
    Sph sph = new Sph(H, 1.0, 0.5, 20.0, 5.0);
    List<Particle> bodies = new ArrayList<>();
    Random random = new Random(3);
    for (int i = 0; i < 12; i++) {
      Vector2 position = new Vector2(random.nextDouble(), random.nextDouble());
      Vector2 velocity = new Vector2(random.nextDouble() - 0.5, random.nextDouble() - 0.5);
      bodies.add(new Particle(position, velocity, 1.0));
    }

    Vector2 before = totalMomentum(bodies);
    sph.step(bodies, Vector2.ZERO, 0.005); // no gravity, no walls
    Vector2 after = totalMomentum(bodies);

    assertEquals(before.x(), after.x(), 1e-9);
    assertEquals(before.y(), after.y(), 1e-9);
  }

  private static Vector2 totalMomentum(List<Particle> bodies) {
    Vector2 sum = Vector2.ZERO;
    for (Particle body : bodies) {
      sum = sum.add(body.velocity().scale(body.mass()));
    }
    return sum;
  }
}
