package org.physics.engine.wave;

/**
 * A travelling plane wave, the shape of light. At every point along the direction it travels, a
 * field points sideways, and its strength rises and falls as a sine. The whole pattern glides
 * forward at a fixed speed without changing shape:
 *
 * <pre>
 *   field(x, t) = amplitude * sin( k (x - speed * t) )
 * </pre>
 *
 * <p>Here {@code k = 2 pi / wavelength} is the wave number (how many radians of the sine fit in a
 * unit of distance). The moving part is {@code x - speed t}: a point of fixed phase, such as a
 * crest, keeps {@code x - speed t} constant, so it moves forward at exactly {@code speed}. For
 * light that speed is the same for every wavelength, which is why colour does not change how fast
 * light travels, and it ties the wavelength and frequency together by {@code speed = frequency *
 * wavelength}.
 *
 * <p>In a real electromagnetic wave there are two such fields at once, an electric one and a
 * magnetic one, at right angles to each other and to the direction of travel, rising and falling in
 * step. This one wave describes both; the scene simply draws it twice, once for each field.
 */
public class PlaneWave {

  private final double amplitude;
  private final double wavelength;
  private final double speed;

  public PlaneWave(double amplitude, double wavelength, double speed) {
    if (wavelength <= 0) {
      throw new IllegalArgumentException("wavelength must be positive: " + wavelength);
    }
    this.amplitude = amplitude;
    this.wavelength = wavelength;
    this.speed = speed;
  }

  public double amplitude() {
    return amplitude;
  }

  public double wavelength() {
    return wavelength;
  }

  public double speed() {
    return speed;
  }

  /** The wave number, 2 pi divided by the wavelength. */
  public double waveNumber() {
    return 2 * Math.PI / wavelength;
  }

  /** How many full oscillations pass a fixed point each second: speed divided by wavelength. */
  public double frequency() {
    return speed / wavelength;
  }

  /** The field's sideways strength at position {@code x} along the wave at time {@code t}. */
  public double fieldAt(double x, double t) {
    return amplitude * Math.sin(waveNumber() * (x - speed * t));
  }
}
