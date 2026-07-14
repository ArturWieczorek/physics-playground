package org.physics.engine.wave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PlaneWave: a travelling wave, the shape of light")
class PlaneWaveTest {

  @Test
  @DisplayName("speed equals frequency times wavelength")
  void speedIsFrequencyTimesWavelength() {
    PlaneWave wave = new PlaneWave(1.0, 2.5, 4.0);
    assertEquals(wave.speed(), wave.frequency() * wave.wavelength(), 1e-9);
  }

  @Test
  @DisplayName("the wave repeats every wavelength in space")
  void repeatsEveryWavelength() {
    PlaneWave wave = new PlaneWave(1.0, 2.5, 4.0);
    assertEquals(wave.fieldAt(1.3, 0), wave.fieldAt(1.3 + wave.wavelength(), 0), 1e-9);
  }

  @Test
  @DisplayName("a point of fixed phase moves forward at the wave speed")
  void crestsTravelAtWaveSpeed() {
    PlaneWave wave = new PlaneWave(1.0, 2.5, 4.0);
    double dt = 0.01;
    // The field a step later, one step's travel further along, is the same value.
    assertEquals(wave.fieldAt(1.3, 0.5), wave.fieldAt(1.3 + wave.speed() * dt, 0.5 + dt), 1e-9);
  }

  @Test
  @DisplayName("the field never exceeds the amplitude")
  void staysWithinAmplitude() {
    PlaneWave wave = new PlaneWave(2.0, 2.5, 4.0);
    for (double t = 0; t < 5; t += 0.1) {
      for (double x = 0; x < 10; x += 0.1) {
        assertTrue(Math.abs(wave.fieldAt(x, t)) <= 2.0 + 1e-9);
      }
    }
  }
}
