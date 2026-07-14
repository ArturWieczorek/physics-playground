package org.physics.engine.pendulum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SimplePendulum: the swing period depends only on length and gravity")
class SimplePendulumTest {

  @Test
  @DisplayName("the period matches 2*pi*sqrt(length/gravity)")
  void periodMatchesFormula() {
    SimplePendulum p = new SimplePendulum(2.0, 9.8, 0.3);
    assertEquals(2 * Math.PI * Math.sqrt(2.0 / 9.8), p.period(), 1e-12);
  }

  @Test
  @DisplayName("a length equal to gravity gives an angular frequency of 1")
  void unitAngularFrequency() {
    assertEquals(1.0, new SimplePendulum(9.8, 9.8, 0.3).angularFrequency(), 1e-12);
  }

  @Test
  @DisplayName("it starts at the amplitude and returns there after a full period")
  void startsAndReturnsAtAmplitude() {
    SimplePendulum p = new SimplePendulum(2.0, 9.8, 0.3);
    assertEquals(0.3, p.angleAt(0), 1e-12);
    assertEquals(0.3, p.angleAt(p.period()), 1e-9);
    assertEquals(-0.3, p.angleAt(p.period() / 2), 1e-9); // farthest the other way
  }

  @Test
  @DisplayName("a shorter pendulum swings faster than a longer one")
  void shorterSwingsFaster() {
    SimplePendulum shortP = new SimplePendulum(1.0, 9.8, 0.3);
    SimplePendulum longP = new SimplePendulum(4.0, 9.8, 0.3);
    assertEquals(true, shortP.period() < longP.period());
  }
}
