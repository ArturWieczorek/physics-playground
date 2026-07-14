package org.physics.app.scene;

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

  @Override
  public String title() {
    return "Fluid: smoothed-particle hydrodynamics";
  }

  @Override
  public String controls() {
    return "drag: stir and splash";
  }

  @Override
  public List<String> readouts() {
    return List.of("particles: " + bodies.size());
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    bodies.clear();
    sph = new Sph(H, MASS, 0.0, STIFFNESS, VISCOSITY);
    bounds = new BoxBounds(0.5, 0.5, 15.5, 8.7, 0.3);

    for (int col = 0; col < COLUMNS; col++) {
      for (int row = 0; row < ROWS; row++) {
        double x = 1.2 + col * SPACING;
        double y = 1.0 + row * SPACING;
        bodies.add(new Particle(new Vector2(x, y), Vector2.ZERO, MASS).radius(0.16));
      }
    }

    // Tune the rest density to this starting block, so the fluid begins near equilibrium rather
    // than immediately exploding apart or collapsing.
    double[] density = sph.densities(bodies);
    double average = 0;
    for (double d : density) {
      average += d;
    }
    sph.setRestDensity(average / density.length * 0.9);

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
    for (Particle body : bodies) {
      // Calm water is deep blue; fast-moving splashes brighten toward white.
      float speed = (float) Math.min(1.0, body.velocity().length() / 6.0);
      shapes.setColor(0.2f + 0.6f * speed, 0.5f + 0.4f * speed, 0.95f, 1f);
      shapes.circle(
          (float) body.position().x(), (float) body.position().y(), (float) body.radius(), 12);
    }
    shapes.end();
  }
}
