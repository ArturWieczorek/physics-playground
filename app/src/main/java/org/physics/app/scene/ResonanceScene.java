package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import org.physics.engine.force.DrivenOscillator;

/**
 * Resonance: why timing a push matters more than how hard you push. On the left, a mass on a spring
 * is driven with a steady rhythm. On the right is the response curve, the size of the resulting
 * swing for every driving rhythm. Sweep the driving frequency and watch the swing stay small
 * everywhere except in a narrow band around the mass's own natural frequency, where it suddenly
 * balloons. That peak is resonance.
 *
 * <p>It is one of the most important effects in engineering and everyday life: pushing a child's
 * swing in time, tuning a radio to one station, a singer shattering a glass, and the reason
 * soldiers break step crossing a bridge.
 *
 * <p>Use the left and right arrows to change the driving frequency and move the marker along the
 * curve.
 */
public class ResonanceScene implements Scene {

  private static final float STEP_DT = 0.001f;
  private static final double MASS = 1.0;
  private static final double SPRING = 20.0;
  private static final double DAMPING = 0.6;
  private static final double DRIVE = 6.0;

  private static final double FREQ_MIN = 0.3;
  private static final double FREQ_MAX = 9.0;
  private static final float ANCHOR_X = 1.8f;
  private static final float EQUILIBRIUM_X = 5.5f;
  private static final float MASS_Y = 6.7f;
  private static final float GX0 = 9.2f;
  private static final float GX1 = 15.5f;
  private static final float GY0 = 1.8f;
  private static final float GY1 = 7.6f;

  private DrivenOscillator oscillator;
  private double driveFrequency;
  private double maxAmplitude;
  private float timeBudget;

  @Override
  public String title() {
    return "Resonance: driving a mass on a spring";
  }

  @Override
  public String controls() {
    return "left/right: driving frequency";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "drive frequency: " + Draw.num(driveFrequency, 2),
        "natural frequency: " + Draw.num(oscillator.naturalFrequency(), 2),
        "swing size: " + Draw.num(oscillator.steadyAmplitude(driveFrequency), 2));
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    oscillator = new DrivenOscillator(MASS, SPRING, DAMPING, DRIVE);
    driveFrequency = 1.5;
    // The tallest point of the response curve, for scaling the graph.
    maxAmplitude = oscillator.steadyAmplitude(oscillator.naturalFrequency());
    timeBudget = 0f;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      driveFrequency = Math.min(FREQ_MAX, driveFrequency + 0.2);
    } else if (keycode == Input.Keys.LEFT) {
      driveFrequency = Math.max(FREQ_MIN, driveFrequency - 0.2);
    }
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= STEP_DT) {
      oscillator.step(driveFrequency, STEP_DT);
      timeBudget -= STEP_DT;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    float massX = EQUILIBRIUM_X + (float) oscillator.position();

    shapes.begin(ShapeType.Filled);
    // The wall, spring, and driven mass.
    shapes.setColor(0.5f, 0.52f, 0.6f, 1f);
    shapes.rect(ANCHOR_X - 0.15f, MASS_Y - 1.0f, 0.15f, 2.0f);
    Draw.line(shapes, ANCHOR_X, MASS_Y, massX, MASS_Y, 0.05f);
    shapes.setColor(0.5f, 0.55f, 0.65f, 0.5f);
    Draw.line(
        shapes, EQUILIBRIUM_X, MASS_Y - 1.1f, EQUILIBRIUM_X, MASS_Y + 1.1f, 0.02f); // rest mark
    shapes.setColor(1f, 0.6f, 0.25f, 1f);
    shapes.rect(massX - 0.35f, MASS_Y - 0.35f, 0.7f, 0.7f);

    // The response curve: swing size versus driving frequency.
    shapes.setColor(0.4f, 0.42f, 0.5f, 1f);
    Draw.line(shapes, GX0, GY0, GX1, GY0, 0.03f); // frequency axis
    Draw.line(shapes, GX0, GY0, GX0, GY1, 0.03f); // amplitude axis

    shapes.setColor(0.3f, 0.8f, 0.95f, 1f);
    float prevX = 0;
    float prevY = 0;
    boolean first = true;
    for (int i = 0; i <= 120; i++) {
      double f = FREQ_MIN + (FREQ_MAX - FREQ_MIN) * i / 120;
      float x = graphX(f);
      float y = graphY(oscillator.steadyAmplitude(f));
      if (!first) {
        Draw.line(shapes, prevX, prevY, x, y, 0.04f);
      }
      prevX = x;
      prevY = y;
      first = false;
    }

    // The marker at the current driving frequency.
    float markX = graphX(driveFrequency);
    shapes.setColor(1f, 0.85f, 0.3f, 0.9f);
    Draw.line(shapes, markX, GY0, markX, GY1, 0.03f);
    shapes.setColor(1f, 0.95f, 0.5f, 1f);
    shapes.circle(markX, graphY(oscillator.steadyAmplitude(driveFrequency)), 0.14f, 14);

    // A tick marking the natural frequency, where the peak sits.
    shapes.setColor(0.9f, 0.4f, 0.4f, 0.8f);
    float nat = graphX(oscillator.naturalFrequency());
    Draw.line(shapes, nat, GY0, nat, GY0 + 0.3f, 0.03f);
    shapes.end();
  }

  private float graphX(double frequency) {
    return GX0 + (float) ((frequency - FREQ_MIN) / (FREQ_MAX - FREQ_MIN)) * (GX1 - GX0);
  }

  private float graphY(double amplitude) {
    return GY0 + (float) Math.min(1.0, amplitude / maxAmplitude) * (GY1 - GY0);
  }
}
