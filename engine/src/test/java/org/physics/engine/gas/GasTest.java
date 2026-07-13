package org.physics.engine.gas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.collide.ParticleCollisions;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

@DisplayName("Kinetic theory: temperature, the speed distribution, and conservation")
class GasTest {

  @Test
  @DisplayName("temperature is the average kinetic energy of the particles")
  void temperatureIsAverageKineticEnergy() {
    // Two unit-mass particles at speeds 2 and 4: KE of 2 and 8, average 5.
    Particle slow = new Particle(new Vector2(0, 0), new Vector2(2, 0), 1.0);
    Particle fast = new Particle(new Vector2(0, 0), new Vector2(0, 4), 1.0);
    assertEquals(5.0, MaxwellBoltzmann.temperature(List.of(slow, fast)), 1e-9);
  }

  @Test
  @DisplayName("the speed distribution is a proper probability density (it integrates to 1)")
  void distributionIntegratesToOne() {
    double temperature = 3.0;
    double mass = 2.0;
    double area = integrate(v -> MaxwellBoltzmann.speedProbabilityDensity(v, temperature, mass));
    assertEquals(1.0, area, 1e-3);
  }

  @Test
  @DisplayName("the average speed from the distribution matches the sqrt(pi T / 2m) formula")
  void averageSpeedMatchesFormula() {
    double temperature = 3.0;
    double mass = 2.0;
    double average =
        integrate(v -> v * MaxwellBoltzmann.speedProbabilityDensity(v, temperature, mass));
    assertEquals(MaxwellBoltzmann.meanSpeed(temperature, mass), average, 1e-3);
  }

  @Test
  @DisplayName("elastic collisions and walls conserve the gas's total energy over a long run")
  void gasConservesTotalEnergy() {
    World world = new World();
    world.addConstraint(new ParticleCollisions(1.0));
    world.addConstraint(new BoxBounds(0, 0, 16, 9, 1.0));

    Random random = new Random(7); // fixed seed, so the test is repeatable
    for (int col = 0; col < 6; col++) {
      for (int row = 0; row < 6; row++) {
        Vector2 position = new Vector2(2 + col * 2.0, 1.5 + row * 1.2);
        Vector2 velocity = new Vector2(random.nextDouble() * 6 - 3, random.nextDouble() * 6 - 3);
        world.add(new Particle(position, velocity, 1.0).radius(0.15));
      }
    }

    double energyBefore = totalKineticEnergy(world.bodies());
    for (int i = 0; i < 3000; i++) {
      world.step(0.01);
    }
    double energyAfter = totalKineticEnergy(world.bodies());

    assertEquals(energyBefore, energyAfter, energyBefore * 1e-6);
  }

  private static double totalKineticEnergy(List<Particle> bodies) {
    double total = 0;
    for (Particle body : bodies) {
      total += body.kineticEnergy();
    }
    return total;
  }

  // Trapezoidal integration from 0 to a large speed, fine enough for a 1e-3 check.
  private interface Curve {
    double at(double v);
  }

  private static double integrate(Curve curve) {
    double from = 0;
    double to = 40;
    int steps = 40000;
    double h = (to - from) / steps;
    double sum = 0.5 * (curve.at(from) + curve.at(to));
    for (int i = 1; i < steps; i++) {
      sum += curve.at(from + i * h);
    }
    return sum * h;
  }
}
