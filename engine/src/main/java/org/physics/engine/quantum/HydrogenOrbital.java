package org.physics.engine.quantum;

/**
 * The shapes of the electron orbitals of a hydrogen atom. An orbital is not an orbit; the electron
 * does not follow a path. It is a cloud of probability: the wavefunction says how likely the
 * electron is to be found at each point, and the square of the wavefunction is that likelihood. The
 * famous s, p, and d shapes are just the shapes of those clouds.
 *
 * <p>Each orbital below is the real wavefunction as a function of position (x, y, z), in atomic
 * units where the Bohr radius is 1. The overall normalising constants are left out because the
 * scene only cares about relative likelihoods when it scatters its dots, so a constant factor makes
 * no difference. What matters, and what the tests check, is the structure: where the cloud is
 * spherical, where it splits into lobes, and where the wavefunction passes through zero (a node).
 *
 * <p>The sign of the wavefunction (its phase) has no effect on the probability, but it is worth
 * seeing, so the scene colours the positive and negative lobes differently. That is why {@link
 * #value} returns a signed number rather than just the density.
 */
public enum HydrogenOrbital {

  /** The ground state: a single round ball, densest at the nucleus. */
  ONE_S("1s") {
    @Override
    public double value(double x, double y, double z) {
      return Math.exp(-radius(x, y, z));
    }
  },

  /** Two shells with a spherical node between them, where (2 - r) changes sign at r = 2. */
  TWO_S("2s") {
    @Override
    public double value(double x, double y, double z) {
      double r = radius(x, y, z);
      return (2 - r) * Math.exp(-r / 2);
    }
  },

  /** A dumbbell along the z axis: two lobes of opposite sign, with a flat node in the xy plane. */
  TWO_P_Z("2p_z") {
    @Override
    public double value(double x, double y, double z) {
      return z * Math.exp(-radius(x, y, z) / 2);
    }
  },

  /** The same dumbbell, pointing along the x axis. */
  TWO_P_X("2p_x") {
    @Override
    public double value(double x, double y, double z) {
      return x * Math.exp(-radius(x, y, z) / 2);
    }
  },

  /** The classic d shape: lobes up and down the z axis with a ring around the middle. */
  THREE_D_Z2("3d_z2") {
    @Override
    public double value(double x, double y, double z) {
      double r = radius(x, y, z);
      return (3 * z * z - r * r) * Math.exp(-r / 3);
    }
  },

  /** A four-lobed clover lying in the xy plane. */
  THREE_D_XY("3d_xy") {
    @Override
    public double value(double x, double y, double z) {
      return x * y * Math.exp(-radius(x, y, z) / 3);
    }
  };

  private final String label;

  HydrogenOrbital(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  /** The signed wavefunction at a point. Its square is the probability density there. */
  public abstract double value(double x, double y, double z);

  /** The probability density, the square of the wavefunction. */
  public double density(double x, double y, double z) {
    double v = value(x, y, z);
    return v * v;
  }

  static double radius(double x, double y, double z) {
    return Math.sqrt(x * x + y * y + z * z);
  }
}
