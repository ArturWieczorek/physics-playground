package org.physics.engine.force;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DrivenOscillator: resonance")
class DrivenOscillatorTest {

  @Test
  @DisplayName("a very slow push just gives the static stretch F/k")
  void slowDriveGivesStaticStretch() {
    DrivenOscillator o = new DrivenOscillator(1, 20, 0.6, 6);
    assertEquals(6.0 / 20.0, o.steadyAmplitude(1e-6), 1e-6);
  }

  @Test
  @DisplayName("the response peaks near the natural frequency")
  void responsePeaksAtResonance() {
    DrivenOscillator o = new DrivenOscillator(1, 20, 0.6, 6);
    double omega0 = o.naturalFrequency();
    double atResonance = o.steadyAmplitude(omega0);
    assertTrue(atResonance > o.steadyAmplitude(0.4 * omega0));
    assertTrue(atResonance > o.steadyAmplitude(2.0 * omega0));
    // Light damping means the peak towers over the static stretch.
    assertTrue(atResonance > 5 * o.steadyAmplitude(1e-6));
  }

  @Test
  @DisplayName("driving at the natural frequency builds a far bigger swing than driving off it")
  void drivingAtResonanceBuildsLargerSwing() {
    double maxOn = peakSwing(4.472); // natural frequency of sqrt(20)
    double maxOff = peakSwing(9.0); // well above resonance
    assertTrue(maxOn > maxOff * 3, "on-resonance " + maxOn + " vs off " + maxOff);
  }

  private static double peakSwing(double driveFrequency) {
    DrivenOscillator o = new DrivenOscillator(1, 20, 0.6, 6);
    double peak = 0;
    for (int i = 0; i < 30000; i++) {
      o.step(driveFrequency, 0.001);
      peak = Math.max(peak, Math.abs(o.position()));
    }
    return peak;
  }
}
