package org.physics.app.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import org.physics.engine.math.Vector2;
import org.physics.engine.wave.Interference;

/**
 * The double-slit experiment, the most famous demonstration that light (and matter) travels as
 * waves. A wave comes in from the left and passes through one or two narrow slits in a barrier.
 * Each open slit acts as a fresh source of circular waves; where two sets of waves overlap they add
 * up in some places and cancel in others, painting a striped pattern of bright and dark bands on
 * the screen at the right. With a single slit there are no stripes; open the second and the stripes
 * appear, which is the whole surprise: light plus light can make darkness.
 *
 * <p>The coloured ripples on the left are the live wave (red crests, blue troughs). The bright bars
 * on the right are the pattern the screen actually records, the time-averaged brightness.
 *
 * <p>Space toggles one or two slits. Up and down change the wavelength; left and right change the
 * gap between the slits, both of which stretch or squeeze the stripes.
 */
public class InterferenceScene implements Scene {

  private static final double BARRIER_X = 2.5;
  private static final double FIELD_RIGHT = 11.5;
  private static final double SCREEN_X = 11.8;
  private static final double PROFILE_WIDTH = 3.6;
  private static final double MID_Y = 4.5;
  private static final double TOP = 8.7;
  private static final double BOTTOM = 0.3;
  private static final double FIELD_STEP = 0.26; // wave-field mesh spacing (smoothed by the GPU)
  private static final double SCREEN_STEP = 0.06; // fine, so the fringe bars look continuous
  private static final double SPEED = 3.0;

  // Reused colours for the four corners of each mesh cell (the fallback path).
  private final Color cornerA = new Color();
  private final Color cornerB = new Color();
  private final Color cornerC = new Color();
  private final Color cornerD = new Color();

  // A fragment shader evaluates the wave per pixel, so the field is perfectly smooth at any
  // wavelength. If it fails to compile (some WebGL contexts), we fall back to the coloured mesh.
  private ShaderProgram fieldShader;
  private Mesh quad;
  private boolean shaderReady;
  private boolean shaderTried;

  private static final String VERTEX_SHADER =
      "attribute vec3 a_position;\n"
          + "uniform mat4 u_projTrans;\n"
          + "varying vec2 v_world;\n"
          + "void main() {\n"
          + "  v_world = a_position.xy;\n"
          + "  gl_Position = u_projTrans * vec4(a_position, 1.0);\n"
          + "}\n";

  private static final String FRAGMENT_SHADER =
      "#ifdef GL_ES\n"
          + "precision highp float;\n"
          + "#endif\n"
          + "varying vec2 v_world;\n"
          + "uniform vec2 u_sources[2];\n"
          + "uniform int u_count;\n"
          + "uniform float u_k;\n"
          + "uniform float u_phase;\n"
          + "uniform float u_maxAmp;\n"
          + "void main() {\n"
          + "  float sum = 0.0;\n"
          + "  for (int i = 0; i < 2; i++) {\n"
          + "    if (i < u_count) {\n"
          + "      float r = distance(v_world, u_sources[i]);\n"
          + "      sum += sin(u_k * r - u_phase);\n"
          + "    }\n"
          + "  }\n"
          + "  float n = sum / u_maxAmp;\n"
          + "  gl_FragColor = vec4(max(0.0, n), 0.05, max(0.0, -n), 1.0);\n"
          + "}\n";

  private boolean twoSlits = true;
  private double wavelength = 0.7;
  private double gap = 1.8;
  private float time;

  @Override
  public String title() {
    return "Double-slit interference";
  }

  @Override
  public String controls() {
    return "space: one/two slits   up/down: wavelength   left/right: slit gap";
  }

  @Override
  public List<String> readouts() {
    return List.of(
        "slits: " + (twoSlits ? "2" : "1"),
        "wavelength: " + Draw.num(wavelength, 2),
        "slit gap: " + Draw.num(gap, 1));
  }

  @Override
  public void show() {
    ensureShader();
    reset();
  }

  // Compiles the shader and builds the full-screen field quad once. Any failure leaves shaderReady
  // false and the scene uses the mesh fallback instead.
  private void ensureShader() {
    if (shaderTried) {
      return;
    }
    shaderTried = true;
    ShaderProgram.pedantic = false; // do not crash if the driver optimises a uniform away
    fieldShader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    shaderReady = fieldShader.isCompiled();
    if (!shaderReady) {
      fieldShader.dispose();
      fieldShader = null;
      return;
    }
    float x0 = (float) BARRIER_X;
    float x1 = (float) FIELD_RIGHT;
    float y0 = (float) BOTTOM;
    float y1 = (float) TOP;
    quad = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 3, "a_position"));
    quad.setVertices(new float[] {x0, y0, 0, x1, y0, 0, x1, y1, 0, x0, y1, 0});
    quad.setIndices(new short[] {0, 1, 2, 2, 3, 0});
  }

  @Override
  public void reset() {
    twoSlits = true;
    wavelength = 0.7;
    gap = 1.8;
    time = 0f;
  }

  @Override
  public void keyPressed(int keycode) {
    if (keycode == Input.Keys.SPACE) {
      twoSlits = !twoSlits;
    } else if (keycode == Input.Keys.UP) {
      wavelength = Math.min(1.6, wavelength + 0.1);
    } else if (keycode == Input.Keys.DOWN) {
      wavelength = Math.max(0.3, wavelength - 0.1);
    } else if (keycode == Input.Keys.RIGHT) {
      gap = Math.min(4.0, gap + 0.2);
    } else if (keycode == Input.Keys.LEFT) {
      gap = Math.max(0.6, gap - 0.2);
    }
  }

  private List<Vector2> slits() {
    List<Vector2> sources = new ArrayList<>();
    if (twoSlits) {
      sources.add(new Vector2(BARRIER_X, MID_Y - gap / 2));
      sources.add(new Vector2(BARRIER_X, MID_Y + gap / 2));
    } else {
      sources.add(new Vector2(BARRIER_X, MID_Y));
    }
    return sources;
  }

  @Override
  public void update(float dt) {
    time += Math.min(dt, 0.05f);
  }

  @Override
  public void render(ShapeRenderer shapes) {
    List<Vector2> sources = slits();
    double maxAmp = sources.size();
    double maxIntensity = sources.size() * sources.size();

    if (shaderReady) {
      drawFieldShader(shapes, sources, maxAmp);
    } else {
      drawFieldMesh(shapes, sources, maxAmp);
    }
    drawOverlay(shapes, sources, maxIntensity);
  }

  // The fast, per-pixel path: one quad shaded by the fragment shader.
  private void drawFieldShader(ShapeRenderer shapes, List<Vector2> sources, double maxAmp) {
    double k = 2 * Math.PI / wavelength;
    double omega = SPEED * k;
    float phase = (float) ((omega * time) % (2 * Math.PI)); // kept bounded for shader precision

    Gdx.gl.glEnable(GL20.GL_BLEND);
    fieldShader.bind();
    fieldShader.setUniformMatrix("u_projTrans", shapes.getProjectionMatrix());
    fieldShader.setUniformf("u_k", (float) k);
    fieldShader.setUniformf("u_phase", phase);
    fieldShader.setUniformf("u_maxAmp", (float) maxAmp);
    fieldShader.setUniformi("u_count", sources.size());
    Vector2 s0 = sources.get(0);
    Vector2 s1 = sources.size() > 1 ? sources.get(1) : s0;
    fieldShader.setUniformf("u_sources[0]", (float) s0.x(), (float) s0.y());
    fieldShader.setUniformf("u_sources[1]", (float) s1.x(), (float) s1.y());
    quad.render(fieldShader, GL20.GL_TRIANGLES);
  }

  // The fallback path: a coloured triangle mesh, smoothed by the GPU between vertices.
  private void drawFieldMesh(ShapeRenderer shapes, List<Vector2> sources, double maxAmp) {
    // Sample the wave height on a grid of vertices.
    int nx = (int) Math.ceil((FIELD_RIGHT - BARRIER_X) / FIELD_STEP);
    int ny = (int) Math.ceil((TOP - BOTTOM) / FIELD_STEP);
    float[] vx = new float[nx + 1];
    float[] vy = new float[ny + 1];
    float[][] amp = new float[nx + 1][ny + 1];
    for (int i = 0; i <= nx; i++) {
      vx[i] = (float) (BARRIER_X + i * FIELD_STEP);
    }
    for (int j = 0; j <= ny; j++) {
      vy[j] = (float) (BOTTOM + j * FIELD_STEP);
    }
    for (int i = 0; i <= nx; i++) {
      for (int j = 0; j <= ny; j++) {
        amp[i][j] =
            (float)
                (Interference.amplitudeAt(sources, wavelength, SPEED, vx[i], vy[j], time) / maxAmp);
      }
    }

    // Draw the field as coloured triangles; the GPU blends the corner colours smoothly across each
    // cell, so the wave looks continuous instead of blocky.
    shapes.begin(ShapeType.Filled);
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        fieldColor(cornerA, amp[i][j]);
        fieldColor(cornerB, amp[i + 1][j]);
        fieldColor(cornerC, amp[i + 1][j + 1]);
        fieldColor(cornerD, amp[i][j + 1]);
        shapes.triangle(
            vx[i], vy[j], vx[i + 1], vy[j], vx[i + 1], vy[j + 1], cornerA, cornerB, cornerC);
        shapes.triangle(
            vx[i], vy[j], vx[i + 1], vy[j + 1], vx[i], vy[j + 1], cornerA, cornerC, cornerD);
      }
    }

    shapes.end();
  }

  // The barrier, the screen with its fringe pattern, and the slit markers, drawn on top of the
  // field by either path.
  private void drawOverlay(ShapeRenderer shapes, List<Vector2> sources, double maxIntensity) {
    shapes.begin(ShapeType.Filled);
    // The barrier: a wall with the slit openings cut out of it.
    shapes.setColor(0.35f, 0.37f, 0.42f, 1f);
    shapes.rect((float) BARRIER_X - 0.07f, (float) BOTTOM, 0.14f, (float) (TOP - BOTTOM));
    shapes.setColor(0.06f, 0.07f, 0.10f, 1f); // background, to open the slits
    for (Vector2 s : sources) {
      shapes.rect((float) BARRIER_X - 0.09f, (float) (s.y() - 0.22), 0.18f, 0.44f);
    }

    // The screen, and the brightness it records as fine bright bars reaching right.
    shapes.setColor(0.25f, 0.27f, 0.32f, 1f);
    shapes.rect((float) SCREEN_X, (float) BOTTOM, 0.06f, (float) (TOP - BOTTOM));
    for (double y = BOTTOM; y <= TOP; y += SCREEN_STEP) {
      double intensity = Interference.intensityAt(sources, wavelength, SCREEN_X, y) / maxIntensity;
      float b = (float) Math.min(1.0, intensity);
      shapes.setColor(b, b * 0.95f, b * 0.7f, 1f); // warm white
      shapes.rect(
          (float) (SCREEN_X + 0.08),
          (float) y,
          (float) (intensity * PROFILE_WIDTH),
          (float) SCREEN_STEP + 0.005f);
    }

    // The slit sources marked as small dots.
    shapes.setColor(1f, 0.9f, 0.4f, 1f);
    for (Vector2 s : sources) {
      shapes.circle((float) s.x(), (float) s.y(), 0.08f, 10);
    }
    shapes.end();
  }

  // Red for crests, blue for troughs, dark near zero (fallback mesh).
  private void fieldColor(Color color, float n) {
    color.set(Math.max(0, n), 0.05f, Math.max(0, -n), 1f);
  }

  @Override
  public void dispose() {
    if (fieldShader != null) {
      fieldShader.dispose();
    }
    if (quad != null) {
      quad.dispose();
    }
  }
}
