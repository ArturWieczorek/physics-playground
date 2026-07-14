package org.physics.app.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.List;
import java.util.Random;
import org.physics.engine.collide.BoxBounds;
import org.physics.engine.collide.ParticleCollisions;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.gas.MaxwellBoltzmann;
import org.physics.engine.math.Vector2;

/**
 * A box full of gas. A few hundred tiny particles bounce around and off each other, all elastic, so
 * no energy is lost. Nothing tells them how fast to go, yet the spread of their speeds settles into
 * the Maxwell-Boltzmann curve, drawn live along the bottom. The smooth curve is the theory for the
 * current temperature; the bars are the actual particles counted up right now.
 *
 * <p>Press the up and down arrows to heat and cool the gas (speed the particles up or slow them
 * down) and watch both the colours and the curve shift. Press R to shuffle a fresh gas.
 */
public class GasScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final int PARTICLE_COUNT = 260;
  private static final double MASS = 1.0;
  private static final double RADIUS = 0.09;

  // The gas lives in the upper box; the lower strip is reserved for the histogram.
  private static final double BOX_LEFT = 0.3;
  private static final double BOX_RIGHT = 15.7;
  private static final double BOX_BOTTOM = 3.0;
  private static final double BOX_TOP = 8.7;

  private static final float HIST_BASE = 0.4f;
  private static final float HIST_HEIGHT = 2.2f;
  private static final int BINS = 32;

  private World world;
  private float timeBudget;
  private double pistonX;

  @Override
  public String title() {
    return "Kinetic gas: Maxwell-Boltzmann";
  }

  @Override
  public String controls() {
    return "up/down: heat/cool   drag: move the piston";
  }

  @Override
  public List<String> readouts() {
    double temperature = MaxwellBoltzmann.temperature(world.bodies());
    double volume = (pistonX - BOX_LEFT) * (BOX_TOP - BOX_BOTTOM);
    // Ideal-gas pressure P = N k T / V (k = 1). Shrink the volume with the piston and watch it
    // rise: that is Boyle's law.
    double pressure = PARTICLE_COUNT * temperature / volume;
    return List.of(
        "temperature: " + Draw.num(temperature, 2),
        "volume: " + Draw.num(volume, 1),
        "pressure (ideal gas): " + Draw.num(pressure, 1));
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    pistonX = BOX_RIGHT;
    rebuildConstraints();

    Random random = new Random(20260714L);
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      double x = BOX_LEFT + random.nextDouble() * (BOX_RIGHT - BOX_LEFT);
      double y = BOX_BOTTOM + random.nextDouble() * (BOX_TOP - BOX_BOTTOM);
      // Start everyone at the same speed in a random direction. The collisions alone will spread
      // that single speed out into the full distribution, which is the point worth watching.
      double angle = random.nextDouble() * Math.PI * 2;
      Vector2 velocity = new Vector2(Math.cos(angle), Math.sin(angle)).scale(3.0);
      world.add(new Particle(new Vector2(x, y), velocity, MASS).radius(RADIUS));
    }
    timeBudget = 0f;
  }

  // Rebuilds the walls so the right wall (the piston) sits at its current position.
  private void rebuildConstraints() {
    world.clearConstraints();
    world.addConstraint(new ParticleCollisions(1.0));
    world.addConstraint(new BoxBounds(BOX_LEFT, BOX_BOTTOM, pistonX, BOX_TOP, 1.0));
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    movePiston(worldX);
  }

  @Override
  public void pointerDrag(float worldX, float worldY) {
    movePiston(worldX);
  }

  private void movePiston(double worldX) {
    pistonX = Math.max(7.0, Math.min(BOX_RIGHT, worldX));
    rebuildConstraints();
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
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.UP) {
      scaleSpeeds(1.08); // heat
    } else if (keycode == Input.Keys.DOWN) {
      scaleSpeeds(0.92); // cool
    }
  }

  private void scaleSpeeds(double factor) {
    for (Particle body : world.bodies()) {
      body.setVelocity(body.velocity().scale(factor));
    }
  }

  @Override
  public void render(ShapeRenderer shapes) {
    List<Particle> bodies = world.bodies();
    double temperature = MaxwellBoltzmann.temperature(bodies);
    double meanSpeed = MaxwellBoltzmann.meanSpeed(temperature, MASS);
    double maxSpeed = Math.max(meanSpeed * 3.0, 0.001);

    // Bin the actual speeds into a histogram, expressed as a probability density so it can be
    // compared directly with the theoretical curve.
    double binWidth = maxSpeed / BINS;
    double[] density = new double[BINS];
    for (Particle body : bodies) {
      int bin = (int) (body.velocity().length() / binWidth);
      if (bin >= 0 && bin < BINS) {
        density[bin] += 1.0 / (bodies.size() * binWidth);
      }
    }

    // Pick a vertical scale so the tallest of (bars, theory peak) fills the strip.
    double peak = 1e-6;
    for (int b = 0; b < BINS; b++) {
      double vc = (b + 0.5) * binWidth;
      peak = Math.max(peak, density[b]);
      peak = Math.max(peak, MaxwellBoltzmann.speedProbabilityDensity(vc, temperature, MASS));
    }
    double yScale = HIST_HEIGHT / peak;

    shapes.begin(ShapeType.Filled);
    shapes.setColor(0.3f, 0.34f, 0.4f, 1f);
    Draw.box(shapes, BOX_LEFT, BOX_BOTTOM, pistonX, BOX_TOP, 0.06f);
    // The piston: a solid bar on the right wall you can drag to change the volume.
    shapes.setColor(0.8f, 0.6f, 0.3f, 1f);
    shapes.rect((float) pistonX - 0.12f, (float) BOX_BOTTOM, 0.24f, (float) (BOX_TOP - BOX_BOTTOM));
    shapes.end();

    drawParticles(shapes, bodies, maxSpeed);
    drawHistogram(shapes, density, yScale);
    drawTheoryCurve(shapes, temperature, binWidth, yScale);
  }

  private void drawParticles(ShapeRenderer shapes, List<Particle> bodies, double maxSpeed) {
    shapes.begin(ShapeType.Filled);
    for (Particle body : bodies) {
      // Colour from cool blue (slow) to hot red (fast).
      float t = (float) Math.min(1.0, body.velocity().length() / maxSpeed);
      shapes.setColor(0.2f + 0.8f * t, 0.4f, 1f - 0.8f * t, 1f);
      shapes.circle(
          (float) body.position().x(), (float) body.position().y(), (float) body.radius(), 12);
    }
    shapes.end();
  }

  private void drawHistogram(ShapeRenderer shapes, double[] density, double yScale) {
    double histLeft = BOX_LEFT;
    double histSpan = BOX_RIGHT - BOX_LEFT;
    double barWidth = histSpan / BINS;
    shapes.begin(ShapeType.Filled);
    shapes.setColor(0.25f, 0.7f, 0.45f, 1f);
    for (int b = 0; b < BINS; b++) {
      float x = (float) (histLeft + b * barWidth);
      float h = (float) (density[b] * yScale);
      shapes.rect(x, HIST_BASE, (float) barWidth * 0.9f, h);
    }
    shapes.end();
  }

  private void drawTheoryCurve(
      ShapeRenderer shapes, double temperature, double binWidth, double yScale) {
    double histLeft = BOX_LEFT;
    double histSpan = BOX_RIGHT - BOX_LEFT;
    shapes.begin(ShapeType.Filled);
    shapes.setColor(1f, 0.8f, 0.2f, 1f);
    float prevX = 0;
    float prevY = 0;
    for (int b = 0; b <= BINS; b++) {
      double speed = b * binWidth;
      double pdf = MaxwellBoltzmann.speedProbabilityDensity(speed, temperature, MASS);
      float x = (float) (histLeft + (b / (double) BINS) * histSpan);
      float y = (float) (HIST_BASE + pdf * yScale);
      if (b > 0) {
        Draw.line(shapes, prevX, prevY, x, y, 0.06f);
      }
      prevX = x;
      prevY = y;
    }
    shapes.end();
  }
}
