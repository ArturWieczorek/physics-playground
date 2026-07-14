package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.core.Particle;
import org.physics.engine.fluid.Sph;
import org.physics.engine.math.Vector2;

/**
 * A pool of liquid, and the grand finale. The fluid is made of hundreds of particles governed by
 * the smoothed-particle hydrodynamics of ch13: each one measures how crowded it is, and pushes away
 * from the crush while being dragged along by its neighbours. Out of that comes something that
 * sloshes, splashes, settles flat, and forms a surface, like real water, with no notion of
 * "surface" or "wave" anywhere in the code.
 *
 * <p>Drag the mouse through the pool to stir and splash it. Press R to drop a fresh block of fluid.
 */
public class FluidScene implements Scene {

  private static final float FIXED_DT = 1f / 240f;
  private static final double H = 0.6;
  private static final double MASS = 1.0;
  private static final double STIFFNESS = 8.0;
  private static final double VISCOSITY = 3.5;
  private static final Vector2 GRAVITY = new Vector2(0, -7.0);
  private static final int COLUMNS = 24;
  private static final int ROWS = 16;
  private static final double SPACING = 0.32;
  private static final double MAX_SPEED =
      12.0; // safety cap so a rare spike cannot fling a drop away
  private static final double PUSH_RADIUS = 1.3;

  private Sph sph;
  private final List<Particle> bodies = new ArrayList<>();
  private BoxBounds bounds;
  private float timeBudget;
  private Vector2 lastPointer;
  private double viscosity;
  private double stiffness;

  @Override
  public String title() {
    return "Fluid: smoothed-particle hydrodynamics";
  }

  @Override
  public String controls() {
    return "drag: stir and splash   up/down: viscosity   left/right: stiffness";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "particles: " + bodies.size(),
        "viscosity: " + Draw.num(viscosity, 1),
        "stiffness: " + Draw.num(stiffness, 1));
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.UP) {
      viscosity = Math.min(20, viscosity + 0.5);
    } else if (keycode == Input.Keys.DOWN) {
      viscosity = Math.max(0, viscosity - 0.5);
    } else if (keycode == Input.Keys.RIGHT) {
      stiffness = Math.min(30, stiffness + 1);
    } else if (keycode == Input.Keys.LEFT) {
      stiffness = Math.max(1, stiffness - 1);
    } else {
      return;
    }
    // Rebuild the solver with the new settings, keeping the tuned rest density.
    sph = new Sph(H, MASS, sph.restDensity(), stiffness, viscosity);
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    bodies.clear();
    viscosity = VISCOSITY;
    stiffness = STIFFNESS;
    sph = new Sph(H, MASS, 0.0, stiffness, viscosity);
    bounds = new BoxBounds(0.5, 0.5, 15.5, 8.7, 0.3);

    for (int col = 0; col < COLUMNS; col++) {
      for (int row = 0; row < ROWS; row++) {
        double x = 1.2 + col * SPACING;
        double y = 1.0 + row * SPACING;
        bodies.add(new Particle(new Vector2(x, y), Vector2.ZERO, MASS).radius(0.16));
      }
    }

    // Tune the rest density to this starting block so the fluid begins at equilibrium. We use the
    // median density, not the mean: particles at the free surface have fewer neighbours and so a
    // lower density, and averaging them in would set the target too low and make the block puff
    // outward on the first frame. The median reflects the packed interior.
    double[] density = sph.densities(bodies);
    double[] sorted = density.clone();
    java.util.Arrays.sort(sorted);
    sph.setRestDensity(sorted[sorted.length / 2]);

    timeBudget = 0f;
    lastPointer = null;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      sph.step(bodies, GRAVITY, FIXED_DT);
      bounds.resolve(bodies);
      clampSpeeds();
      timeBudget -= FIXED_DT;
    }
  }

  private void clampSpeeds() {
    for (Particle body : bodies) {
      double speed = body.velocity().length();
      if (speed > MAX_SPEED) {
        body.setVelocity(body.velocity().scale(MAX_SPEED / speed));
      }
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    lastPointer = new Vector2(worldX, worldY);
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    Vector2 here = new Vector2(worldX, worldY);
    if (lastPointer != null) {
      Vector2 push =
          here.subtract(lastPointer).scale(18.0); // shove the fluid the way the mouse moved
      for (Particle body : bodies) {
        if (body.position().distanceTo(here) < PUSH_RADIUS) {
          body.setVelocity(body.velocity().add(push));
        }
      }
    }
    lastPointer = here;
  }

  @Override
  public void pointerUp() {
    lastPointer = null;
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    shapes.setColor(0.3f, 0.34f, 0.4f, 1f);
    Draw.box(shapes, 0.5, 0.5, 15.5, 8.7, 0.06f);
    // Draw each particle as a large translucent blob, roughly a smoothing-radius wide. Where
    // particles crowd together the blobs overlap and merge into a continuous body of water, with a
    // soft surface, instead of a stipple of separate dots.
    float blob = (float) (H * 0.9);
    for (Particle body : bodies) {
      // Calm water is deep blue; fast-moving splashes brighten toward white.
      float speed = (float) Math.min(1.0, body.velocity().length() / 6.0);
      shapes.setColor(0.2f + 0.6f * speed, 0.5f + 0.4f * speed, 0.95f, 0.5f);
      shapes.circle((float) body.position().x(), (float) body.position().y(), blob, 16);
    }
    shapes.end();
  }
}
