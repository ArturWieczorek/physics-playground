package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

@DisplayName("Lennard-Jones: the force that binds atoms")
class LennardJonesTest {

  private static final double EPS = 1.0;
  private static final double SIGMA = 1.0;
  private static final double CUTOFF = 3.0;

  @Test
  @DisplayName("the force is zero at the equilibrium separation 2^(1/6) * sigma")
  void forceVanishesAtEquilibrium() {
    double equilibrium = Math.pow(2, 1.0 / 6.0) * SIGMA;
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(equilibrium, 0), Vector2.ZERO, 1.0);
    new LennardJones(EPS, SIGMA, CUTOFF).apply(List.of(a, b));
    assertEquals(0.0, a.force().length(), 1e-9);
    assertEquals(0.0, b.force().length(), 1e-9);
  }

  @Test
  @DisplayName("closer than equilibrium, the atoms push apart")
  void tooCloseRepels() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(1.0, 0), Vector2.ZERO, 1.0); // inside 1.122 sigma
    new LennardJones(EPS, SIGMA, CUTOFF).apply(List.of(a, b));
    assertTrue(a.force().x() < 0, "a should be pushed left, away from b");
    assertTrue(b.force().x() > 0, "b should be pushed right, away from a");
  }

  @Test
  @DisplayName("farther than equilibrium (within the cutoff), the atoms pull together")
  void fartherAttracts() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(1.5, 0), Vector2.ZERO, 1.0);
    new LennardJones(EPS, SIGMA, CUTOFF).apply(List.of(a, b));
    assertTrue(a.force().x() > 0, "a should be pulled right, toward b");
    assertTrue(b.force().x() < 0, "b should be pulled left, toward a");
  }

  @Test
  @DisplayName("beyond the cutoff the atoms do not interact")
  void beyondCutoffNoForce() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(4.0, 0), Vector2.ZERO, 1.0); // past cutoff 3.0
    new LennardJones(EPS, SIGMA, CUTOFF).apply(List.of(a, b));
    assertEquals(0.0, a.force().length(), 1e-12);
  }

  @Test
  @DisplayName("two atoms released near equilibrium stay bound and do not fly apart")
  void atomsStayBound() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0);
    Particle b = new Particle(new Vector2(1.4, 0), Vector2.ZERO, 1.0);
    World world = new World();
    world.add(a);
    world.add(b);
    world.addForce(new LennardJones(EPS, SIGMA, CUTOFF));

    double minGap = Double.MAX_VALUE;
    double maxGap = 0;
    for (int i = 0; i < 4000; i++) {
      world.step(0.001);
      double gap = a.position().distanceTo(b.position());
      minGap = Math.min(minGap, gap);
      maxGap = Math.max(maxGap, gap);
    }
    // They oscillate around the equilibrium separation, staying bound: neither collapsing to zero
    // nor drifting off past the cutoff.
    assertTrue(minGap > 0.9, "atoms collapsed too close: " + minGap);
    assertTrue(maxGap < 1.6, "atoms drifted apart: " + maxGap);
  }
}
