package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.field.ElectricField;
import org.physics.engine.force.Coulomb;
import org.physics.engine.math.Vector2;

/**
 * Electric charges and the field around them. Two fixed source charges, one positive and one
 * negative, set up a field, drawn as a grid of little arrows: each arrow shows which way a positive
 * test charge would be pushed there. Scattered among them are free test charges that actually feel
 * the field and drift along it, out of the positive and into the negative.
 *
 * <p>Click anywhere to pin a new source charge. Its sign alternates each click (red for positive,
 * blue for negative), so you can build up your own arrangement and watch the arrows and the
 * drifting charges rearrange. Press R to reset.
 */
public class ChargesScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final double K = 3.0;
  private static final double SOFTENING = 0.35;
  private static final double SOURCE_CHARGE = 6.0;

  private World world;
  private final List<Particle> sources = new ArrayList<>();
  private final List<Particle> testCharges = new ArrayList<>();
  private float timeBudget;
  private double nextSign = 1.0;

  @Override
  public String title() {
    return "Charges: Coulomb's law and fields";
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    sources.clear();
    testCharges.clear();
    nextSign = 1.0;
    world.addForce(new Coulomb(K, SOFTENING));
    world.addConstraint(new BoxBounds(0.2, 0.2, 15.8, 8.8, 0.3));

    addSource(new Vector2(10.5, 4.5), SOURCE_CHARGE);
    addSource(new Vector2(5.5, 4.5), -SOURCE_CHARGE);

    Random random = new Random(99);
    for (int i = 0; i < 16; i++) {
      double x = 1 + random.nextDouble() * 14;
      double y = 1 + random.nextDouble() * 7;
      Particle test = new Particle(new Vector2(x, y), Vector2.ZERO, 1.0).radius(0.12).charge(0.4);
      testCharges.add(test);
      world.add(test);
    }
    timeBudget = 0f;
  }

  private void addSource(Vector2 position, double charge) {
    Particle source = new Particle(position, Vector2.ZERO, 1.0).radius(0.35).charge(charge).pin();
    sources.add(source);
    world.add(source);
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.1f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    addSource(new Vector2(worldX, worldY), SOURCE_CHARGE * nextSign);
    nextSign = -nextSign; // alternate the sign of the next placed charge
  }

  @Override
  public void render(ShapeRenderer shapes) {
    drawFieldArrows(shapes);

    shapes.begin(ShapeType.Filled);
    // Test charges, small and pale.
    shapes.setColor(0.95f, 0.9f, 0.5f, 1f);
    for (Particle test : testCharges) {
      shapes.circle(
          (float) test.position().x(), (float) test.position().y(), (float) test.radius(), 12);
    }
    // Source charges: red for positive, blue for negative.
    for (Particle source : sources) {
      if (source.charge() >= 0) {
        shapes.setColor(0.95f, 0.3f, 0.3f, 1f);
      } else {
        shapes.setColor(0.3f, 0.5f, 0.95f, 1f);
      }
      shapes.circle(
          (float) source.position().x(),
          (float) source.position().y(),
          (float) source.radius(),
          24);
    }
    shapes.end();
  }

  private void drawFieldArrows(ShapeRenderer shapes) {
    shapes.begin(ShapeType.Line);
    for (double x = 0.75; x < 16; x += 1.0) {
      for (double y = 0.75; y < 9; y += 1.0) {
        Vector2 point = new Vector2(x, y);
        Vector2 field = ElectricField.fieldAt(point, sources, K, SOFTENING);
        double magnitude = field.length();
        if (magnitude < 1e-6) {
          continue;
        }
        Vector2 direction = field.scale(1.0 / magnitude);
        // Brighter where the field is stronger, dimmer far away.
        float intensity = (float) Math.min(1.0, 0.25 + magnitude / 6.0);
        shapes.setColor(0.45f * intensity, 0.7f * intensity, 0.55f * intensity, 1f);
        float x2 = (float) (x + direction.x() * 0.55);
        float y2 = (float) (y + direction.y() * 0.55);
        shapes.line((float) x, (float) y, x2, y2);
      }
    }
    shapes.end();
  }
}
