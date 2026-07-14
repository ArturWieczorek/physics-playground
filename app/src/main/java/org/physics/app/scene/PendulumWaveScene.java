package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.pendulum.SimplePendulum;

/**
 * A pendulum wave: a row of pendulums whose lengths are tuned so that, over one long cycle, each
 * swings a whole number of times, and each does exactly one more swing than its neighbour. They
 * start swinging together, then drift apart into travelling waves, curling snakes and shimmering
 * patterns, before sweeping back into step and repeating. Nothing is coordinating them; the
 * patterns come purely from the steady drift of slightly different swing rates.
 *
 * <p>It rests on the one fact from ch25: a longer pendulum swings more slowly. Tune the lengths
 * just right and the phases fan out and refold like clockwork. Press R to line them up again.
 */
public class PendulumWaveScene implements Scene {

  private static final int COUNT = 15;
  private static final double GRAVITY = 9.8;
  private static final double AMPLITUDE = 0.2; // radians, small so the bobs do not collide
  private static final double COMMON_PERIOD = 50.0; // time for the whole row to realign
  private static final double BASE_SWINGS = 12; // swings the longest pendulum makes in that time
  private static final float PIVOT_Y = 8.4f;

  private final SimplePendulum[] pendulums = new SimplePendulum[COUNT];
  private final float[] pivotX = new float[COUNT];
  private float time;

  @Override
  public String title() {
    return "Pendulum wave";
  }

  @Override
  public String controls() {
    return "R: line them up again";
  }

  @Override
  public List<String> readouts() {
    return List.of(COUNT + " pendulums, tuned lengths", "watch the travelling waves form");
  }

  @Override
  public void show() {
    for (int i = 0; i < COUNT; i++) {
      // Each pendulum makes one more swing per cycle than the last, so its length is set from that.
      double swings = BASE_SWINGS + i;
      double omega = 2 * Math.PI * swings / COMMON_PERIOD;
      double length = GRAVITY / (omega * omega);
      pendulums[i] = new SimplePendulum(length, GRAVITY, AMPLITUDE);
      pivotX[i] = 1.0f + i * (14.0f / (COUNT - 1));
    }
    reset();
  }

  @Override
  public void reset() {
    time = 0f;
  }

  @Override
  public void update(float dt) {
    time += Math.min(dt, 0.05f);
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    // The bar the pendulums hang from.
    shapes.setColor(0.5f, 0.52f, 0.6f, 1f);
    Draw.line(shapes, 0.6, PIVOT_Y, 15.4, PIVOT_Y, 0.06f);

    for (int i = 0; i < COUNT; i++) {
      double angle = pendulums[i].angleAt(time);
      double length = pendulums[i].length();
      float bx = (float) (pivotX[i] + length * Math.sin(angle));
      float by = (float) (PIVOT_Y - length * Math.cos(angle));

      // String, then a bob coloured across the row so the wave is easy to follow.
      shapes.setColor(0.5f, 0.55f, 0.65f, 0.8f);
      Draw.line(shapes, pivotX[i], PIVOT_Y, bx, by, 0.02f);
      float t = i / (float) (COUNT - 1);
      shapes.setColor(0.35f + 0.6f * t, 0.55f, 1f - 0.55f * t, 1f);
      shapes.circle(bx, by, 0.22f, 20);
    }
    shapes.end();
  }
}
