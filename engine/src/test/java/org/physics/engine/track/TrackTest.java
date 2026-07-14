package org.physics.engine.track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.math.Vector2;

@DisplayName("Brachistochrone: sliding beads and the fastest descent")
class TrackTest {

  private static final Vector2 START = new Vector2(0, 5);
  private static final Vector2 END = new Vector2(6, 0);
  private static final double G = 9.8;

  @Test
  @DisplayName("a bead on a frictionless wire conserves energy: v = sqrt(2 g drop)")
  void energyIsConserved() {
    Track ramp = Curves.straightLine(START, END, 400);
    Bead bead = new Bead(0, 0);
    while (!bead.isFinished()) {
      bead.step(ramp, G, 0.0002);
    }
    // At the bottom the bead has dropped the full height, so v = sqrt(2 g h).
    double expected = Math.sqrt(2 * G * (START.y() - END.y()));
    assertEquals(expected, bead.speed(), expected * 0.02);
  }

  @Test
  @DisplayName("the cycloid is faster than the straight ramp (the brachistochrone result)")
  void cycloidBeatsTheStraightLine() {
    double straightTime = descentTime(Curves.straightLine(START, END, 600));
    double cycloidTime = descentTime(Curves.cycloid(START, END, 600));
    assertTrue(
        cycloidTime < straightTime,
        "cycloid " + cycloidTime + " should beat straight " + straightTime);
  }

  @Test
  @DisplayName("the arc also beats the straight line but loses to the cycloid")
  void arcIsBetweenStraightAndCycloid() {
    double straightTime = descentTime(Curves.straightLine(START, END, 600));
    double arcTime = descentTime(Curves.arc(START, END, 2.0, 600));
    double cycloidTime = descentTime(Curves.cycloid(START, END, 600));
    assertTrue(arcTime < straightTime, "arc should beat the straight line");
    assertTrue(cycloidTime < arcTime, "cycloid should be the fastest");
  }

  private static double descentTime(Track track) {
    Bead bead = new Bead(0, 0);
    double dt = 0.0002;
    double time = 0;
    while (!bead.isFinished() && time < 100) {
      bead.step(track, G, dt);
      time += dt;
    }
    return time;
  }
}
