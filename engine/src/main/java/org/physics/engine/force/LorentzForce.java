package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The force on a moving charge from electric and magnetic fields, the Lorentz force:
 *
 * <pre>
 *   force = q * (E + v x B)
 * </pre>
 *
 * <p>The electric part, {@code q E}, is the steady push from ch09: it acts whether the charge moves
 * or not. The magnetic part, {@code q (v x B)}, is stranger and only acts on a moving charge, and
 * it always pushes sideways, at right angles to the motion. A sideways push that never speeds the
 * charge up or slows it down can only do one thing: bend its path into a circle.
 *
 * <p>We are working in the flat plane, so the magnetic field points straight in or out of the
 * screen, described by a single number (its out-of-plane component). With that, the cross product
 * {@code v x B} for a velocity {@code (vx, vy)} works out to {@code (vy*B, -vx*B)}: exactly
 * perpendicular to the velocity, which is what makes the charge go in circles.
 */
public class LorentzForce implements Force {

  private final Vector2 electricField;
  private final double magneticField;

  /**
   * @param electricField the uniform electric field E (a push, in the plane)
   * @param magneticField the uniform magnetic field B, pointing in or out of the screen (positive
   *     is out of the screen)
   */
  public LorentzForce(Vector2 electricField, double magneticField) {
    this.electricField = electricField;
    this.magneticField = magneticField;
  }

  @Override
  public void apply(List<Particle> bodies) {
    for (Particle body : bodies) {
      double q = body.charge();
      if (q == 0) {
        continue;
      }
      Vector2 v = body.velocity();
      // v x B with B pointing out of the screen: turns (vx, vy) into (vy*B, -vx*B).
      Vector2 magnetic = new Vector2(v.y() * magneticField, -v.x() * magneticField);
      Vector2 force = electricField.add(magnetic).scale(q);
      body.addForce(force);
    }
  }
}
