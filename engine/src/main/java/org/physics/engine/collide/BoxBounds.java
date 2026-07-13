package org.physics.engine.collide;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A rectangular box the particles are trapped inside. When one reaches a wall, we put it back
 * against the wall and flip the velocity component that pointed into it, so it bounces.
 *
 * <p>The bounce keeps a fraction of the incoming speed, called the restitution. A restitution of 1
 * is a perfect bounce that loses no speed; 0 means the particle just stops against the wall;
 * in-between values are the everyday case where a ball comes back a little slower each time.
 */
public class BoxBounds implements Constraint {

  private final double left;
  private final double bottom;
  private final double right;
  private final double top;
  private final double restitution;

  public BoxBounds(double left, double bottom, double right, double top, double restitution) {
    this.left = left;
    this.bottom = bottom;
    this.right = right;
    this.top = top;
    this.restitution = restitution;
  }

  @Override
  public void resolve(List<Particle> bodies) {
    for (Particle body : bodies) {
      if (body.isPinned()) {
        continue;
      }
      double r = body.radius();
      double x = body.position().x();
      double y = body.position().y();
      double vx = body.velocity().x();
      double vy = body.velocity().y();

      if (x - r < left) {
        x = left + r;
        if (vx < 0) {
          vx = -vx * restitution;
        }
      } else if (x + r > right) {
        x = right - r;
        if (vx > 0) {
          vx = -vx * restitution;
        }
      }

      if (y - r < bottom) {
        y = bottom + r;
        if (vy < 0) {
          vy = -vy * restitution;
        }
      } else if (y + r > top) {
        y = top - r;
        if (vy > 0) {
          vy = -vy * restitution;
        }
      }

      body.setPosition(new Vector2(x, y));
      body.setVelocity(new Vector2(vx, vy));
    }
  }
}
