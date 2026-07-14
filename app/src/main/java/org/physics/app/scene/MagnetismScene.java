package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.force.LorentzForce;
import org.physics.engine.math.Vector2;

/**
 * Charged particles in a magnetic field. The field points straight out of the screen, the same
 * everywhere. Because the magnetic force is always sideways to the motion (ch10), the charges do
 * not fly straight; they curl into circles. Positive and negative charges curl opposite ways, which
 * is exactly how physicists tell them apart in a real particle detector.
 *
 * <p>Click to launch a new charge, alternating positive (red) and negative (blue). The left and
 * right arrows make the magnetic field weaker or stronger (watch the circles grow and shrink, and
 * flip direction when the field reverses). The up and down arrows add a sideways electric field,
 * which turns the circles into drifting loops. Press R to clear them.
 */
public class MagnetismScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final int TRAIL_LENGTH = 220;
  private static final double LAUNCH_SPEED = 4.0;

  private World world;
  private final List<Trace> traces = new ArrayList<>();
  private double magneticField = 1.5;
  private Vector2 electricField = Vector2.ZERO;
  private float timeBudget;
  private double nextSign = 1.0;

  @Override
  public String title() {
    return "Magnetism: the Lorentz force";
  }

  @Override
  public String controls() {
    return "click: launch a charge   left/right: field strength   up/down: add E-field";
  }

  @Override
  public java.util.List<String> readouts() {
    return java.util.List.of(
        "magnetic field B: " + Draw.num(magneticField, 1),
        "electric field E: " + Draw.num(electricField.length(), 1));
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    traces.clear();
    magneticField = 1.5;
    electricField = Vector2.ZERO;
    nextSign = 1.0;
    rebuild();

    // Start with one of each sign so the opposite curling is visible straight away.
    launch(new Vector2(6, 4.5), new Vector2(0, LAUNCH_SPEED), 1.0);
    launch(new Vector2(10, 4.5), new Vector2(0, LAUNCH_SPEED), -1.0);
    timeBudget = 0f;
  }

  // The Lorentz force holds a fixed field, so when the field changes we build a fresh world around
  // the same charges (they keep their position and velocity, since we reuse the objects).
  private void rebuild() {
    World fresh = new World();
    for (Trace trace : traces) {
      fresh.add(trace.particle);
    }
    fresh.addForce(new LorentzForce(electricField, magneticField));
    fresh.addConstraint(new BoxBounds(0.2, 0.2, 15.8, 8.8, 1.0));
    world = fresh;
  }

  private void launch(Vector2 position, Vector2 velocity, double sign) {
    Particle charge = new Particle(position, velocity, 1.0).radius(0.14).charge(sign);
    traces.add(new Trace(charge, sign >= 0));
    rebuild();
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.1f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
    for (Trace trace : traces) {
      trace.record();
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    launch(new Vector2(worldX, worldY), new Vector2(0, LAUNCH_SPEED), nextSign);
    nextSign = -nextSign;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.RIGHT) {
      magneticField += 0.3;
      rebuild();
    } else if (keycode == Input.Keys.LEFT) {
      magneticField -= 0.3;
      rebuild();
    } else if (keycode == Input.Keys.UP) {
      electricField = electricField.add(new Vector2(1.0, 0));
      rebuild();
    } else if (keycode == Input.Keys.DOWN) {
      electricField = electricField.add(new Vector2(-1.0, 0));
      rebuild();
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    // The walls the charges bounce off.
    shapes.setColor(0.3f, 0.34f, 0.4f, 1f);
    Draw.box(shapes, 0.2, 0.2, 15.8, 8.8, 0.06f);
    // Fading trails showing each charge's circular path.
    for (Trace trace : traces) {
      List<Vector2> trail = trace.trail;
      for (int i = 1; i < trail.size(); i++) {
        float fade = i / (float) trail.size();
        if (trace.positive) {
          shapes.setColor(0.95f, 0.4f, 0.4f, fade);
        } else {
          shapes.setColor(0.4f, 0.6f, 0.95f, fade);
        }
        Vector2 p0 = trail.get(i - 1);
        Vector2 p1 = trail.get(i);
        Draw.line(shapes, p0.x(), p0.y(), p1.x(), p1.y(), 0.03f);
      }
    }
    shapes.end();

    shapes.begin(ShapeType.Filled);
    for (Trace trace : traces) {
      if (trace.positive) {
        shapes.setColor(0.95f, 0.3f, 0.3f, 1f);
      } else {
        shapes.setColor(0.3f, 0.5f, 0.95f, 1f);
      }
      Vector2 p = trace.particle.position();
      shapes.circle((float) p.x(), (float) p.y(), (float) trace.particle.radius(), 16);
    }
    shapes.end();
  }

  /** A charge, whether it is positive, and the fading trail of where it has been. */
  private static final class Trace {
    private final Particle particle;
    private final boolean positive;
    private final List<Vector2> trail = new ArrayList<>();

    private Trace(Particle particle, boolean positive) {
      this.particle = particle;
      this.positive = positive;
    }

    private void record() {
      trail.add(particle.position());
      if (trail.size() > TRAIL_LENGTH) {
        trail.remove(0);
      }
    }
  }
}
