package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.transform.LaplaceFunction;

/**
 * The Laplace transform, drawn the way it is best understood: as a landscape over the complex
 * plane. The flat ground is the complex variable s: left-to-right is its real part (how fast a
 * signal grows or decays) and front-to-back is its imaginary part (how fast it oscillates). The
 * height of the surface is the size of the transform there, and the whole point is the spikes.
 *
 * <p>Those spikes are the poles: the values of s where the transform blows up to infinity, and they
 * capture the essence of the signal. Cycle through the signals with the arrows and watch where the
 * poles sit. An oscillation puts a pair of poles up the imaginary axis (at the oscillation's
 * frequency); a decaying signal puts a single pole out on the negative real axis. Drag to orbit.
 *
 * <p>The spikes truly go to infinity, so we cap the height to keep them on screen; the colour still
 * runs from cool in the valleys to hot at the peaks.
 */
public class LaplaceScene implements Scene {

  private static final int GRID = 64;
  private static final double SIGMA_MIN = -1.5;
  private static final double SIGMA_MAX = 2.5;
  private static final double OMEGA_MIN = -3.0;
  private static final double OMEGA_MAX = 3.0;
  private static final double HEIGHT_CAP = 4.0; // clip the infinite spikes here
  private static final float HEIGHT = 3.4f;
  private static final float GROUND = 5.5f;
  private static final float CAMERA_DISTANCE = 13f;

  private final LaplaceFunction[] functions = LaplaceFunction.values();
  private int current;
  private PerspectiveCamera camera;
  private double azimuth = 0.8;
  private double elevation = 0.45;
  private float[][] surface;
  private boolean dirty = true;

  @Override
  public String title() {
    return "Laplace transform: poles in the complex plane";
  }

  @Override
  public String controls() {
    return "drag: rotate   left/right: signal";
  }

  @Override
  public List<String> readouts() {
    return List.of("signal: " + functions[current].label(), "height = |F(s)|, spikes are poles");
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
    elevation = 0.45;
    dirty = true;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      current = (current + 1) % functions.length;
    } else if (keycode == Input.Keys.LEFT) {
      current = (current - 1 + functions.length) % functions.length;
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

  // Sample the surface once, only when the signal changes, not every frame.
  private void sampleSurface() {
    LaplaceFunction f = functions[current];
    surface = new float[GRID + 1][GRID + 1];
    for (int i = 0; i <= GRID; i++) {
      double sigma = SIGMA_MIN + (SIGMA_MAX - SIGMA_MIN) * i / GRID;
      for (int j = 0; j <= GRID; j++) {
        double omega = OMEGA_MIN + (OMEGA_MAX - OMEGA_MIN) * j / GRID;
        surface[i][j] = (float) Math.min(HEIGHT_CAP, f.magnitude(sigma, omega)); // clip the spike
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

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    shapes.setProjectionMatrix(camera.combined);
    shapes.begin(ShapeType.Line);
    for (int i = 0; i <= GRID; i++) {
      for (int j = 0; j <= GRID; j++) {
        if (i < GRID) {
          drawEdge(shapes, i, j, i + 1, j, surface);
        }
        if (j < GRID) {
          drawEdge(shapes, i, j, i, j + 1, surface);
        }
      }
    }
    shapes.end();
    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
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
    camera.lookAt(0, 1.2f, 0);
    camera.near = 0.1f;
    camera.far = 100f;
    camera.update();
  }

  private void drawEdge(ShapeRenderer shapes, int i1, int j1, int i2, int j2, float[][] height) {
    float ay = height[i1][j1] / (float) HEIGHT_CAP * HEIGHT;
    float by = height[i2][j2] / (float) HEIGHT_CAP * HEIGHT;
    setHeightColor(shapes, (height[i1][j1] + height[i2][j2]) / 2f);
    shapes.line(worldX(i1), ay, worldZ(j1), worldX(i2), by, worldZ(j2));
  }

  private float worldX(int i) {
    return (i / (float) GRID * 2f - 1f) * GROUND;
  }

  private float worldZ(int j) {
    return (j / (float) GRID * 2f - 1f) * GROUND;
  }

  // Cool in the valleys, hot at the peaks.
  private void setHeightColor(ShapeRenderer shapes, float h) {
    float t = (float) Math.min(1.0, h / HEIGHT_CAP);
    float hue = 0.66f * (1 - t);
    float h6 = hue * 6f;
    int sector = (int) h6 % 6;
    float frac = h6 - (int) h6;
    float q = 1 - frac;
    float r;
    float g;
    float b;
    switch (sector) {
      case 0 -> {
        r = 1;
        g = frac;
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
        b = frac;
      }
      case 3 -> {
        r = 0;
        g = q;
        b = 1;
      }
      case 4 -> {
        r = frac;
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
