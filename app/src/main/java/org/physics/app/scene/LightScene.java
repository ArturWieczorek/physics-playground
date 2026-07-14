package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.wave.PlaneWave;

/**
 * Light, drawn as what it actually is: an electromagnetic wave. Down the middle runs the direction
 * the wave travels. At each point along it, two fields stick out sideways, an electric field
 * (upright, blue) and a magnetic field (into the depth, orange), at right angles to each other and
 * to the direction of travel. Both rise and fall together as a sine, and the whole pattern glides
 * forward. The tips of the arrows trace the familiar wavy curve of light.
 *
 * <p>Change the wavelength with the left and right arrows and the amplitude with up and down.
 * Notice the speed never changes: for light, a shorter wavelength just means a higher frequency,
 * never a different speed.
 */
public class LightScene implements Scene {

  private static final double AXIS_LEFT = 1.5;
  private static final double AXIS_RIGHT = 15.0;
  private static final double MID_Y = 4.6;
  private static final double STEP = 0.3;
  private static final double SPEED = 3.0;

  // A foreshortened direction standing in for "into the page", so the magnetic field looks
  // perpendicular to the electric field in our flat view.
  private static final double DEPTH_X = 0.62;
  private static final double DEPTH_Y = -0.36;

  private PlaneWave wave;
  private double amplitude;
  private double wavelength;
  private float time;

  @Override
  public String title() {
    return "Light: electromagnetic wave";
  }

  @Override
  public String controls() {
    return "left/right: wavelength   up/down: amplitude";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "wavelength: " + Draw.num(wavelength, 2),
        "frequency: " + Draw.num(wave.frequency(), 2),
        "speed: " + Draw.num(SPEED, 1) + " (fixed)");
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    amplitude = 2.0;
    wavelength = 3.5;
    rebuildWave();
    time = 0f;
  }

  private void rebuildWave() {
    wave = new PlaneWave(amplitude, wavelength, SPEED);
  }

  @Override
  public void update(float dt) {
    time += Math.min(dt, 0.05f); // let the wave travel
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      wavelength = Math.min(8.0, wavelength + 0.3);
    } else if (keycode == Input.Keys.LEFT) {
      wavelength = Math.max(1.0, wavelength - 0.3);
    } else if (keycode == Input.Keys.UP) {
      amplitude = Math.min(3.2, amplitude + 0.2);
    } else if (keycode == Input.Keys.DOWN) {
      amplitude = Math.max(0.4, amplitude - 0.2);
    } else {
      return;
    }
    rebuildWave();
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);

    // The axis the wave travels along.
    shapes.setColor(0.4f, 0.42f, 0.5f, 1f);
    Draw.line(shapes, AXIS_LEFT, MID_Y, AXIS_RIGHT, MID_Y, 0.03f);

    // The field arrows at each sample point: electric upright, magnetic into the depth.
    for (double x = AXIS_LEFT; x <= AXIS_RIGHT; x += STEP) {
      double value = wave.fieldAt(x, time);

      shapes.setColor(0.35f, 0.65f, 1f, 1f); // electric field, blue
      Draw.arrow(shapes, x, MID_Y, x, MID_Y + value, 0.03f, 0.14);

      shapes.setColor(1f, 0.55f, 0.3f, 0.75f); // magnetic field, orange, into the depth
      Draw.arrow(shapes, x, MID_Y, x + DEPTH_X * value, MID_Y + DEPTH_Y * value, 0.03f, 0.14);
    }

    // Smooth curves through the arrow tips.
    drawTipCurve(shapes, 1f, true);
    drawTipCurve(shapes, 0.75f, false);

    // The source, a bright dot at the start of the axis.
    shapes.setColor(1f, 0.6f, 0.35f, 1f);
    shapes.circle((float) AXIS_LEFT, (float) MID_Y, 0.22f, 20);

    shapes.end();
  }

  // Connects the tips of one field's arrows into a continuous wave curve.
  private void drawTipCurve(ShapeRenderer shapes, float alpha, boolean electric) {
    if (electric) {
      shapes.setColor(0.6f, 0.85f, 1f, alpha);
    } else {
      shapes.setColor(1f, 0.7f, 0.45f, alpha);
    }
    double prevX = 0;
    double prevY = 0;
    boolean first = true;
    for (double x = AXIS_LEFT; x <= AXIS_RIGHT; x += STEP) {
      double value = wave.fieldAt(x, time);
      double tipX = electric ? x : x + DEPTH_X * value;
      double tipY = electric ? MID_Y + value : MID_Y + DEPTH_Y * value;
      if (!first) {
        Draw.line(shapes, prevX, prevY, tipX, tipY, 0.03f);
      }
      prevX = tipX;
      prevY = tipY;
      first = false;
    }
  }
}
