package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.math.Vector2;
import org.physics.engine.track.Bead;
import org.physics.engine.track.Curves;
import org.physics.engine.track.Track;

/**
 * A race that overturns intuition. Three wires run from the same high start to the same low finish:
 * a straight ramp, a bowed arc, and a cycloid. A bead is released on each at the same instant, and
 * they slide down without friction. You would think the straight ramp, being the shortest path,
 * would win. It comes last. The cycloid wins, because it drops steeply at the start and the speed
 * it banks early more than makes up for its longer route. This is the brachistochrone, the
 * "shortest time" curve, and it was one of the founding problems of the calculus of variations.
 *
 * <p>Press R to run the race again.
 */
public class BrachistochroneScene implements Scene {

  private static final float FIXED_DT = 1f / 240f;
  private static final double GRAVITY = 9.8;
  private static final int SAMPLES = 140;
  private static final Vector2 START = new Vector2(2.5, 7.8);
  private static final Vector2 END = new Vector2(13.5, 1.8);

  private static final String[] NAMES = {"straight", "arc", "cycloid"};
  private static final float[][] COLORS = {
    {0.95f, 0.45f, 0.4f}, {1f, 0.8f, 0.35f}, {0.45f, 0.9f, 0.55f}
  };

  private final Track[] tracks = new Track[3];
  private final Bead[] beads = new Bead[3];
  private final double[] times = new double[3];
  private float timeBudget;

  @Override
  public String title() {
    return "Brachistochrone: the fastest descent";
  }

  @Override
  public String controls() {
    return "R: race again";
  }

  @Override
  public List<String> readouts() {
    return List.of(label(0), label(1), label(2));
  }

  private String label(int i) {
    String done = beads[i].isFinished() ? " (done)" : "";
    return NAMES[i] + ": " + Draw.num(times[i], 2) + "s" + done;
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    tracks[0] = Curves.straightLine(START, END, SAMPLES);
    tracks[1] = Curves.arc(START, END, 2.5, SAMPLES);
    tracks[2] = Curves.cycloid(START, END, SAMPLES);
    for (int i = 0; i < 3; i++) {
      beads[i] = new Bead(0, 0);
      times[i] = 0;
    }
    timeBudget = 0f;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      for (int i = 0; i < 3; i++) {
        if (!beads[i].isFinished()) {
          beads[i].step(tracks[i], GRAVITY, FIXED_DT);
          times[i] += FIXED_DT;
        }
      }
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    // The three wires.
    for (int i = 0; i < 3; i++) {
      shapes.setColor(COLORS[i][0], COLORS[i][1], COLORS[i][2], 0.9f);
      Vector2[] points = tracks[i].points();
      for (int k = 1; k < points.length; k++) {
        Draw.line(
            shapes, points[k - 1].x(), points[k - 1].y(), points[k].x(), points[k].y(), 0.04f);
      }
    }
    // Start and finish markers.
    shapes.setColor(0.85f, 0.88f, 0.95f, 1f);
    shapes.circle((float) START.x(), (float) START.y(), 0.15f, 16);
    shapes.circle((float) END.x(), (float) END.y(), 0.15f, 16);
    // The beads.
    for (int i = 0; i < 3; i++) {
      shapes.setColor(COLORS[i][0], COLORS[i][1], COLORS[i][2], 1f);
      Vector2 p = beads[i].positionOn(tracks[i]);
      shapes.circle((float) p.x(), (float) p.y(), 0.22f, 20);
    }
    shapes.end();
  }
}
