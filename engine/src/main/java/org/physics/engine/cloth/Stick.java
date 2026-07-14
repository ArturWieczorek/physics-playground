package org.physics.engine.cloth;

/**
 * A thread joining two cloth points, holding them a fixed distance apart (its rest length). A whole
 * grid of these, running across and down, is what makes a sheet behave like cloth: each thread
 * quietly insists on its length, and out of all of them together comes something that folds, sways,
 * and hangs.
 *
 * <p>A thread can also snap. If it is stretched past its tear length it breaks and stops pulling,
 * which is how the cloth rips when you yank it too hard.
 */
public class Stick {

  private final ClothPoint a;
  private final ClothPoint b;
  private final double restLength;
  private final double tearLength;
  private boolean broken;

  public Stick(ClothPoint a, ClothPoint b, double restLength, double tearLength) {
    this.a = a;
    this.b = b;
    this.restLength = restLength;
    this.tearLength = tearLength;
  }

  public ClothPoint a() {
    return a;
  }

  public ClothPoint b() {
    return b;
  }

  public double restLength() {
    return restLength;
  }

  public double tearLength() {
    return tearLength;
  }

  public boolean isBroken() {
    return broken;
  }

  public void breakStick() {
    broken = true;
  }
}
