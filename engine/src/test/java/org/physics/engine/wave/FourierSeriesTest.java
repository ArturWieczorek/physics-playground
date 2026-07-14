package org.physics.engine.wave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FourierSeries: building shapes out of sine waves")
class FourierSeriesTest {

  @Test
  @DisplayName("the first square-wave harmonic has size 4/pi")
  void firstSquareHarmonic() {
    FourierSeries.Harmonic first = FourierSeries.squareWave(3).harmonics().get(0);
    assertEquals(4.0 / Math.PI, first.amplitude(), 1e-12);
    assertEquals(1, first.frequency());
  }

  @Test
  @DisplayName("a square wave uses only the odd harmonics")
  void squareWaveUsesOddHarmonicsOnly() {
    for (FourierSeries.Harmonic h : FourierSeries.squareWave(5).harmonics()) {
      assertTrue(h.frequency() % 2 == 1, "frequency " + h.frequency() + " should be odd");
    }
  }

  @Test
  @DisplayName("more terms make the flat top of the square wave converge to 1")
  void moreTermsConvergeToTheFlatTop() {
    double middleOfHalfCycle = Math.PI / 2; // well away from the jump
    double few = FourierSeries.squareWave(1).valueAt(middleOfHalfCycle);
    double many = FourierSeries.squareWave(80).valueAt(middleOfHalfCycle);
    assertEquals(1.0, many, 0.02);
    assertTrue(Math.abs(many - 1) < Math.abs(few - 1), "more terms should be closer to 1");
  }

  @Test
  @DisplayName("the square wave is odd: flipping time flips the sign")
  void squareWaveIsOdd() {
    FourierSeries wave = FourierSeries.squareWave(10);
    assertEquals(-wave.valueAt(0.7), wave.valueAt(-0.7), 1e-12);
  }
}
