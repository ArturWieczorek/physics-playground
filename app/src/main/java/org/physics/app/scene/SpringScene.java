package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.force.Spring;
import org.physics.engine.force.UniformGravity;
import org.physics.engine.math.Vector2;

/**
 * The first thing we put on the screen: a weight hanging from a fixed point by a spring, swinging
 * and bobbing under gravity. Grab the weight with the mouse and let it go to set it swinging; press
 * R to reset.
 *
 * <p>All the physics comes from the engine we already built and tested. This class only decides
 * where things start, steps the world forward, and draws the result. That split, tested physics
 * plus thin drawing, is the pattern for every scene from here on.
 */
public class SpringScene implements Scene {

  // The visible area is 16 by 9 world units, with the origin at the bottom-left corner.
  private static final Vector2 ANCHOR_POSITION = new Vector2(8, 7.5);
  private static final Vector2 BOB_START = new Vector2(11.5, 6.5);
  private static final double BOB_MASS = 1.0;
  private static final double REST_LENGTH = 2.0;
  private static final double STIFFNESS = 60.0;
  private static final double DAMPING = 0.6;
  private static final float BOB_RADIUS = 0.35f;
  private static final float GRAB_RADIUS = 0.8f;

  // We step the physics at a fixed, small rate for stability, no matter the frame rate. Any
  // left-over time is carried into the next frame.
  private static final float FIXED_DT = 1f / 120f;

  private World world;
  private Particle anchor;
  private Particle bob;
  private float timeBudget;
  private boolean holding;

  @Override
  public String title() {
    return "Spring: Hooke's law";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    anchor = world.add(new Particle(ANCHOR_POSITION, Vector2.ZERO, 1.0).pin());
    bob = world.add(new Particle(BOB_START, Vector2.ZERO, BOB_MASS));
    world.addForce(new UniformGravity(new Vector2(0, -9.81)));
    world.addForce(new Spring(anchor, bob, REST_LENGTH, STIFFNESS, DAMPING));
    timeBudget = 0f;
    holding = false;
  }

  @Override
  public void update(float dt) {
    // Cap the catch-up so a hiccup cannot make us step forever.
    timeBudget += Math.min(dt, 0.1f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    // Grab the bob if the click landed near it. Pinning it lets us drag it without the spring
    // fighting back, exactly the pin feature from ch05.
    if (bob.position().distanceTo(new Vector2(worldX, worldY)) <= GRAB_RADIUS) {
      holding = true;
      bob.pin();
      bob.setPosition(new Vector2(worldX, worldY));
    }
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    if (holding) {
      bob.setPosition(new Vector2(worldX, worldY));
    }
  }

  @Override
  public void pointerUp() {
    if (holding) {
      holding = false;
      bob.unpin();
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    Vector2 a = anchor.position();
    Vector2 b = bob.position();

    // The spring, drawn as a simple line between the two ends.
    shapes.begin(ShapeType.Line);
    shapes.setColor(0.55f, 0.6f, 0.7f, 1f);
    shapes.line((float) a.x(), (float) a.y(), (float) b.x(), (float) b.y());
    shapes.end();

    shapes.begin(ShapeType.Filled);
    // The fixed anchor, a small pale block.
    shapes.setColor(0.8f, 0.82f, 0.9f, 1f);
    shapes.rect((float) a.x() - 0.25f, (float) a.y() - 0.1f, 0.5f, 0.2f);
    // The bob, a warm circle.
    shapes.setColor(1f, 0.6f, 0.2f, 1f);
    shapes.circle((float) b.x(), (float) b.y(), BOB_RADIUS, 32);
    shapes.end();
  }
}
