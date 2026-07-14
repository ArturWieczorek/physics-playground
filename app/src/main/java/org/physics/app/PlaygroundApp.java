package org.physics.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import org.physics.app.scene.ChargesScene;
import org.physics.app.scene.ClothScene;
import org.physics.app.scene.CollisionScene;
import org.physics.app.scene.FluidScene;
import org.physics.app.scene.GasScene;
import org.physics.app.scene.MagnetismScene;
import org.physics.app.scene.MolecularScene;
import org.physics.app.scene.OrbitScene;
import org.physics.app.scene.Scene;
import org.physics.app.scene.SpringScene;

/**
 * The application, independent of how it is launched. LibGDX calls {@link #create()} once and then
 * {@link #render()} every frame. Our job here is small and always the same: read the mouse and
 * keyboard, hand them to the current scene, let the scene step its physics, then draw it, with a
 * heads-up display of the scene name, its controls, and its live numbers on top.
 *
 * <p>The scene is drawn in world units, mapped by a fixed 16 by 9 camera onto whatever size the
 * window happens to be. The heads-up display is drawn separately, in screen pixels, and its text is
 * scaled to the window height so it stays readable on a small window, a large monitor, or a
 * high-resolution browser canvas alike.
 */
public class PlaygroundApp extends ApplicationAdapter {

  public static final float WORLD_WIDTH = 16f;
  public static final float WORLD_HEIGHT = 9f;

  private OrthographicCamera camera;
  private Viewport viewport;
  private ShapeRenderer shapes;
  private SpriteBatch batch;
  private BitmapFont font;
  private final Matrix4 hudMatrix = new Matrix4();

  private final List<Scene> scenes = new ArrayList<>();
  private int current;
  private boolean paused;
  private boolean stepOnce;
  private float timeScale = 1f;

  private boolean pointerWasDown;
  private final Vector3 pointer = new Vector3();

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    viewport.apply(true); // place the origin at the bottom-left of the view
    shapes = new ShapeRenderer();
    batch = new SpriteBatch();
    font = new BitmapFont();
    // Smooth the built-in font when we scale it up, so it stays crisp instead of blocky.
    font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    font.setUseIntegerPositions(false);

    scenes.add(new SpringScene());
    scenes.add(new CollisionScene());
    scenes.add(new GasScene());
    scenes.add(new OrbitScene());
    scenes.add(new ChargesScene());
    scenes.add(new MagnetismScene());
    scenes.add(new MolecularScene());
    scenes.add(new ClothScene());
    scenes.add(new FluidScene());
    scenes.get(current).show();
  }

  @Override
  public void render() {
    handleInput();

    Scene scene = scenes.get(current);
    if (!paused) {
      scene.update(Gdx.graphics.getDeltaTime() * timeScale);
    } else if (stepOnce) {
      scene.update(1f / 60f); // advance a single frame while paused
      stepOnce = false;
    }

    ScreenUtils.clear(0.06f, 0.07f, 0.10f, 1f);
    // Enable alpha blending so fading trails and translucent effects show through.
    Gdx.gl.glEnable(GL20.GL_BLEND);
    shapes.setProjectionMatrix(camera.combined);
    scene.render(shapes);

    drawHud(scene);
  }

  private void handleInput() {
    for (int i = 0; i < scenes.size(); i++) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
        switchTo(i);
      }
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      scenes.get(current).reset();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      paused = !paused;
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
      stepOnce = true; // single-step the next frame (most useful while paused)
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)) {
      timeScale = Math.max(0.1f, timeScale - 0.1f); // slow motion
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)) {
      timeScale = Math.min(3f, timeScale + 0.1f); // speed up
    }
    // Forward the keys scenes use for their own controls (heating a gas, changing a field, ...).
    for (int key :
        new int[] {
          Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.SPACE
        }) {
      if (Gdx.input.isKeyJustPressed(key)) {
        scenes.get(current).keyPressed(key);
      }
    }

    if (Gdx.input.isTouched()) {
      pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
      viewport.unproject(pointer); // screen pixels -> world units
      if (pointerWasDown) {
        scenes.get(current).pointerDrag(pointer.x, pointer.y);
      } else {
        scenes.get(current).pointerDown(pointer.x, pointer.y);
        pointerWasDown = true;
      }
    } else if (pointerWasDown) {
      scenes.get(current).pointerUp();
      pointerWasDown = false;
    }
  }

  private void switchTo(int index) {
    if (index == current) {
      return;
    }
    current = index;
    scenes.get(current).show();
    pointerWasDown = false;
  }

  private void drawHud(Scene scene) {
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();
    float ui = h / 720f; // scale everything to the window height, so text is resolution-independent
    hudMatrix.setToOrtho2D(0, 0, w, h);

    List<String> meters = scene.readouts();
    float topBar = 66f * ui;
    float lineGap = 26f * ui;

    // Translucent panels behind the text, so it stays legible over any scene.
    Gdx.gl.glEnable(GL20.GL_BLEND);
    shapes.setProjectionMatrix(hudMatrix);
    shapes.begin(ShapeType.Filled);
    shapes.setColor(0f, 0f, 0f, 0.5f);
    shapes.rect(0, h - topBar, w, topBar);
    if (!meters.isEmpty()) {
      float boxH = (meters.size() * lineGap) + 14f * ui;
      shapes.rect(0, 0, 320f * ui, boxH);
    }
    shapes.end();

    // The text itself.
    batch.setProjectionMatrix(hudMatrix);
    batch.begin();

    font.getData().setScale(2.0f * ui);
    font.setColor(0.96f, 0.97f, 1f, 1f);
    String title = scene.title();
    if (paused) {
      title += "   [paused]";
    } else if (Math.abs(timeScale - 1f) > 0.01f) {
      title += "   [speed x" + (Math.round(timeScale * 10) / 10.0) + "]";
    }
    font.draw(batch, title, 16f * ui, h - 14f * ui);

    font.getData().setScale(1.3f * ui);
    font.setColor(0.62f, 0.78f, 1f, 1f);
    font.draw(batch, controlsLine(scene), 16f * ui, h - 14f * ui - 30f * ui);

    if (!meters.isEmpty()) {
      font.setColor(0.85f, 0.92f, 1f, 1f);
      float y = meters.size() * lineGap;
      for (String meter : meters) {
        font.draw(batch, meter, 14f * ui, y);
        y -= lineGap;
      }
    }
    batch.end();
  }

  private String controlsLine(Scene scene) {
    String sceneControls = scene.controls();
    String shared = "1-" + scenes.size() + " scenes   R reset   P pause   [ ] speed";
    return sceneControls.isEmpty() ? shared : sceneControls + "      " + shared;
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  @Override
  public void dispose() {
    if (shapes != null) {
      shapes.dispose();
    }
    if (batch != null) {
      batch.dispose();
    }
    if (font != null) {
      font.dispose();
    }
    for (Scene scene : scenes) {
      scene.dispose();
    }
  }
}
