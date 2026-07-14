package org.physics.engine.wave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.math.Vector2;

@DisplayName("Interference: overlapping waves make bright and dark fringes")
class InterferenceTest {

  // Two slits, one above the other, with the beam heading off along +x toward a screen.
  private static final List<Vector2> TWO_SOURCES = List.of(new Vector2(0, -1), new Vector2(0, 1));

  @Test
  @DisplayName("on the centre line the two waves arrive in step, giving the brightest spot")
  void centreLineIsFullyConstructive() {
    // A point straight ahead on the x axis is equidistant from both slits, so their waves add in
    // phase. Two unit phasors in phase give an intensity of 2^2 = 4.
    assertEquals(4.0, Interference.intensityAt(TWO_SOURCES, 1.0, 8, 0), 1e-9);
  }

  @Test
  @DisplayName("a single source has the same brightness everywhere (no fringes)")
  void oneSourceHasNoFringes() {
    List<Vector2> one = List.of(new Vector2(0, 0));
    double a = Interference.intensityAt(one, 1.0, 3, 4);
    double b = Interference.intensityAt(one, 1.0, -2, 7);
    assertEquals(a, b, 1e-9); // both equal 1
    assertEquals(1.0, a, 1e-9);
  }

  @Test
  @DisplayName("along a far screen the pattern really does swing between bright and dark")
  void screenShowsBrightAndDarkBands() {
    double wavelength = 1.0;
    double screenX = 25; // a distant screen
    double brightest = 0;
    double darkest = Double.MAX_VALUE;
    for (double y = -20; y <= 20; y += 0.02) {
      double intensity = Interference.intensityAt(TWO_SOURCES, wavelength, screenX, y);
      brightest = Math.max(brightest, intensity);
      darkest = Math.min(darkest, intensity);
    }
    assertTrue(brightest > 3.9, "should reach a bright fringe near intensity 4, got " + brightest);
    assertTrue(darkest < 0.1, "should reach a dark fringe near intensity 0, got " + darkest);
  }

  @Test
  @DisplayName("the travelling wave stays within the sum of the source amplitudes")
  void amplitudeIsBounded() {
    for (double t = 0; t < 3; t += 0.1) {
      double a = Interference.amplitudeAt(TWO_SOURCES, 1.0, 2.0, 4, 5, t);
      assertTrue(Math.abs(a) <= 2.0 + 1e-9);
    }
  }
}
