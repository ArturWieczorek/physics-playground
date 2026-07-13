package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.collide.ParticleCollisions;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.math.Vector2;

/**
 * A break shot, like the opening of a game of pool. A single ball is fired into a tidy cluster of
 * resting balls, and the collisions scatter them. There is no gravity here, so once the balls are
 * moving they keep going, bouncing off the walls, and you can watch momentum pass from ball to
 * ball.
 *
 * <p>Click anywhere to fire a fresh ball from the left toward that point; press R to rack them up
 * again.
 */
public class CollisionScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final double BALL_RADIUS = 0.3;
  private static final double BALL_MASS = 1.0;

  private World world;
  private final List<Particle> balls = new ArrayList<>();
  private Particle cue;
  private float timeBudget;

  @Override
  public String title() {
    return "Collisions: momentum and energy";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    balls.clear();
    world.addConstraint(new ParticleCollisions(1.0)); // perfectly elastic
    world.addConstraint(new BoxBounds(0, 0, 16, 9, 1.0));

    // A triangular rack on the right, all at rest.
    double startX = 10.5;
    double centerY = 4.5;
    double spacing = BALL_RADIUS * 2.05;
    for (int row = 0; row < 4; row++) {
      for (int i = 0; i <= row; i++) {
        double x = startX + row * spacing * 0.95;
        double y = centerY + (i - row / 2.0) * spacing;
        balls.add(addBall(new Vector2(x, y), Vector2.ZERO));
      }
    }

    // The cue ball on the left, already moving toward the rack.
    cue = addBall(new Vector2(2.5, 4.5), new Vector2(9, 0));
    timeBudget = 0f;
  }

  private Particle addBall(Vector2 position, Vector2 velocity) {
    Particle ball = new Particle(position, velocity, BALL_MASS).radius(BALL_RADIUS);
    world.add(ball);
    return ball;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.1f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    // Fire the cue ball from the left edge toward where you clicked.
    Vector2 start = new Vector2(1.0, worldY);
    Vector2 aim = new Vector2(worldX, worldY).subtract(start);
    if (aim.lengthSquared() > 0) {
      cue.setPosition(start);
      cue.setVelocity(aim.normalized().scale(9));
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    for (Particle ball : balls) {
      if (ball == cue) {
        shapes.setColor(0.95f, 0.95f, 0.98f, 1f); // cue ball, white
      } else {
        shapes.setColor(0.25f, 0.6f, 0.95f, 1f); // the rack, blue
      }
      shapes.circle(
          (float) ball.position().x(), (float) ball.position().y(), (float) ball.radius(), 28);
    }
    shapes.end();
  }
}
