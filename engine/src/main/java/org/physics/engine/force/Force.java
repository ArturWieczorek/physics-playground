package org.physics.engine.force;

import java.util.List;
import org.physics.engine.core.Particle;

/**
 * Something that pushes on the particles in a world: gravity, a spring, the pull between two
 * charges, and so on. On each step the world asks every force to add its contribution to the
 * particles' force totals, before any of them move.
 *
 * <p>A force is handed the whole list of bodies, not just one, because some forces depend on more
 * than a single particle. Gravity between two planets, for example, needs both of them at once. The
 * simplest forces just loop over the list and push on each in turn.
 */
public interface Force {

  void apply(List<Particle> bodies);
}
