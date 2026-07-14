package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.math.Vector2;
import org.physics.engine.wave.Interference;

/**
 * The double-slit experiment, the most famous demonstration that light (and matter) travels as
 * waves. A wave comes in from the left and passes through one or two narrow slits in a barrier.
 * Each open slit acts as a fresh source of circular waves; where two sets of waves overlap they add
 * up in some places and cancel in others, painting a striped pattern of bright and dark bands on
 * the screen at the right. With a single slit there are no stripes; open the second and the stripes
 * appear, which is the whole surprise: light plus light can make darkness.
 *
 * <p>The coloured ripples on the left are the live wave (red crests, blue troughs). The bright bars
 * on the right are the pattern the screen actually records, the time-averaged brightness.
 *
 * <p>Space toggles one or two slits. Up and down change the wavelength; left and right change the
 * gap between the slits, both of which stretch or squeeze the stripes.
 */
public class InterferenceScene implements Scene {

  private static final double BARRIER_X = 2.5;
  private static final double FIELD_RIGHT = 11.5;
  private static final double SCREEN_X = 11.8;
  private static final double PROFILE_WIDTH = 3.6;
  private static final double MID_Y = 4.5;
  private static final double TOP = 8.7;
  private static final double BOTTOM = 0.3;
  private static final double CELL = 0.22; // coarse enough to stay smooth on the web build
  private static final double SPEED = 3.0;

  private boolean twoSlits = true;
  private double wavelength = 0.7;
  private double gap = 1.8;
  private float time;

  @Override
  public String title() {
    return "Double-slit interference";
  }

  @Override
  public String controls() {
    return "space: one/two slits   up/down: wavelength   left/right: slit gap";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "slits: " + (twoSlits ? "2" : "1"),
        "wavelength: " + Draw.num(wavelength, 2),
        "slit gap: " + Draw.num(gap, 1));
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    twoSlits = true;
    wavelength = 0.7;
    gap = 1.8;
    time = 0f;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.SPACE) {
      twoSlits = !twoSlits;
    } else if (keycode == Input.Keys.UP) {
      wavelength = Math.min(1.6, wavelength + 0.1);
    } else if (keycode == Input.Keys.DOWN) {
      wavelength = Math.max(0.3, wavelength - 0.1);
    } else if (keycode == Input.Keys.RIGHT) {
      gap = Math.min(4.0, gap + 0.2);
    } else if (keycode == Input.Keys.LEFT) {
      gap = Math.max(0.6, gap - 0.2);
    }
  }

  private List<Vector2> slits() {
    List<Vector2> sources = new ArrayList<>();
    if (twoSlits) {
      sources.add(new Vector2(BARRIER_X, MID_Y - gap / 2));
      sources.add(new Vector2(BARRIER_X, MID_Y + gap / 2));
    } else {
      sources.add(new Vector2(BARRIER_X, MID_Y));
    }
    return sources;
  }

  @Override
  public void update(float dt) {
    time += Math.min(dt, 0.05f);
  }

  @Override
  public void render(ShapeRenderer shapes) {
    List<Vector2> sources = slits();
    double maxAmp = sources.size();
    double maxIntensity = sources.size() * sources.size();

    shapes.begin(ShapeType.Filled);

    // The live wave field, red for crests and blue for troughs.
    for (double x = BARRIER_X; x <= FIELD_RIGHT; x += CELL) {
      for (double y = BOTTOM; y <= TOP; y += CELL) {
        double n = Interference.amplitudeAt(sources, wavelength, SPEED, x, y, time) / maxAmp;
        shapes.setColor((float) Math.max(0, n), 0.05f, (float) Math.max(0, -n), 1f);
        shapes.rect((float) x, (float) y, (float) CELL, (float) CELL);
      }
    }

    // The barrier: a wall with the slit openings cut out of it.
    shapes.setColor(0.35f, 0.37f, 0.42f, 1f);
    shapes.rect((float) BARRIER_X - 0.07f, (float) BOTTOM, 0.14f, (float) (TOP - BOTTOM));
    shapes.setColor(0.06f, 0.07f, 0.10f, 1f); // background, to open the slits
    for (Vector2 s : sources) {
      shapes.rect((float) BARRIER_X - 0.09f, (float) (s.y() - 0.22), 0.18f, 0.44f);
    }

    // The screen, and the brightness it records as bright bars reaching right.
    shapes.setColor(0.25f, 0.27f, 0.32f, 1f);
    shapes.rect((float) SCREEN_X, (float) BOTTOM, 0.06f, (float) (TOP - BOTTOM));
    for (double y = BOTTOM; y <= TOP; y += CELL) {
      double intensity = Interference.intensityAt(sources, wavelength, SCREEN_X, y) / maxIntensity;
      float b = (float) Math.min(1.0, intensity);
      shapes.setColor(b, b * 0.95f, b * 0.7f, 1f); // warm white
      shapes.rect(
          (float) (SCREEN_X + 0.08), (float) y, (float) (intensity * PROFILE_WIDTH), (float) CELL);
    }
    shapes.end();

    // The slit sources marked as small dots.
    shapes.begin(ShapeType.Filled);
    shapes.setColor(1f, 0.9f, 0.4f, 1f);
    for (Vector2 s : sources) {
      shapes.circle((float) s.x(), (float) s.y(), 0.08f, 10);
    }
    shapes.end();
  }
}
