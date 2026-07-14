package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.force.Spring;
import org.physics.engine.math.Vector2;

/**
 * A row of masses joined by springs, with both ends fixed: a string made of beads. It is the
 * simplest system with more than one moving part connected by springs, and it shows how a handful
 * of coupled oscillators behaves like a vibrating string. Left in a smooth curve it rocks back and
 * forth as a standing wave; pluck it in the middle and the disturbance splits and races to the
 * ends, reflects, and comes back.
 *
 * <p>Every spring here is the same Hooke's-law spring from ch05, tested there; this scene just
 * chains many of them together. The springs are stretched taut so the string has tension, and the
 * masses are held to move only up and down, which is what makes clean transverse waves.
 *
 * <p>Drag any bead to pluck the string. Space cycles the starting shape through the string's normal
 * modes (1 hump, 2 humps, 3 humps, ...). Up and down change the tension, which changes how fast the
 * waves travel.
 */
public class CoupledSpringsScene implements Scene {

  private static final int COUNT = 18;
  private static final float FIXED_DT = 1f / 240f;
  private static final double MID_Y = 4.5;
  private static final double LEFT_X = 1.5;
  private static final double RIGHT_X = 14.5;
  private static final double AMPLITUDE = 1.3;

  private World world;
  private final List<Particle> beads = new ArrayList<>();
  private final double[] equilibriumX = new double[COUNT];
  private double stiffness;
  private int mode;
  private float timeBudget;

  private Particle grabbed;

  @Override
  public String title() {
    return "Coupled springs: a vibrating string";
  }

  @Override
  public String controls() {
    return "drag: pluck   space: next mode   up/down: tension";
  }

  @Override
  public List<String> readouts() {
    return List.of("beads: " + COUNT, "mode: " + mode, "stiffness: " + Draw.num(stiffness, 0));
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    stiffness = 90;
    mode = 1;
    build();
  }

  // Builds the chain and sets its shape to the current normal mode.
  private void build() {
    world = new World();
    beads.clear();
    grabbed = null;
    double spacing = (RIGHT_X - LEFT_X) / (COUNT - 1);
    for (int i = 0; i < COUNT; i++) {
      equilibriumX[i] = LEFT_X + i * spacing;
      double shape = AMPLITUDE * Math.sin(mode * Math.PI * i / (COUNT - 1));
      Particle bead = new Particle(new Vector2(equilibriumX[i], MID_Y + shape), Vector2.ZERO, 1.0);
      if (i == 0 || i == COUNT - 1) {
        bead.pin(); // the fixed ends of the string
      }
      beads.add(world.add(bead));
    }
    // Pretensioned springs (rest length shorter than the gap) give the string its tension.
    for (int i = 0; i < COUNT - 1; i++) {
      world.addForce(new Spring(beads.get(i), beads.get(i + 1), spacing * 0.5, stiffness, 0.05));
    }
    timeBudget = 0f;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.SPACE) {
      mode = mode % 5 + 1; // 1..5
      build();
    } else if (keycode == Input.Keys.UP) {
      stiffness = Math.min(300, stiffness + 20);
      build();
    } else if (keycode == Input.Keys.DOWN) {
      stiffness = Math.max(20, stiffness - 20);
      build();
    }
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.05f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      // Hold every bead on its own vertical line, so the string moves only up and down.
      for (int i = 0; i < COUNT; i++) {
        Particle bead = beads.get(i);
        bead.setPosition(new Vector2(equilibriumX[i], bead.position().y()));
        bead.setVelocity(new Vector2(0, bead.velocity().y()));
      }
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    Particle nearest = null;
    double best = 0.9;
    for (Particle bead : beads) {
      if (bead.isPinned()) {
        continue;
      }
      double d = bead.position().distanceTo(new Vector2(worldX, worldY));
      if (d < best) {
        best = d;
        nearest = bead;
      }
    }
    if (nearest != null) {
      grabbed = nearest;
      grabbed.pin();
      grabbed.setPosition(new Vector2(grabbed.position().x(), worldY));
    }
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    if (grabbed != null) {
      grabbed.setPosition(new Vector2(grabbed.position().x(), worldY));
    }
  }

  @Override
  public void pointerUp() {
    if (grabbed != null) {
      grabbed.unpin();
      grabbed = null;
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Filled);
    // The springs between beads.
    shapes.setColor(0.5f, 0.55f, 0.65f, 1f);
    for (int i = 0; i < COUNT - 1; i++) {
      Vector2 a = beads.get(i).position();
      Vector2 b = beads.get(i + 1).position();
      Draw.line(shapes, a.x(), a.y(), b.x(), b.y(), 0.03f);
    }
    // The beads, coloured across the string.
    for (int i = 0; i < COUNT; i++) {
      float t = i / (float) (COUNT - 1);
      shapes.setColor(0.4f + 0.55f * t, 0.6f, 1f - 0.55f * t, 1f);
      Vector2 p = beads.get(i).position();
      shapes.circle((float) p.x(), (float) p.y(), 0.18f, 16);
    }
    shapes.end();
  }
}
