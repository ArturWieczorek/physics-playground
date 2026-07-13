package org.physics.engine.collide;

import java.util.List;
import org.physics.engine.core.Particle;

/**
 * Something that tidies up the particles' positions and velocities after they have moved: keeping
 * them inside the box, stopping them overlapping, holding two of them a fixed distance apart. A
 * force nudges things over time; a constraint fixes things right now, once they have moved.
 *
 * <p>The world runs its forces and integrates first, then asks each constraint to resolve. Walls
 * and particle-to-particle bouncing (ch06) are constraints, and so is the thread that holds a piece
 * of cloth together (ch12).
 */
public interface Constraint {

  void resolve(List<Particle> bodies);
}
