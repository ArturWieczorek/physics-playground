package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.quantum.TwoElectronState;

/**
 * The Pauli exclusion principle, drawn as a landscape. This is the first scene in three dimensions:
 * we plot the two-electron wavefunction (ch18) as a surface, with the two electron positions
 * running along the ground and the wavefunction's value as the height. Drag to orbit the camera and
 * see it from any angle.
 *
 * <p>Press space to switch between the symmetric combination (bosons, which may pile up together)
 * and the antisymmetric one (fermions, which electrons are). In the antisymmetric case a sharp
 * trench is cut along the diagonal where the two electrons would coincide: the surface is pinned to
 * zero there, because two electrons can never be in the same place in the same state. That trench
 * is the exclusion principle made visible.
 *
 * <p>The surface is drawn as a wireframe with the existing shape renderer under a perspective
 * camera, coloured from cool (low) to hot (high) by height.
 */
public class PauliScene implements Scene {

  private static final int GRID = 46;
  private static final double L = 1.0; // half-width of the box the electrons live in
  private static final float GROUND = 5.0f; // how far the (x1, x2) plane spreads in world units
  private static final float HEIGHT = 2.6f; // vertical scale of the wavefunction
  private static final float CAMERA_DISTANCE = 12f;

  private final TwoElectronState state = new TwoElectronState(L);
  private PerspectiveCamera camera;
  private boolean antisymmetric = true; // electrons are fermions, so start here
  private double azimuth = 0.9;
  private double elevation = 0.6;

  @Override
  public String title() {
    return "Pauli exclusion: two-electron wavefunction";
  }

  @Override
  public String controls() {
    return "drag: rotate   space: boson / fermion";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "state: " + (antisymmetric ? "antisymmetric (fermion)" : "symmetric (boson)"),
        antisymmetric ? "electrons: forbidden on the diagonal" : "particles: may coincide");
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
    antisymmetric = true;
    azimuth = 0.9;
    elevation = 0.6;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.SPACE) {
      antisymmetric = !antisymmetric;
    }
  }

  @Override
  public void update(float dt) {
    if (Gdx.input.isTouched()) {
      // Drag to orbit.
      azimuth -= Gdx.input.getDeltaX() * 0.01;
      elevation += Gdx.input.getDeltaY() * 0.01;
      elevation = Math.max(0.1, Math.min(1.4, elevation));
    } else {
      azimuth += dt * 0.25; // gently spin when left alone
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    positionCamera();

    // Sample the wavefunction on the grid, tracking the largest value so we can scale it.
    float[][] height = new float[GRID + 1][GRID + 1];
    double maxAbs = 1e-9;
    for (int i = 0; i <= GRID; i++) {
      double x1 = -L + 2 * L * i / GRID;
      for (int j = 0; j <= GRID; j++) {
        double x2 = -L + 2 * L * j / GRID;
        double v = state.value(x1, x2, antisymmetric);
        height[i][j] = (float) v;
        maxAbs = Math.max(maxAbs, Math.abs(v));
      }
    }

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    shapes.setProjectionMatrix(camera.combined);
    shapes.begin(ShapeType.Line);
    for (int i = 0; i <= GRID; i++) {
      for (int j = 0; j <= GRID; j++) {
        if (i < GRID) {
          drawEdge(shapes, i, j, i + 1, j, height, maxAbs);
        }
        if (j < GRID) {
          drawEdge(shapes, i, j, i, j + 1, height, maxAbs);
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
    float px = (float) (r * Math.cos(elevation) * Math.sin(azimuth));
    float py = (float) (r * Math.sin(elevation));
    float pz = (float) (r * Math.cos(elevation) * Math.cos(azimuth));
    camera.position.set(px, py, pz);
    camera.up.set(0, 1, 0);
    camera.lookAt(0, 0, 0);
    camera.near = 0.1f;
    camera.far = 100f;
    camera.update();
  }

  private void drawEdge(
      ShapeRenderer shapes, int i1, int j1, int i2, int j2, float[][] height, double maxAbs) {
    float ax = worldGround(i1);
    float az = worldGround(j1);
    float ay = (float) (height[i1][j1] / maxAbs) * HEIGHT;
    float bx = worldGround(i2);
    float bz = worldGround(j2);
    float by = (float) (height[i2][j2] / maxAbs) * HEIGHT;
    // Colour by the average height of the edge, cool (low) to hot (high).
    setHeightColor(shapes, (height[i1][j1] + height[i2][j2]) / 2f, maxAbs);
    shapes.line(ax, ay, az, bx, by, bz);
  }

  private float worldGround(int index) {
    return (index / (float) GRID * 2f - 1f) * GROUND;
  }

  // Maps a wavefunction value to a rainbow colour: blue for the deepest, red for the highest.
  private void setHeightColor(ShapeRenderer shapes, float value, double maxAbs) {
    float t = (float) (value / maxAbs + 1) / 2f; // 0..1
    float hue = 0.66f * (1 - t); // blue (low) -> red (high)
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
