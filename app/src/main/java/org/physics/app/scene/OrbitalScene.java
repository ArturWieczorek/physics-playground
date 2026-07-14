package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import java.util.Random;
import org.physics.engine.quantum.HydrogenOrbital;

/**
 * The electron orbitals of hydrogen, drawn the honest way: as clouds of probability. The electron
 * has no path and no fixed place; the wavefunction only says how likely it is to be found at each
 * point. So we scatter thousands of dots, packing them where the electron is likely to be (where
 * the wavefunction squared is large) and thinning them out where it is not. The result is the shape
 * of the orbital itself, the round 1s ball, the dumbbell of a p orbital, the clover of a d.
 *
 * <p>Drag to orbit the view. Use the left and right arrows to step through the orbitals. Each dot
 * is coloured by the sign of the wavefunction there (its phase), so a p orbital's two lobes and a d
 * orbital's alternating lobes show up in two colours.
 */
public class OrbitalScene implements Scene {

  private static final int TARGET_POINTS = 8000;
  // Peaked orbitals (1s) accept only a small fraction of proposals, so give the sampler a generous
  // budget. This runs only when you switch orbitals, not every frame.
  private static final int MAX_ATTEMPTS = 2_000_000;
  private static final float CAMERA_DISTANCE = 13f;

  private final HydrogenOrbital[] orbitals = HydrogenOrbital.values();
  private int current;

  private PerspectiveCamera camera;
  private double azimuth = 0.9;
  private double elevation = 0.5;

  private float[] px = new float[0];
  private float[] py = new float[0];
  private float[] pz = new float[0];
  private boolean[] positive = new boolean[0];
  private int count;

  @Override
  public String title() {
    return "Electron orbitals: probability clouds";
  }

  @Override
  public String controls() {
    return "drag: rotate   left/right: orbital";
  }

  @Override
  public List<String> readouts() {
    return List.of("orbital: " + orbitals[current].label());
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
    azimuth = 0.9;
    elevation = 0.5;
    buildCloud();
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      current = (current + 1) % orbitals.length;
      buildCloud();
    } else if (keycode == Input.Keys.LEFT) {
      current = (current - 1 + orbitals.length) % orbitals.length;
      buildCloud();
    }
  }

  // Scatters dots by the probability density, using rejection sampling: propose a random point,
  // keep it with a chance proportional to how likely the electron is to be there.
  private void buildCloud() {
    HydrogenOrbital orbital = orbitals[current];
    double reach = extent(orbital);
    double worldScale = 6.5 / reach;

    Random random = new Random(1234L + current);
    double maxDensity = 1e-12;
    for (int i = 0; i < 20000; i++) {
      double x = (random.nextDouble() * 2 - 1) * reach;
      double y = (random.nextDouble() * 2 - 1) * reach;
      double z = (random.nextDouble() * 2 - 1) * reach;
      maxDensity = Math.max(maxDensity, orbital.density(x, y, z));
    }
    maxDensity *= 1.05;

    px = new float[TARGET_POINTS];
    py = new float[TARGET_POINTS];
    pz = new float[TARGET_POINTS];
    positive = new boolean[TARGET_POINTS];
    count = 0;

    random = new Random(9876L + current);
    int attempts = 0;
    while (count < TARGET_POINTS && attempts < MAX_ATTEMPTS) {
      attempts++;
      double x = (random.nextDouble() * 2 - 1) * reach;
      double y = (random.nextDouble() * 2 - 1) * reach;
      double z = (random.nextDouble() * 2 - 1) * reach;
      if (random.nextDouble() * maxDensity < orbital.density(x, y, z)) {
        px[count] = (float) (x * worldScale);
        py[count] = (float) (y * worldScale);
        pz[count] = (float) (z * worldScale);
        positive[count] = orbital.value(x, y, z) >= 0;
        count++;
      }
    }
  }

  // How far out to sample, in Bohr radii, big enough to hold each orbital.
  private static double extent(HydrogenOrbital orbital) {
    return switch (orbital) {
      case ONE_S -> 6;
      case TWO_S, TWO_P_Z, TWO_P_X -> 15;
      default -> 24;
    };
  }

  @Override
  public void update(float dt) {
    if (Gdx.input.isTouched()) {
      azimuth -= Gdx.input.getDeltaX() * 0.01;
      elevation += Gdx.input.getDeltaY() * 0.01;
      elevation = Math.max(-1.4, Math.min(1.4, elevation));
    } else {
      azimuth += dt * 0.25;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
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

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    shapes.setProjectionMatrix(camera.combined);

    shapes.begin(ShapeType.Point);
    for (int i = 0; i < count; i++) {
      if (positive[i]) {
        shapes.setColor(1f, 0.6f, 0.3f, 0.8f); // positive phase, warm
      } else {
        shapes.setColor(0.3f, 0.7f, 1f, 0.8f); // negative phase, cool
      }
      shapes.point(px[i], py[i], pz[i]);
    }
    shapes.end();

    // The nucleus.
    shapes.begin(ShapeType.Line);
    shapes.setColor(1f, 1f, 1f, 1f);
    shapes.line(-0.15f, 0, 0, 0.15f, 0, 0);
    shapes.line(0, -0.15f, 0, 0, 0.15f, 0);
    shapes.line(0, 0, -0.15f, 0, 0, 0.15f);
    shapes.end();

    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
  }
}
