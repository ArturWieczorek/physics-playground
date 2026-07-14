package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.physics.engine.wave.FourierSeries;

/**
 * Fourier series, drawn as spinning circles. Each sine wave in the series is a rotating vector, and
 * stacking the vectors tip to tip makes a chain of circles turning at different speeds. The end of
 * the chain traces out the wave, and its height is fed to the graph on the right, which scrolls to
 * reveal the shape being built.
 *
 * <p>Start with one circle and you get a plain sine. Add more (up and down arrows) and watch the
 * flat tops and sharp jumps of a square wave slowly appear out of nothing but smooth sines. Space
 * switches between a square wave and a sawtooth.
 */
public class FourierScene implements Scene {

  private static final float CX = 4.5f;
  private static final float CY = 4.6f;
  private static final float SCALE = 1.8f; // world units per unit of wave value
  private static final float TRACE_X = 8.2f;
  private static final float TRACE_STEP = 0.045f;
  private static final int TRACE_LENGTH = 150;
  private static final float SPEED = 1.3f;

  private boolean square = true;
  private int terms = 4;
  private FourierSeries series;
  private float time;
  private final Deque<Float> trace = new ArrayDeque<>();

  @Override
  public String title() {
    return "Fourier series: circles drawing a wave";
  }

  @Override
  public String controls() {
    return "up/down: harmonics   space: square / sawtooth";
  }

  @Override
  public List<String> readouts() {
    return List.of("shape: " + (square ? "square" : "sawtooth"), "harmonics: " + terms);
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    square = true;
    terms = 4;
    rebuild();
    time = 0f;
    trace.clear();
  }

  private void rebuild() {
    series = square ? FourierSeries.squareWave(terms) : FourierSeries.sawtooth(terms);
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.UP) {
      terms = Math.min(40, terms + 1);
      rebuild();
    } else if (keycode == Input.Keys.DOWN) {
      terms = Math.max(1, terms - 1);
      rebuild();
    } else if (keycode == Input.Keys.SPACE) {
      square = !square;
      rebuild();
      trace.clear();
    }
  }

  @Override
  public void update(float dt) {
    time += Math.min(dt, 0.05f) * SPEED;
    trace.addFirst((float) series.valueAt(time) * SCALE); // same scale as the epicycle tip
    while (trace.size() > TRACE_LENGTH) {
      trace.removeLast();
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    // The epicycle circles and the radial arms, drawn thin.
    float x = CX;
    float y = CY;
    shapes.begin(ShapeType.Line);
    shapes.setColor(0.4f, 0.5f, 0.65f, 1f);
    for (FourierSeries.Harmonic h : series.harmonics()) {
      double angle = h.frequency() * time + h.phase();
      float r = (float) h.amplitude() * SCALE;
      shapes.circle(x, y, Math.abs(r), 40);
      float nx = x + (float) (r * Math.cos(angle));
      float ny = y + (float) (r * Math.sin(angle));
      shapes.line(x, y, nx, ny);
      x = nx;
      y = ny;
    }
    shapes.end();

    shapes.begin(ShapeType.Filled);
    // A line from the chain's tip across to where the graph begins.
    shapes.setColor(0.5f, 0.55f, 0.65f, 0.7f);
    if (!trace.isEmpty()) {
      Draw.line(shapes, x, y, TRACE_X, CY + trace.peekFirst(), 0.02f);
    }
    // The scrolling trace of the wave being drawn.
    shapes.setColor(1f, 0.75f, 0.3f, 1f);
    Float prev = null;
    int i = 0;
    for (Float value : trace) {
      float px = TRACE_X + i * TRACE_STEP;
      float py = CY + value;
      if (prev != null) {
        Draw.line(shapes, px - TRACE_STEP, CY + prev, px, py, 0.04f);
      }
      prev = value;
      i++;
    }
    // The tip of the chain.
    shapes.setColor(1f, 0.95f, 0.5f, 1f);
    shapes.circle(x, y, 0.08f, 12);
    shapes.end();
  }
}
