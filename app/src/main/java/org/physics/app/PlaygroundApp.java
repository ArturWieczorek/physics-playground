package org.physics.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import org.physics.app.scene.CollisionScene;
import org.physics.app.scene.Scene;
import org.physics.app.scene.SpringScene;

/**
 * The application, independent of how it is launched. LibGDX calls {@link #create()} once and then
 * {@link #render()} every frame. Our job here is small and always the same: read the mouse and
 * keyboard, hand them to the current scene, let the scene step its physics, then draw it, with a
 * short heads-up line of text on top.
 *
 * <p>We draw the scene in world units, not pixels. A camera and viewport map a fixed 16 by 9 world
 * onto whatever size the window happens to be, so scenes never worry about resolution. The text
 * overlay is drawn separately, in screen pixels.
 *
 * <p>The number keys switch scenes; each chapter from ch05 adds one to the list.
 */
public class PlaygroundApp extends ApplicationAdapter {

  public static final float WORLD_WIDTH = 16f;
  public static final float WORLD_HEIGHT = 9f;

  private OrthographicCamera camera;
  private Viewport viewport;
  private ShapeRenderer shapes;
  private SpriteBatch batch;
  private BitmapFont font;

  private final List<Scene> scenes = new ArrayList<>();
  private int current;

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

    scenes.add(new SpringScene());
    scenes.add(new CollisionScene());
    scenes.get(current).show();
  }

  @Override
  public void render() {
    handleInput();

    scenes.get(current).update(Gdx.graphics.getDeltaTime());

    ScreenUtils.clear(0.06f, 0.07f, 0.10f, 1f);
    shapes.setProjectionMatrix(camera.combined);
    scenes.get(current).render(shapes);

    drawHud();
  }

  private void handleInput() {
    // Number keys pick a scene.
    for (int i = 0; i < scenes.size(); i++) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
        switchTo(i);
      }
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      scenes.get(current).reset();
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

  private void drawHud() {
    batch
        .getProjectionMatrix()
        .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.begin();
    float top = Gdx.graphics.getHeight() - 10f;
    font.setColor(Color.WHITE);
    font.draw(batch, scenes.get(current).title(), 12f, top);
    font.setColor(0.7f, 0.75f, 0.85f, 1f);
    font.draw(batch, controlsLine(), 12f, top - 22f);
    batch.end();
  }

  private String controlsLine() {
    StringBuilder line = new StringBuilder();
    for (int i = 0; i < scenes.size(); i++) {
      line.append(i + 1).append(' ').append(scenes.get(i).title()).append("    ");
    }
    line.append("|   R reset   |   click or drag to interact");
    return line.toString();
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
