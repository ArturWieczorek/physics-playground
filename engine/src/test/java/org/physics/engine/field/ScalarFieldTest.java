package org.physics.engine.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.math.Vector2;

@DisplayName("ScalarField: partial derivatives and the gradient")
class ScalarFieldTest {

  @Test
  @DisplayName("the saddle's partials match the analytic slopes 0.5x and -0.5y")
  void saddlePartialsMatchAnalytic() {
    // f = 0.25 (x^2 - y^2), so df/dx = 0.5 x and df/dy = -0.5 y.
    assertEquals(1.0, ScalarField.SADDLE.partialX(2, 1), 1e-4); // 0.5 * 2
    assertEquals(-0.5, ScalarField.SADDLE.partialY(2, 1), 1e-4); // -0.5 * 1
  }

  @Test
  @DisplayName("the gradient bundles the two partials into one vector")
  void gradientBundlesThePartials() {
    Vector2 g = ScalarField.SADDLE.gradient(2, 1);
    assertEquals(ScalarField.SADDLE.partialX(2, 1), g.x(), 1e-12);
    assertEquals(ScalarField.SADDLE.partialY(2, 1), g.y(), 1e-12);
  }

  @Test
  @DisplayName("on a hill the gradient points back toward the summit (uphill)")
  void gradientPointsUphill() {
    // On the single peak at the origin, from a point to the right the uphill direction is left.
    Vector2 g = ScalarField.PEAK.gradient(1.0, 0.0);
    assertTrue(g.x() < 0, "gradient should point back toward the peak at the origin");
    assertEquals(0.0, g.y(), 1e-6);
  }

  @Test
  @DisplayName("the gradient is zero at the top of the peak (a level point)")
  void gradientVanishesAtTheSummit() {
    assertTrue(ScalarField.PEAK.gradient(0, 0).length() < 1e-3);
  }

  @Test
  @DisplayName("the gradient is perpendicular to the contour lines")
  void gradientIsPerpendicularToContours() {
    // Move a tiny step along the gradient and along the perpendicular; the field should change a
    // lot along the gradient and almost nothing along the perpendicular (a contour direction).
    double x = 1.3;
    double y = -0.7;
    Vector2 g = ScalarField.WAVES.gradient(x, y);
    Vector2 alongContour = new Vector2(-g.y(), g.x()); // perpendicular to the gradient
    double step = 1e-3;
    double changeAlongContour =
        Math.abs(
            ScalarField.WAVES.value(x + alongContour.x() * step, y + alongContour.y() * step)
                - ScalarField.WAVES.value(x, y));
    double changeAlongGradient =
        Math.abs(
            ScalarField.WAVES.value(x + g.x() * step, y + g.y() * step)
                - ScalarField.WAVES.value(x, y));
    assertTrue(changeAlongGradient > changeAlongContour * 50);
  }
}
