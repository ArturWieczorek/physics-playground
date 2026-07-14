package org.physics.engine.force;

import org.physics.engine.core.Particle;
import org.physics.engine.math.Vector2;

/**
 * A special-purpose integrator for a charged particle in electric and magnetic fields, the Boris
 * pusher. The ordinary semi-implicit step from ch03 slowly leaks energy for a magnetic force,
 * because that force depends on velocity, so a cyclotron circle drawn with it gently spirals. The
 * Boris method fixes this by splitting the step into three parts:
 *
 * <ol>
 *   <li>half an electric kick,
 *   <li>a pure rotation for the magnetic part (which, being a rotation, cannot change the speed),
 *   <li>the other half of the electric kick.
 * </ol>
 *
 * <p>Because the magnetic part is handled as an exact rotation, the speed is preserved to machine
 * precision and the circle stays closed forever. It is the standard choice in plasma physics for
 * exactly this reason.
 */
public final class BorisPusher {

  private BorisPusher() {}

  /** Advances one charged particle by {@code dt} in a uniform electric and magnetic field. */
  public static void step(Particle p, Vector2 electricField, double magneticField, double dt) {
    double q = p.charge();
    double m = p.mass();

    Vector2 halfElectricKick = electricField.scale(q * dt / (2 * m));
    Vector2 vMinus = p.velocity().add(halfElectricKick);

    // Rotate by the magnetic field. In the plane, B points out of the screen, so rotating a
    // velocity uses the same (vy, -vx) turn we met in the Lorentz force.
    double t = q * magneticField * dt / (2 * m);
    Vector2 vPrime = vMinus.add(new Vector2(vMinus.y() * t, -vMinus.x() * t));
    double s = 2 * t / (1 + t * t);
    Vector2 vPlus = vMinus.add(new Vector2(vPrime.y() * s, -vPrime.x() * s));

    Vector2 vNew = vPlus.add(halfElectricKick);
    p.setVelocity(vNew);
    p.setPosition(p.position().add(vNew.scale(dt)));
  }
}
