package org.physics.engine.wave;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fourier series: the surprising fact that almost any repeating shape, even a sharp-cornered
 * square wave, can be built by adding together enough plain sine waves. Each sine is a "harmonic",
 * with its own frequency (a whole-number multiple of the base) and its own size. Stack enough of
 * them and the wiggles conspire into corners.
 *
 * <p>This is also the idea behind the spinning-circle "epicycle" animations: each harmonic is a
 * rotating vector, and adding the vectors tip to tip is the same as adding the sine waves. The
 * scene draws exactly that.
 */
public class FourierSeries {

  /** One sine component: a rotating vector of a given size and whole-number frequency. */
  public record Harmonic(double amplitude, int frequency, double phase) {}

  private final List<Harmonic> harmonics;

  public FourierSeries(List<Harmonic> harmonics) {
    this.harmonics = List.copyOf(harmonics);
  }

  public List<Harmonic> harmonics() {
    return harmonics;
  }

  /** The value of the summed wave at "time" (angle) t. */
  public double valueAt(double t) {
    double sum = 0;
    for (Harmonic h : harmonics) {
      sum += h.amplitude() * Math.sin(h.frequency() * t + h.phase());
    }
    return sum;
  }

  /**
   * The Fourier series of a square wave, using {@code terms} sine waves. A square wave is the sum
   * of the odd harmonics only, each with size {@code 4 / (n * pi)}. This is the classic example
   * because the flat tops and vertical jumps are so unlike a single smooth sine, yet they emerge.
   */
  public static FourierSeries squareWave(int terms) {
    List<Harmonic> list = new ArrayList<>();
    for (int i = 0; i < terms; i++) {
      int n = 2 * i + 1; // 1, 3, 5, ...
      list.add(new Harmonic(4.0 / (n * Math.PI), n, 0));
    }
    return new FourierSeries(list);
  }

  /**
   * The Fourier series of a sawtooth wave: every harmonic, with size {@code 2 / (n * pi)} and an
   * alternating sign.
   */
  public static FourierSeries sawtooth(int terms) {
    List<Harmonic> list = new ArrayList<>();
    for (int n = 1; n <= terms; n++) {
      double sign = (n % 2 == 1) ? 1 : -1;
      list.add(new Harmonic(sign * 2.0 / (n * Math.PI), n, 0));
    }
    return new FourierSeries(list);
  }
}
