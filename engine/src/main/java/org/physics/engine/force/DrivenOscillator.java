package org.physics.engine.force;

/**
 * A mass on a spring that is pushed rhythmically, the setting for resonance. On its own the mass
 * has a natural frequency it likes to swing at, {@code sqrt(k/m)}. Push it at some other rhythm and
 * it responds weakly; but push it at, or near, its natural frequency and the pushes keep arriving
 * in step with the motion, adding energy every cycle, and the swing grows enormous. That is
 * resonance, and it is why a pushed swing goes higher only when you time the pushes right, why a
 * wine glass shatters at the right pitch, and why bridges must avoid marching feet.
 *
 * <p>The mass obeys {@code m x'' = -k x - c x' + F cos(drive * t)}: a spring pull, a damping drag,
 * and the rhythmic driving push. The steady swing size settles to
 *
 * <pre>
 *   amplitude(drive) = F / sqrt( (k - m drive^2)^2 + (c drive)^2 )
 * </pre>
 *
 * <p>which is small far from the natural frequency and peaks sharply near it, more sharply the less
 * damping there is.
 */
public class DrivenOscillator {

  private final double mass;
  private final double springConstant;
  private final double damping;
  private final double driveAmplitude;

  private double position;
  private double velocity;
  private double time;

  public DrivenOscillator(
      double mass, double springConstant, double damping, double driveAmplitude) {
    this.mass = mass;
    this.springConstant = springConstant;
    this.damping = damping;
    this.driveAmplitude = driveAmplitude;
  }

  /** The frequency the mass swings at on its own, with no pushing: sqrt(k / m). */
  public double naturalFrequency() {
    return Math.sqrt(springConstant / mass);
  }

  /**
   * The steady swing size when driven at {@code driveFrequency}. Peaks near the natural frequency.
   */
  public double steadyAmplitude(double driveFrequency) {
    double stiffnessTerm = springConstant - mass * driveFrequency * driveFrequency;
    double dampingTerm = damping * driveFrequency;
    return driveAmplitude / Math.sqrt(stiffnessTerm * stiffnessTerm + dampingTerm * dampingTerm);
  }

  /** Advances the mass by {@code dt}, driven at {@code driveFrequency}. */
  public void step(double driveFrequency, double dt) {
    double force =
        -springConstant * position
            - damping * velocity
            + driveAmplitude * Math.cos(driveFrequency * time);
    double acceleration = force / mass;
    velocity += acceleration * dt;
    position += velocity * dt;
    time += dt;
  }

  public double position() {
    return position;
  }
}
