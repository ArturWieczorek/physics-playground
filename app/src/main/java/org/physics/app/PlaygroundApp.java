package org.physics.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.physics.app.scene.Scene;
import org.physics.app.scene.SpringScene;

/**
 * The application, independent of how it is launched. LibGDX calls {@link #create()} once and then
 * {@link #render()} every frame. Our job here is small and always the same: read the mouse and
 * keyboard, hand them to the current scene, let the scene step its physics, then draw it.
 *
 * <p>We draw in world units, not pixels. A camera and viewport map a fixed 16 by 9 world onto
 * whatever size the window happens to be, so scenes never worry about resolution.
 */
public class PlaygroundApp extends ApplicationAdapter {

  public static final float WORLD_WIDTH = 16f;
  public static final float WORLD_HEIGHT = 9f;

  private OrthographicCamera camera;
  private Viewport viewport;
  private ShapeRenderer shapes;
  private Scene scene;

  private boolean pointerWasDown;
  private final Vector3 pointer = new Vector3();

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    viewport.apply(true); // place the origin at the bottom-left of the view
    shapes = new ShapeRenderer();

    scene = new SpringScene();
    scene.show();
  }

  @Override
  public void render() {
    handleInput();

    scene.update(Gdx.graphics.getDeltaTime());

    ScreenUtils.clear(0.06f, 0.07f, 0.10f, 1f);
    shapes.setProjectionMatrix(camera.combined);
    scene.render(shapes);
  }

  private void handleInput() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      scene.reset();
    }

    if (Gdx.input.isTouched()) {
      pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
      viewport.unproject(pointer); // screen pixels -> world units
      if (pointerWasDown) {
        scene.pointerDrag(pointer.x, pointer.y);
      } else {
        scene.pointerDown(pointer.x, pointer.y);
        pointerWasDown = true;
      }
    } else if (pointerWasDown) {
      scene.pointerUp();
      pointerWasDown = false;
    }
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
    if (scene != null) {
      scene.dispose();
    }
  }
}
