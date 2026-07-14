package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayDeque;
import java.util.Deque;
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
  private final Deque<float[]> trail = new ArrayDeque<>();
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

  // Only the simulation lives in update, so pausing freezes the attractor. Camera orbit is in
  // render (see orbit()), so you can still rotate a frozen attractor.
  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= STEP) {
      system.step(STEP);
      timeBudget -= STEP;
      // Map Lorenz coordinates into a centred world position.
      trail.addLast(
          new float[] {
            (float) (system.x() * SCALE),
            (float) ((system.z() - 25) * SCALE),
            (float) (system.y() * SCALE)
          });
      if (trail.size() > TRAIL_LENGTH) {
        trail.removeFirst();
      }
    }
  }

  private void orbit() {
    if (Gdx.input.isTouched()) {
      azimuth -= Gdx.input.getDeltaX() * 0.01;
      elevation += Gdx.input.getDeltaY() * 0.01;
      elevation = Math.max(-1.4, Math.min(1.4, elevation));
    } else {
      azimuth += Gdx.graphics.getDeltaTime() * 0.15;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    orbit();
    positionCamera();
    // Only a translucent trail is drawn, so no depth test is needed; draw order (oldest first) is
    // already correct.
    shapes.setProjectionMatrix(camera.combined);

    shapes.begin(ShapeType.Line);
    int size = trail.size();
    int i = 0;
    float[] prev = null;
    for (float[] point : trail) {
      if (prev != null) {
        float fade = i / (float) size; // brighter toward the newest point
        shapes.setColor(0.3f + 0.6f * fade, 0.4f * (1 - fade) + 0.2f, 0.7f + 0.3f * fade, fade);
        shapes.line(prev[0], prev[1], prev[2], point[0], point[1], point[2]);
      }
      prev = point;
      i++;
    }
    shapes.end();
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
