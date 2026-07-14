package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.chaos.LorenzSystem;

/**
 * The Lorenz attractor, the picture of chaos. The point never stops moving and never retraces its
 * path, yet it never escapes: it winds forever around two centres, sketching a shape like a
 * butterfly's wings. This is a "strange attractor", and it is the reason the weather cannot be
 * forecast far ahead, because two nearly identical starts end up on completely different parts of
 * the wing (ch16's chaos, in a new guise).
 *
 * <p>Drag to orbit the shape as it draws itself. The trail fades from the bright leading point back
 * into the past.
 */
public class LorenzScene implements Scene {

  private static final double STEP = 0.005;
  private static final float SCALE = 0.15f;
  private static final int TRAIL_LENGTH = 3600;
  private static final float CAMERA_DISTANCE = 13f;

  private LorenzSystem system;
  private final List<float[]> trail = new ArrayList<>();
  private PerspectiveCamera camera;
  private double azimuth = 0.7;
  private double elevation = 0.35;
  private double timeBudget;

  @Override
  public String title() {
    return "Lorenz attractor: the shape of chaos";
  }

  @Override
  public String controls() {
    return "drag: rotate   R: restart";
  }

  @Override
  public List<String> readouts() {
    return List.of("a strange attractor", "deterministic, yet unpredictable");
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
    system = new LorenzSystem(1, 1, 1);
    trail.clear();
    timeBudget = 0;
    azimuth = 0.7;
    elevation = 0.35;
  }

  @Override
  public void update(float dt) {
    if (Gdx.input.isTouched()) {
      azimuth -= Gdx.input.getDeltaX() * 0.01;
      elevation += Gdx.input.getDeltaY() * 0.01;
      elevation = Math.max(-1.4, Math.min(1.4, elevation));
    } else {
      azimuth += dt * 0.15;
    }

    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= STEP) {
      system.step(STEP);
      timeBudget -= STEP;
      // Map Lorenz coordinates into a centred world position.
      trail.add(
          new float[] {
            (float) (system.x() * SCALE),
            (float) ((system.z() - 25) * SCALE),
            (float) (system.y() * SCALE)
          });
      if (trail.size() > TRAIL_LENGTH) {
        trail.remove(0);
      }
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    positionCamera();
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    shapes.setProjectionMatrix(camera.combined);

    shapes.begin(ShapeType.Line);
    for (int i = 1; i < trail.size(); i++) {
      float fade = i / (float) trail.size(); // brighter toward the newest point
      // Colour shifts blue to pink around the wings, brightening at the head.
      shapes.setColor(0.3f + 0.6f * fade, 0.4f * (1 - fade) + 0.2f, 0.7f + 0.3f * fade, fade);
      float[] a = trail.get(i - 1);
      float[] b = trail.get(i);
      shapes.line(a[0], a[1], a[2], b[0], b[1], b[2]);
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
    camera.lookAt(0, 0, 0);
    camera.near = 0.1f;
    camera.far = 100f;
    camera.update();
  }
}
