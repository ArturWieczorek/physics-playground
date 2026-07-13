package org.physics.engine.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Vector2: a point or direction in the plane")
class Vector2Test {

  private static final double TOL = 1e-9;

  @Test
  @DisplayName("adding two vectors adds their components")
  void addingCombinesComponents() {
    Vector2 sum = new Vector2(1, 2).add(new Vector2(3, 4));
    assertEquals(4, sum.x(), TOL);
    assertEquals(6, sum.y(), TOL);
  }

  @Test
  @DisplayName("subtracting gives the vector from one point to another")
  void subtractingGivesTheDifference() {
    Vector2 diff = new Vector2(5, 7).subtract(new Vector2(1, 2));
    assertEquals(4, diff.x(), TOL);
    assertEquals(5, diff.y(), TOL);
  }

  @Test
  @DisplayName("scaling multiplies both components by the same number")
  void scalingStretchesTheVector() {
    Vector2 scaled = new Vector2(2, -3).scale(2.5);
    assertEquals(5, scaled.x(), TOL);
    assertEquals(-7.5, scaled.y(), TOL);
  }

  @Test
  @DisplayName("negating flips the vector to point the opposite way")
  void negatingReversesDirection() {
    Vector2 v = new Vector2(2, -3).negate();
    assertEquals(-2, v.x(), TOL);
    assertEquals(3, v.y(), TOL);
  }

  @Test
  @DisplayName("the dot product of perpendicular vectors is zero")
  void dotOfPerpendicularVectorsIsZero() {
    assertEquals(0, new Vector2(1, 0).dot(new Vector2(0, 1)), TOL);
  }

  @Test
  @DisplayName("length uses Pythagoras: a 3,4 vector is 5 long")
  void lengthIsThePythagoreanDistance() {
    assertEquals(5, new Vector2(3, 4).length(), TOL);
    assertEquals(25, new Vector2(3, 4).lengthSquared(), TOL);
  }

  @Test
  @DisplayName("distanceTo measures the gap between two points")
  void distanceToMeasuresTheGap() {
    assertEquals(5, new Vector2(0, 0).distanceTo(new Vector2(3, 4)), TOL);
  }

  @Test
  @DisplayName("a normalized vector points the same way but is exactly one unit long")
  void normalizedHasLengthOne() {
    Vector2 unit = new Vector2(0, 8).normalized();
    assertEquals(1, unit.length(), TOL);
    assertEquals(0, unit.x(), TOL);
    assertEquals(1, unit.y(), TOL);
  }

  @Test
  @DisplayName("normalizing a zero-length vector is undefined and is rejected")
  void normalizingZeroIsRejected() {
    assertThrows(ArithmeticException.class, () -> Vector2.ZERO.normalized());
  }

  @Test
  @DisplayName("the 2D cross product gives the signed area and detects turn direction")
  void crossGivesSignedArea() {
    assertTrue(new Vector2(1, 0).cross(new Vector2(0, 1)) > 0);
    assertTrue(new Vector2(1, 0).cross(new Vector2(0, -1)) < 0);
  }
}
