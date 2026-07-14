package org.physics.engine.wave;

import java.util.List;
import org.physics.engine.math.Vector2;

/**
 * Interference: what happens when waves from more than one source overlap. Where two crests meet
 * they reinforce into a bigger crest (constructive interference); where a crest meets a trough they
 * cancel (destructive interference). The result is a fixed pattern of bright and dark bands. This
 * is the heart of the double-slit experiment: light passing through two narrow slits behaves like
 * two sources, and the overlapping waves paint a striped pattern on a screen.
 *
 * <p>Each source sends out a circular wave. At a point a distance {@code r} from a source the wave
 * looks like {@code sin(k r - omega t)}, where {@code k = 2 pi / wavelength} sets how tightly
 * packed the ripples are. To combine sources we simply add their waves, because waves superpose.
 */
public final class Interference {

  private Interference() {}

  /** The combined wave height at a point and time, adding a circular wave from each source. */
  public static double amplitudeAt(
      List<Vector2> sources, double wavelength, double speed, double x, double y, double t) {
    double k = 2 * Math.PI / wavelength;
    double omega = speed * k;
    double sum = 0;
    for (Vector2 source : sources) {
      double dx = x - source.x();
      double dy = y - source.y();
      double r = Math.sqrt(dx * dx + dy * dy);
      sum += Math.sin(k * r - omega * t);
    }
    return sum;
  }

  /**
   * The brightness (intensity) at a point: how strong the wave is there. This is what a screen or a
   * camera records, and it does not depend on time. We add up the waves as rotating phasors and
   * take the squared length of the total, {@code |sum of e^(i k r)|^2}, which is the standard way
   * to combine coherent waves. (It is proportional to the squared peak amplitude; the constant does
   * not matter, since only the pattern of bright and dark is meaningful here.)
   */
  public static double intensityAt(List<Vector2> sources, double wavelength, double x, double y) {
    double k = 2 * Math.PI / wavelength;
    double real = 0;
    double imaginary = 0;
    for (Vector2 source : sources) {
      double dx = x - source.x();
      double dy = y - source.y();
      double r = Math.sqrt(dx * dx + dy * dy);
      real += Math.cos(k * r);
      imaginary += Math.sin(k * r);
    }
    return real * real + imaginary * imaginary;
  }
}
