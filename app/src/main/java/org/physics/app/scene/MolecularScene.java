package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Random;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.force.LennardJones;
import org.physics.engine.math.Vector2;

/**
 * A little piece of matter, made of atoms that attract at a distance and repel up close through the
 * Lennard-Jones force from ch11. Left cold, the atoms settle at their preferred spacing and lock
 * into a neat hexagonal pattern: a solid crystal, gently jiggling. Heat it and the jiggling grows
 * until the pattern breaks up and the atoms slide over one another like a liquid; heat it more and
 * they scatter as a gas.
 *
 * <p>The whole point is that nothing switches between "solid", "liquid" and "gas" in the code.
 * There is only the one force and a temperature. The states of matter emerge from it. Press the up
 * arrow to heat, the down arrow to cool, and R to freeze a fresh crystal.
 */
public class MolecularScene implements Scene {

  private static final float FIXED_DT = 1f / 240f;
  private static final double EPSILON = 1.0;
  private static final double SIGMA = 0.6;
  private static final double CUTOFF = 1.5;
  private static final int COLUMNS = 12;
  private static final int ROWS = 8;

  private World world;
  private float timeBudget;

  @Override
  public String title() {
    return "Molecular dynamics: melting a crystal";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    world.addForce(new LennardJones(EPSILON, SIGMA, CUTOFF));
    world.addConstraint(new BoxBounds(0.3, 0.3, 15.7, 8.7, 1.0));

    double spacing = Math.pow(2, 1.0 / 6.0) * SIGMA; // the atoms' preferred separation
    double startX = (16 - (COLUMNS - 1) * spacing) / 2.0;
    double startY = (9 - (ROWS - 1) * spacing) / 2.0;

    Random random = new Random(2026);
    for (int col = 0; col < COLUMNS; col++) {
      for (int row = 0; row < ROWS; row++) {
        // Offset every other row so the atoms pack in the natural hexagonal arrangement.
        double x = startX + col * spacing + (row % 2) * spacing * 0.5;
        double y = startY + row * spacing * 0.87;
        // A small random push gives the crystal a little warmth to start with.
        Vector2 velocity =
            new Vector2(random.nextDouble() - 0.5, random.nextDouble() - 0.5).scale(0.6);
        world.add(new Particle(new Vector2(x, y), velocity, 1.0).radius(0.28));
      }
    }
    timeBudget = 0f;
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.UP) {
      scaleSpeeds(1.06); // heat
    } else if (keycode == Input.Keys.DOWN) {
      scaleSpeeds(0.94); // cool
    }
  }

  private void scaleSpeeds(double factor) {
    for (Particle atom : world.bodies()) {
      atom.setVelocity(atom.velocity().scale(factor));
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    for (Particle atom : world.bodies()) {
      // Colour by speed: cool blue when frozen, hot orange when the crystal melts.
      float heat = (float) Math.min(1.0, atom.velocity().length() / 2.0);
      shapes.setColor(0.3f + 0.7f * heat, 0.5f + 0.2f * heat, 1f - 0.7f * heat, 1f);
      shapes.circle(
          (float) atom.position().x(), (float) atom.position().y(), (float) atom.radius(), 16);
    }
    shapes.end();
  }
}
