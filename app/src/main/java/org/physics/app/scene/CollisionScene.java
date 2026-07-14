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
 * <p>Drag from the white cue ball to aim: a line shows the shot, and the further you pull the
 * harder it strikes. Release to break the rack. Press R to set it up again.
 */
public class CollisionScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final double BALL_RADIUS = 0.3;
  private static final double BALL_MASS = 1.0;
  private static final double MAX_SHOT_SPEED = 16.0;

  private World world;
  private final List<Particle> balls = new ArrayList<>();
  private Particle cue;
  private float timeBudget;

  private boolean aiming;
  private Vector2 aimPoint = Vector2.ZERO;

  @Override
  public String title() {
    return "Collisions: momentum and energy";
  }

  @Override
  public String controls() {
    return "drag from the cue ball to aim, release to shoot";
  }

  @Override
  public List<String> readouts() {
    double kineticEnergy = 0;
    double momentumX = 0;
    double momentumY = 0;
    for (Particle ball : balls) {
      kineticEnergy += ball.kineticEnergy();
      momentumX += ball.mass() * ball.velocity().x();
      momentumY += ball.mass() * ball.velocity().y();
    }
    double momentum = Math.hypot(momentumX, momentumY);
    // With perfectly elastic collisions and walls, both of these hold steady.
    return List.of(
        "kinetic energy: " + Draw.num(kineticEnergy, 1),
        "total momentum: " + Draw.num(momentum, 1));
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

    // The cue ball on the left, waiting for you to take the shot.
    cue = addBall(new Vector2(3.5, 4.5), Vector2.ZERO);
    timeBudget = 0f;
    aiming = false;
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
    aiming = true;
    aimPoint = new Vector2(worldX, worldY);
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    if (aiming) {
      aimPoint = new Vector2(worldX, worldY);
    }
  }

  @Override
  public void pointerUp() {
    if (!aiming) {
      return;
    }
    aiming = false;
    // Shoot the cue toward where you released; the further you dragged, the faster it goes.
    Vector2 shot = aimPoint.subtract(cue.position()).scale(2.2);
    double speed = shot.length();
    if (speed > MAX_SHOT_SPEED) {
      shot = shot.scale(MAX_SHOT_SPEED / speed);
    }
    cue.setVelocity(shot);
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    // Green felt, then the cushioned rails the balls bounce off.
    shapes.setColor(0.09f, 0.28f, 0.18f, 1f);
    shapes.rect(0.1f, 0.1f, 15.8f, 8.8f);
    shapes.setColor(0.32f, 0.2f, 0.12f, 1f);
    Draw.box(shapes, 0.1, 0.1, 15.9, 8.9, 0.2f);

    for (Particle ball : balls) {
      if (ball == cue) {
        shapes.setColor(0.97f, 0.97f, 0.99f, 1f); // cue ball, white
      } else {
        shapes.setColor(0.25f, 0.6f, 0.95f, 1f); // the rack, blue
      }
      shapes.circle(
          (float) ball.position().x(), (float) ball.position().y(), (float) ball.radius(), 28);
    }

    // The aim line while you are lining up a shot.
    if (aiming) {
      shapes.setColor(1f, 1f, 1f, 0.7f);
      Draw.line(shapes, cue.position().x(), cue.position().y(), aimPoint.x(), aimPoint.y(), 0.05f);
      shapes.circle((float) aimPoint.x(), (float) aimPoint.y(), 0.12f, 12);
    }
    shapes.end();
  }
}
