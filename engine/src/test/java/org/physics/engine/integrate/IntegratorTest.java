package org.physics.engine.integrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

@DisplayName("Integrators: stepping a particle forward through time")
class IntegratorTest {

  // No force at all: the particle should coast in a straight line. Every integrator must get
  // this right, because it is just position += velocity * time.
  @Test
  @DisplayName("with no acceleration a particle moves in a straight line")
  void freeParticleCoasts() {
    for (Integrator integrator :
        new Integrator[] {new ExplicitEuler(), new SemiImplicitEuler(), new VelocityVerlet()}) {
      Particle p = new Particle(new Vector2(0, 0), new Vector2(2, -1), 1.0);
      AccelerationField none = particle -> Vector2.ZERO;
      for (int i = 0; i < 100; i++) {
        integrator.step(p, none, 0.01); // total time 1.0
      }
      assertEquals(2.0, p.position().x(), 1e-6, integrator.getClass().getSimpleName());
      assertEquals(-1.0, p.position().y(), 1e-6, integrator.getClass().getSimpleName());
    }
  }

  // Constant acceleration (gravity). Velocity Verlet is exact for this case, so after time t the
  // drop should match the schoolbook formula distance = 0.5 * g * t^2 to very high precision.
  @Test
  @DisplayName("velocity Verlet reproduces 0.5*g*t^2 exactly for constant gravity")
  void verletMatchesClosedFormForGravity() {
    Particle p = new Particle(new Vector2(0, 0), new Vector2(0, 0), 1.0);
    AccelerationField gravity = particle -> new Vector2(0, -9.81);
    double dt = 0.001;
    int steps = 1000; // total time 1.0 s
    Integrator verlet = new VelocityVerlet();
    for (int i = 0; i < steps; i++) {
      verlet.step(p, gravity, dt);
    }
    double t = dt * steps;
    double expectedDrop = 0.5 * 9.81 * t * t;
    assertEquals(-expectedDrop, p.position().y(), 1e-6);
    assertEquals(-9.81 * t, p.velocity().y(), 1e-6);
  }

  // The interesting one. A mass on an ideal spring bounces forever with a fixed energy. A perfect
  // integrator would keep that energy constant. We run many oscillations and watch what each method
  // does to the energy. This is the whole reason we care which integrator we pick.
  @Test
  @DisplayName("explicit Euler pumps energy into a spring that should conserve it")
  void explicitEulerGainsEnergy() {
    double drift = energyDriftForHarmonicOscillator(new ExplicitEuler());
    assertTrue(drift > 0.5, "explicit Euler should gain a lot of energy, got drift " + drift);
  }

  @Test
  @DisplayName("semi-implicit Euler keeps the spring energy bounded")
  void semiImplicitEulerStaysBounded() {
    double drift = Math.abs(energyDriftForHarmonicOscillator(new SemiImplicitEuler()));
    assertTrue(drift < 0.1, "semi-implicit Euler energy should stay bounded, got drift " + drift);
  }

  @Test
  @DisplayName("velocity Verlet keeps the spring energy very close to constant")
  void velocityVerletStaysVeryClose() {
    double drift = Math.abs(energyDriftForHarmonicOscillator(new VelocityVerlet()));
    assertTrue(drift < 0.01, "velocity Verlet energy should barely move, got drift " + drift);
  }

  /**
   * Runs a unit-mass harmonic oscillator (acceleration = -x, so angular frequency 1) for a long
   * time and returns the fractional change in total energy: (finalEnergy - startEnergy) /
   * startEnergy. Zero means energy was perfectly conserved.
   */
  private static double energyDriftForHarmonicOscillator(Integrator integrator) {
    Particle p = new Particle(new Vector2(1, 0), new Vector2(0, 0), 1.0);
    AccelerationField spring = particle -> particle.position().negate(); // a = -x
    double dt = 0.02;
    int steps = 5000; // 100 time units, about 16 full swings
    double startEnergy = harmonicEnergy(p);
    for (int i = 0; i < steps; i++) {
      integrator.step(p, spring, dt);
    }
    double endEnergy = harmonicEnergy(p);
    return (endEnergy - startEnergy) / startEnergy;
  }

  // Energy of the unit-mass, unit-frequency oscillator: kinetic (0.5 v^2) plus spring (0.5 x^2).
  private static double harmonicEnergy(Particle p) {
    return 0.5 * p.velocity().lengthSquared() + 0.5 * p.position().lengthSquared();
  }
}
