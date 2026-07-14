package org.physics.engine.field;

import org.physics.engine.math.Vector2;

/**
 * A scalar field: a landscape that gives one number, a height, at every point of the plane. It is
 * the setting for two of the central ideas of multivariable calculus, the partial derivative and
 * the gradient.
 *
 * <p>A <b>partial derivative</b> is a slope measured while walking in just one direction. Stand on
 * the landscape and step east (increasing x) holding y fixed: how steeply does the height change?
 * That is the partial derivative with respect to x. Step north instead and you get the partial with
 * respect to y. Each partial answers "how does the height change if I move along this one axis?".
 *
 * <p>The <b>gradient</b> gathers both partials into a single vector, {@code (df/dx, df/dy)}. It has
 * a beautiful meaning: it points in the direction of steepest ascent, straight uphill, and its
 * length is how steep that climb is. It is always perpendicular to the contour lines (the paths of
 * constant height), because the flattest direction to walk is along a contour and the steepest is
 * square across it. Where the gradient is zero, the ground is level: a peak, a valley bottom, or a
 * saddle.
 *
 * <p>We compute the partials numerically, by nudging the point a hair in each direction and
 * measuring the change (a central difference). That works for any field, without anyone having to
 * work out the derivative by hand, and the scene uses it to draw the gradient arrows.
 */
public enum ScalarField {

  /** A single smooth hill centred at the origin. The gradient everywhere points toward the top. */
  PEAK("single peak") {
    @Override
    public double value(double x, double y) {
      return Math.exp(-(x * x + y * y) / 4.0);
    }
  },

  /**
   * A saddle: uphill along x, downhill along y. The gradient sweeps around the pass in the middle.
   */
  SADDLE("saddle") {
    @Override
    public double value(double x, double y) {
      return 0.25 * (x * x - y * y);
    }
  },

  /** A rolling egg-box of hills and hollows. */
  WAVES("waves") {
    @Override
    public double value(double x, double y) {
      return Math.sin(x) * Math.cos(y);
    }
  },

  /** Two separate hills, so you can see the gradient part company along the valley between them. */
  TWIN_PEAKS("two peaks") {
    @Override
    public double value(double x, double y) {
      double left = Math.exp(-((x + 1.7) * (x + 1.7) + y * y) / 2.0);
      double right = Math.exp(-((x - 1.7) * (x - 1.7) + y * y) / 2.0);
      return left + right;
    }
  };

  private static final double STEP = 1e-3;

  private final String label;

  ScalarField(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  /** The height of the field at a point. */
  public abstract double value(double x, double y);

  /** The partial derivative with respect to x: the slope walking east, holding y fixed. */
  public double partialX(double x, double y) {
    return (value(x + STEP, y) - value(x - STEP, y)) / (2 * STEP);
  }

  /** The partial derivative with respect to y: the slope walking north, holding x fixed. */
  public double partialY(double x, double y) {
    return (value(x, y + STEP) - value(x, y - STEP)) / (2 * STEP);
  }

  /** The gradient, the two partials gathered into one vector pointing straight uphill. */
  public Vector2 gradient(double x, double y) {
    return new Vector2(partialX(x, y), partialY(x, y));
  }
}
