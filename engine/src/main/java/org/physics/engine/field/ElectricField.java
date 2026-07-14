package org.physics.engine.field;

import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * The invisible field around a set of charges. A charge does not reach out and touch another
 * directly; instead it fills the space around it with a field, and any other charge feels the field
 * where it sits. This class lets us look at that field itself, which is what the scene draws as a
 * grid of arrows.
 *
 * <p>Two quantities describe the field at a point:
 *
 * <ul>
 *   <li>The <b>field vector</b>, which way and how strongly a tiny positive test charge would be
 *       pushed there. It points away from positive charges and toward negative ones.
 *   <li>The <b>potential</b>, a single number, the electrical "height" of that point. Charges roll
 *       downhill in potential just as balls roll downhill in gravity. Places of equal potential
 *       joined up form the equipotential lines.
 * </ul>
 */
public final class ElectricField {

  private ElectricField() {}

  /** The electric field vector at {@code point} produced by all the given charges. */
  public static Vector2 fieldAt(
      Vector2 point, List<Particle> charges, double coulombConstant, double softening) {
    Vector2 field = Vector2.ZERO;
    for (Particle charge : charges) {
      Vector2 delta = point.subtract(charge.position()); // from the charge out to the point
      double distanceSquared = delta.lengthSquared() + softening * softening;
      double distance = Math.sqrt(distanceSquared);
      double magnitude = coulombConstant * charge.charge() / distanceSquared;
      field = field.add(delta.scale(magnitude / distance));
    }
    return field;
  }

  /**
   * The electric potential (a single number) at {@code point} produced by all the given charges.
   */
  public static double potentialAt(
      Vector2 point, List<Particle> charges, double coulombConstant, double softening) {
    double potential = 0;
    for (Particle charge : charges) {
      double distance =
          Math.sqrt(point.subtract(charge.position()).lengthSquared() + softening * softening);
      potential += coulombConstant * charge.charge() / distance;
    }
    return potential;
  }
}
