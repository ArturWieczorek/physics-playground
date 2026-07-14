package org.physics.app.scene;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.core.Particle;
import org.physics.engine.core.World;
import org.physics.engine.force.Gravitation;
import org.physics.engine.math.Vector2;

/**
 * Planets and moons. A heavy star sits at the centre and everything else falls around it under the
 * gravity from ch08. Give a body a sideways push and, instead of falling straight in, it keeps
 * missing the star and traces an orbit: the same ellipses Kepler found in the motion of the
 * planets. Each body leaves a fading trail so you can see the shape of its path.
 *
 * <p>Click anywhere to drop a new planet there. It is launched at exactly the speed needed for a
 * circular orbit, so it settles into a clean ring; nudge one close to another and watch the paths
 * bend. Press R to reset to the starting system.
 */
public class OrbitScene implements Scene {

  private static final float FIXED_DT = 1f / 120f;
  private static final Vector2 STAR_POSITION = new Vector2(8, 4.5);
  private static final double STAR_MASS = 220.0;
  private static final double G = 1.0;
  private static final double SOFTENING = 0.05;
  private static final int TRAIL_LENGTH = 160;

  private static final float[][] PALETTE = {
    {0.4f, 0.7f, 1f},
    {1f, 0.5f, 0.4f},
    {0.5f, 0.9f, 0.6f},
    {0.9f, 0.6f, 1f},
    {1f, 0.8f, 0.4f}
  };

  private World world;
  private Particle star;
  private final List<OrbitBody> planets = new ArrayList<>();

  private float timeBudget;
  private int colorCursor;

  @Override
  public String title() {
    return "Gravity: orbits";
  }

  @Override
  public String controls() {
    return "click: add a planet";
  }

  @Override
  public java.util.List<String> readouts() {
    return java.util.List.of("planets: " + planets.size());
  }

  @Override
  public void show() {
    reset();
  }

  @Override
  public void reset() {
    world = new World();
    planets.clear();
    colorCursor = 0;
    star = world.add(new Particle(STAR_POSITION, Vector2.ZERO, STAR_MASS).pin());
    world.addForce(new Gravitation(G, SOFTENING));

    addPlanetAt(new Vector2(11, 4.5));
    addPlanetAt(new Vector2(8, 8.0));
    addPlanetAt(new Vector2(2.5, 4.5));
    timeBudget = 0f;
  }

  private void addPlanetAt(Vector2 position) {
    Vector2 toStar = STAR_POSITION.subtract(position);
    double radius = toStar.length();
    if (radius < 0.5) {
      return; // too close to the star to make a sensible orbit
    }
    double speed = Math.sqrt(G * STAR_MASS / radius);
    // Velocity perpendicular to the line to the star gives a circular orbit.
    Vector2 direction = new Vector2(-toStar.y(), toStar.x()).normalized();
    Vector2 velocity = direction.scale(speed);

    Particle planet = world.add(new Particle(position, velocity, 1.0).radius(0.18));
    float[] color = PALETTE[colorCursor % PALETTE.length];
    colorCursor++;
    planets.add(new OrbitBody(planet, color));
  }

  @Override
  public void update(float dt) {
    timeBudget += Math.min(dt, 0.1f);
    while (timeBudget >= FIXED_DT) {
      world.step(FIXED_DT);
      timeBudget -= FIXED_DT;
    }
    for (OrbitBody planet : planets) {
      planet.recordTrail();
    }
  }

  @Override
  public void pointerDown(float worldX, float worldY) {
    addPlanetAt(new Vector2(worldX, worldY));
  }

  @Override
  public void render(ShapeRenderer shapes) {
    // Trails first, as fading comet tails, so the planets draw on top.
    shapes.begin(ShapeType.Filled);
    for (OrbitBody planet : planets) {
      List<Vector2> trail = planet.trail;
      for (int i = 1; i < trail.size(); i++) {
        float fade = i / (float) trail.size();
        shapes.setColor(planet.color[0], planet.color[1], planet.color[2], fade);
        Vector2 p0 = trail.get(i - 1);
        Vector2 p1 = trail.get(i);
        Draw.line(shapes, p0.x(), p0.y(), p1.x(), p1.y(), 0.03f);
      }
    }
    shapes.end();

    shapes.begin(ShapeType.Filled);
    // The star.
    shapes.setColor(1f, 0.85f, 0.3f, 1f);
    shapes.circle((float) star.position().x(), (float) star.position().y(), 0.6f, 40);
    // The planets.
    for (OrbitBody planet : planets) {
      shapes.setColor(planet.color[0], planet.color[1], planet.color[2], 1f);
      Vector2 p = planet.particle.position();
      shapes.circle((float) p.x(), (float) p.y(), (float) planet.particle.radius(), 20);
    }
    shapes.end();
  }

  /** A planet plus the colour it is drawn in and the fading trail of its recent positions. */
  private static final class OrbitBody {
    private final Particle particle;
    private final float[] color;
    private final List<Vector2> trail = new ArrayList<>();

    private OrbitBody(Particle particle, float[] color) {
      this.particle = particle;
      this.color = color;
    }

    private void recordTrail() {
      trail.add(particle.position());
      if (trail.size() > TRAIL_LENGTH) {
        trail.remove(0);
      }
    }
  }
}
