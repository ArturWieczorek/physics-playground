package org.physics.engine.cloth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.math.Vector2;

@DisplayName("Cloth: Verlet points held together by threads")
class ClothTest {

  @Test
  @DisplayName("relaxing pulls a stretched thread back to its rest length")
  void relaxRestoresRestLength() {
    Cloth cloth = new Cloth(2, 1, 1.0, new Vector2(0, 0), 100.0); // two points, one thread, rest 1
    // Pull the second point out to distance 3.
    cloth.points().get(1).setPosition(new Vector2(3, 0));
    cloth.points().get(1).setPrevious(new Vector2(3, 0));

    cloth.relax(50);

    double distance = cloth.points().get(0).position().distanceTo(cloth.points().get(1).position());
    assertEquals(1.0, distance, 1e-6);
  }

  @Test
  @DisplayName("two free ends meet in the middle, leaving their midpoint fixed")
  void freeEndsShareTheMoveEqually() {
    Cloth cloth = new Cloth(2, 1, 1.0, new Vector2(0, 0), 100.0);
    cloth.points().get(0).setPosition(new Vector2(0, 0));
    cloth.points().get(1).setPosition(new Vector2(3, 0));
    Vector2 midpointBefore =
        cloth.points().get(0).position().add(cloth.points().get(1).position()).scale(0.5);

    cloth.relax(50);

    Vector2 midpointAfter =
        cloth.points().get(0).position().add(cloth.points().get(1).position()).scale(0.5);
    assertEquals(midpointBefore.x(), midpointAfter.x(), 1e-9);
    assertEquals(midpointBefore.y(), midpointAfter.y(), 1e-9);
  }

  @Test
  @DisplayName("a thread stretched past its tear length snaps")
  void overstretchedThreadTears() {
    Cloth cloth = new Cloth(2, 1, 1.0, new Vector2(0, 0), 2.0); // tears past length 2
    cloth.points().get(1).setPosition(new Vector2(3, 0)); // distance 3 > 2

    cloth.relax(1);

    assertTrue(cloth.sticks().get(0).isBroken(), "the thread should have snapped");
  }

  @Test
  @DisplayName("a pinned top edge lets the rest of the sheet hang below it, and stays stable")
  void pinnedClothHangs() {
    Cloth cloth = new Cloth(5, 5, 1.0, new Vector2(0, 5), 100.0);
    cloth.pinTopRow();

    for (int i = 0; i < 400; i++) {
      cloth.step(new Vector2(0, -9.8), 1.0 / 60.0, 5);
    }

    double topY = cloth.points().get(0).position().y(); // a pinned point on the top row
    double bottomY = cloth.points().get(20).position().y(); // first point of the bottom row
    assertTrue(bottomY < topY, "the bottom of the cloth should hang below the pinned top");

    // The top row stayed pinned exactly in place.
    assertEquals(5.0, topY, 1e-9);
    // Nothing blew up: every point is at a finite position.
    for (ClothPoint point : cloth.points()) {
      assertTrue(Double.isFinite(point.position().x()) && Double.isFinite(point.position().y()));
    }
  }
}
