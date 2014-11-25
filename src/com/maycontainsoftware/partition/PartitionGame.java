package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The main Game instance.
 * 
 * @author Charlie
 */
public class PartitionGame extends Game {

	/** Tag for logging purposes. */
	public static final String TAG = PartitionGame.class.getName();

	/** App-global debug flag, for controlling debug output etc. */
	public static final boolean DEBUG = true;

	/** The OpenGL camera used by the app. There is only one camera and it is managed entirely by the Game instance. */
	OrthographicCamera camera;

	/** The raster graphics renderer. There is only one SpriteBatch and it is held by the Game instance. */
	SpriteBatch batch;

	/** The primitive renderer. There is only one ShapeRenderer and it is held by the Game instance. */
	ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		// Create camera of same dimensions as screen buffer
		camera = new OrthographicCamera(w, h);

		// Create rendering objects
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// TEMP: For now, just start with MyScreen
		setScreen(new MyScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		// Update camera wrt changed screen dimensions
		camera.setToOrtho(false, width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void render() {

		// Update camera projection
		camera.update();

		// Update renderer projections
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		// Clear screenbuffer
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Render Screen
		super.render();
	}
}
