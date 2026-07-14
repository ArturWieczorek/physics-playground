package org.physics.engine.quantum;

/**
 * The wavefunction of two electrons sharing two quantum states, and the home of the Pauli exclusion
 * principle. Quantum particles are truly identical, so swapping the two electrons cannot change any
 * real prediction. That leaves only two possibilities for how the wavefunction may behave under a
 * swap: it stays the same (symmetric) or it flips sign (antisymmetric). Nature assigns the first to
 * "bosons" and the second to "fermions", and electrons are fermions.
 *
 * <p>We combine two single-particle states, {@code a} and {@code b}, in the two ways allowed:
 *
 * <pre>
 *   symmetric:     ( a(x1) b(x2) + b(x1) a(x2) ) / sqrt(2)
 *   antisymmetric: ( a(x1) b(x2) - b(x1) a(x2) ) / sqrt(2)
 * </pre>
 *
 * <p>The consequence is dramatic and it is the whole point. Set the two electrons at the same
 * place, {@code x1 = x2}, in the antisymmetric case and the two terms become identical and cancel:
 * the wavefunction is exactly zero. Two electrons in the same state can never be found together.
 * That is the Pauli exclusion principle, and it is why atoms have shells, why matter is rigid, and
 * why chemistry exists. Plotted as a surface, it shows up as a trench cut clean along the diagonal.
 *
 * <p>For the two single-particle states we use the two lowest standing waves of a particle in a box
 * of half-width {@code L}: an even ground state and an odd first excited state, which vanish at the
 * walls {@code x = +/- L}.
 */
public class TwoElectronState {

  private final double halfWidth;

  public TwoElectronState(double halfWidth) {
    if (halfWidth <= 0) {
      throw new IllegalArgumentException("halfWidth must be positive: " + halfWidth);
    }
    this.halfWidth = halfWidth;
  }

  public double halfWidth() {
    return halfWidth;
  }

  /** The ground state: an even cosine that is largest in the middle and zero at the walls. */
  private double stateA(double x) {
    return Math.cos(Math.PI * x / (2 * halfWidth));
  }

  /** The first excited state: an odd sine with a node in the middle and zeros at the walls. */
  private double stateB(double x) {
    return Math.sin(Math.PI * x / halfWidth);
  }

  /**
   * The combined wavefunction at electron positions {@code x1} and {@code x2}. Pass {@code true}
   * for the antisymmetric (fermion, electron) combination, {@code false} for the symmetric (boson)
   * one.
   */
  public double value(double x1, double x2, boolean antisymmetric) {
    double term1 = stateA(x1) * stateB(x2);
    double term2 = stateB(x1) * stateA(x2);
    double sign = antisymmetric ? -1.0 : 1.0;
    return (term1 + sign * term2) / Math.sqrt(2);
  }
}
