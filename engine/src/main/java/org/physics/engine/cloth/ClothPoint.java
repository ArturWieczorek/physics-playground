package org.physics.engine.cloth;

import org.physics.engine.math.Vector2;

/**
 * One node of the cloth. Cloth uses a different style of motion from the rest of the engine, called
 * Verlet integration, and this point is built for it. Instead of storing a velocity, it remembers
 * where it was on the previous step. The difference between where it is now and where it was then
 * is its velocity, worked out on the fly.
 *
 * <p>This sounds like a quirk, but it is the whole trick behind cloth. Because velocity is implied
 * by the two positions, we can move a point directly (to satisfy the threads pulling on it) and its
 * velocity updates itself automatically. That is what lets thousands of little length constraints
 * cooperate into something that behaves like fabric.
 */
public class ClothPoint {

  private Vector2 position;
  private Vector2 previous;
  private boolean pinned;

  public ClothPoint(Vector2 position) {
    this.position = position;
    this.previous = position;
  }

  public Vector2 position() {
    return position;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public Vector2 previous() {
    return previous;
  }

  public void setPrevious(Vector2 previous) {
    this.previous = previous;
  }

  public boolean isPinned() {
    return pinned;
  }

  public void pin() {
    pinned = true;
  }

  public void unpin() {
    pinned = false;
  }

  /**
   * How much this point yields when a thread pulls on it: 1 for a free point, 0 for a pinned one.
   * Writing the constraint solver in terms of this weight lets a pinned point act as an immovable
   * anchor without any special-case code.
   */
  public double weight() {
    return pinned ? 0.0 : 1.0;
  }
}
