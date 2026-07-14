package org.physics.engine.track;

import org.physics.engine.math.Vector2;

/**
 * A bead threaded on a {@link Track}, sliding without friction under gravity. It is described by
 * two numbers: how far along the wire it is, and how fast it is going along the wire. Gravity
 * speeds it up on the downhill parts and slows it on the uphill parts.
 *
 * <p>The only force that matters is the part of gravity pointing along the wire. Sideways, the wire
 * simply holds the bead, so we never have to model that push; we just project gravity onto the
 * tangent. That is why a bead problem, which sounds hard, becomes a single line of arithmetic.
 */
public class Bead {

  private double distance; // how far along the wire
  private double speed; // speed along the wire
  private boolean finished;

  public Bead(double startDistance, double startSpeed) {
    this.distance = startDistance;
    this.speed = startSpeed;
  }

  /**
   * Advances the bead by {@code dt} on {@code track} under a downward gravity of size {@code
   * gravity}. Stops it once it reaches the end of the wire.
   */
  public void step(Track track, double gravity, double dt) {
    if (finished) {
      return;
    }
    Vector2 tangent = track.tangentAt(distance);
    // Gravity is (0, -gravity); its component along the wire is the dot product with the tangent.
    double along = -gravity * tangent.y();
    speed += along * dt;
    distance += speed * dt;
    if (distance >= track.length()) {
      distance = track.length();
      finished = true;
    } else if (distance < 0) {
      distance = 0;
      speed = 0;
    }
  }

  public double distance() {
    return distance;
  }

  public double speed() {
    return speed;
  }

  public boolean isFinished() {
    return finished;
  }

  public Vector2 positionOn(Track track) {
    return track.positionAt(distance);
  }
}
