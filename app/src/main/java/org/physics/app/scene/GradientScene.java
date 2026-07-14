package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.field.ScalarField;
import org.physics.engine.math.Vector2;

/**
 * Gradients and partial derivatives, in three dimensions. A scalar field is drawn as a landscape:
 * the height of the surface is the field's value. On the flat ground beneath it, a grid of white
 * arrows shows the gradient, each pointing straight uphill and as long as the slope is steep. Watch
 * how the arrows always run square across the contours of the surface and grow on the steep flanks,
 * shrinking to nothing at a peak or a saddle where the ground is momentarily level.
 *
 * <p>Each arrow is the pair of partial derivatives, one along x and one along y, added together as
 * a vector. That is all a gradient is: assemble the slope in each axis direction and you get the
 * single direction of steepest climb.
 *
 * <p>Drag to orbit. Use the left and right arrows to change the landscape.
 */
public class GradientScene implements Scene {

  private static final int GRID = 54;
  private static final double RANGE = 4.0; // domain is [-RANGE, RANGE] in x and y
  private static final float GROUND = 5.5f;
  private static final float HEIGHT = 3.0f;
  private static final float CAMERA_DISTANCE = 13f;

  private final ScalarField[] fields = ScalarField.values();
  private int current;
  private PerspectiveCamera camera;
  private double azimuth = 0.8;
  private double elevation = 0.5;
  private float[][] surface;
  private double surfaceMax;
  private boolean dirty = true;

  @Override
  public String title() {
    return "Gradients and partial derivatives";
  }

  @Override
  public String controls() {
    return "drag: rotate   left/right: landscape";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "field: " + fields[current].label(), "white arrows: gradient (uphill, steepest ascent)");
  }

  @Override
  public void show() {
    if (camera == null) {
      camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    reset();
  }

  @Override
  public void reset() {
    current = 0;
    azimuth = 0.8;
    elevation = 0.5;
    dirty = true;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      current = (current + 1) % fields.length;
    } else if (keycode == Input.Keys.LEFT) {
      current = (current - 1 + fields.length) % fields.length;
    } else {
      return;
    }
    dirty = true;
  }

  // Camera orbiting lives here (not in update) so it keeps working even while the app is paused.
  private void orbit() {
    if (Gdx.input.isTouched()) {
      azimuth -= Gdx.input.getDeltaX() * 0.01;
      elevation += Gdx.input.getDeltaY() * 0.01;
      elevation = Math.max(0.1, Math.min(1.4, elevation));
    } else {
      azimuth += Gdx.graphics.getDeltaTime() * 0.2;
    }
  }

  // Sample the surface once, only when the landscape changes.
  private void sampleSurface() {
    ScalarField field = fields[current];
    surface = new float[GRID + 1][GRID + 1];
    surfaceMax = 1e-9;
    for (int i = 0; i <= GRID; i++) {
      double x = domain(i);
      for (int j = 0; j <= GRID; j++) {
        double v = field.value(x, domain(j));
        surface[i][j] = (float) v;
        surfaceMax = Math.max(surfaceMax, Math.abs(v));
      }
    }
    dirty = false;
  }

  @Override
  public void render(ShapeRenderer shapes) {
    orbit();
    positionCamera();
    if (dirty) {
      sampleSurface();
    }
    ScalarField field = fields[current];

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    shapes.setProjectionMatrix(camera.combined);

    shapes.begin(ShapeType.Line);
    // The surface, coloured by height.
    for (int i = 0; i <= GRID; i++) {
      for (int j = 0; j <= GRID; j++) {
        if (i < GRID) {
          surfaceEdge(shapes, i, j, i + 1, j, surface, surfaceMax);
        }
        if (j < GRID) {
          surfaceEdge(shapes, i, j, i, j + 1, surface, surfaceMax);
        }
      }
    }

    // The gradient arrows on the ground plane, normalised so the steepest is a fixed length.
    double maxGrad = 1e-9;
    for (double x = -RANGE + 0.5; x <= RANGE - 0.5; x += 1.0) {
      for (double y = -RANGE + 0.5; y <= RANGE - 0.5; y += 1.0) {
        maxGrad = Math.max(maxGrad, field.gradient(x, y).length());
      }
    }
    shapes.setColor(0.95f, 0.96f, 1f, 1f);
    for (double x = -RANGE + 0.5; x <= RANGE - 0.5; x += 1.0) {
      for (double y = -RANGE + 0.5; y <= RANGE - 0.5; y += 1.0) {
        Vector2 g = field.gradient(x, y);
        double scale = 0.9 / maxGrad;
        float wx = worldFromDomain(x);
        float wz = worldFromDomain(y);
        groundArrow(shapes, wx, wz, wx + (float) (g.x() * scale), wz + (float) (g.y() * scale));
      }
    }
    shapes.end();

    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
  }

  private double domain(int index) {
    return -RANGE + 2 * RANGE * index / GRID;
  }

  private float worldFromDomain(double d) {
    return (float) (d / RANGE * GROUND);
  }

  private void surfaceEdge(
      ShapeRenderer shapes, int i1, int j1, int i2, int j2, float[][] height, double maxAbs) {
    float ay = (float) (height[i1][j1] / maxAbs) * HEIGHT;
    float by = (float) (height[i2][j2] / maxAbs) * HEIGHT;
    setHeightColor(shapes, (height[i1][j1] + height[i2][j2]) / 2f, maxAbs);
    shapes.line(
        worldFromDomain(domain(i1)),
        ay,
        worldFromDomain(domain(j1)),
        worldFromDomain(domain(i2)),
        by,
        worldFromDomain(domain(j2)));
  }

  // A flat arrow on the ground plane (y = 0), with a small two-stroke head at the tip.
  private void groundArrow(ShapeRenderer shapes, float x1, float z1, float x2, float z2) {
    shapes.line(x1, 0, z1, x2, 0, z2);
    double dx = x2 - x1;
    double dz = z2 - z1;
    double len = Math.hypot(dx, dz);
    if (len < 1e-6) {
      return;
    }
    double ux = dx / len;
    double uz = dz / len;
    double cos = Math.cos(Math.toRadians(150));
    double sin = Math.sin(Math.toRadians(150));
    double head = 0.16;
    shapes.line(
        x2,
        0,
        z2,
        x2 + (float) ((ux * cos - uz * sin) * head),
        0,
        z2 + (float) ((ux * sin + uz * cos) * head));
    shapes.line(
        x2,
        0,
        z2,
        x2 + (float) ((ux * cos + uz * sin) * head),
        0,
        z2 + (float) ((-ux * sin + uz * cos) * head));
  }

  private void positionCamera() {
    camera.viewportWidth = Gdx.graphics.getWidth();
    camera.viewportHeight = Gdx.graphics.getHeight();
    float r = CAMERA_DISTANCE;
    camera.position.set(
        (float) (r * Math.cos(elevation) * Math.sin(azimuth)),
        (float) (r * Math.sin(elevation)),
        (float) (r * Math.cos(elevation) * Math.cos(azimuth)));
    camera.up.set(0, 1, 0);
    camera.lookAt(0, 0, 0);
    camera.near = 0.1f;
    camera.far = 100f;
    camera.update();
  }

  private void setHeightColor(ShapeRenderer shapes, float value, double maxAbs) {
    float t = (float) (value / maxAbs + 1) / 2f;
    float hue = 0.66f * (1 - t);
    float h6 = hue * 6f;
    int sector = (int) h6 % 6;
    float f = h6 - (int) h6;
    float q = 1 - f;
    float r;
    float g;
    float b;
    switch (sector) {
      case 0 -> {
        r = 1;
        g = f;
        b = 0;
      }
      case 1 -> {
        r = q;
        g = 1;
        b = 0;
      }
      case 2 -> {
        r = 0;
        g = 1;
        b = f;
      }
      case 3 -> {
        r = 0;
        g = q;
        b = 1;
      }
      case 4 -> {
        r = f;
        g = 0;
        b = 1;
      }
      default -> {
        r = 1;
        g = 0;
        b = q;
      }
    }
    shapes.setColor(r, g, b, 1f);
  }
}
