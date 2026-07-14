package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.math.Vector2;
import org.physics.engine.pendulum.DoublePendulum;

/**
 * A double pendulum: one pendulum swinging from the end of another. It is the showpiece of chaos.
 * The motion follows fixed laws with nothing random in it, yet it never repeats and is impossible
 * to predict for long, and the fading trail of the lower bob traces beautiful, never-repeating
 * loops.
 *
 * <p>Click anywhere to drop the pendulum from that direction and watch a completely different dance
 * unfold; the smallest change sends it somewhere else entirely. Press R to reset.
 */
public class DoublePendulumScene implements Scene {

  private static final float FIXED_DT = 1f / 240f;
  private static final Vector2 PIVOT = new Vector2(8, 6.8);
  private static final double LENGTH = 2.2;
  private static final int TRAIL_LENGTH = 500;

  private DoublePendulum pendulum;
  private final List<Vector2> trail = new ArrayList<>();
  private float timeBudget;

  @Override
  public String title() {
    return "Double pendulum: chaos";
  }

  @Override
  public String controls() {
    return "click: drop from that direction";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    pendulum = new DoublePendulum(1, 1, LENGTH, LENGTH, 9.8, 2.3, 2.6);
    trail.clear();
    timeBudget = 0f;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      pendulum.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
    trail.add(pendulum.bob2(PIVOT));
    if (trail.size() > TRAIL_LENGTH) {
      trail.remove(0);
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    // Point both rods toward the click, measuring the angle from straight down.
    double dx = worldX - PIVOT.x();
    double dy = worldY - PIVOT.y();
    double angle = Math.atan2(dx, -dy);
    pendulum.setAngles(angle, angle);
    trail.clear();
  }

  @Override
  public void render(ShapeRenderer shapes) {
    Vector2 bob1 = pendulum.bob1(PIVOT);
    Vector2 bob2 = pendulum.bob2(PIVOT);

    shapes.begin(ShapeType.Filled);
    // Fading trail of the lower bob.
    for (int i = 1; i < trail.size(); i++) {
      float fade = i / (float) trail.size();
      shapes.setColor(0.5f, 0.85f, 1f, fade);
      Vector2 p0 = trail.get(i - 1);
      Vector2 p1 = trail.get(i);
      Draw.line(shapes, p0.x(), p0.y(), p1.x(), p1.y(), 0.03f);
    }

    // The two rods.
    shapes.setColor(0.7f, 0.75f, 0.85f, 1f);
    Draw.line(shapes, PIVOT.x(), PIVOT.y(), bob1.x(), bob1.y(), 0.06f);
    Draw.line(shapes, bob1.x(), bob1.y(), bob2.x(), bob2.y(), 0.06f);

    // The pivot and the two bobs.
    shapes.setColor(0.85f, 0.88f, 0.95f, 1f);
    shapes.circle((float) PIVOT.x(), (float) PIVOT.y(), 0.12f, 12);
    shapes.setColor(1f, 0.6f, 0.2f, 1f);
    shapes.circle((float) bob1.x(), (float) bob1.y(), 0.28f, 24);
    shapes.setColor(1f, 0.4f, 0.5f, 1f);
    shapes.circle((float) bob2.x(), (float) bob2.y(), 0.28f, 24);
    shapes.end();
  }
}
