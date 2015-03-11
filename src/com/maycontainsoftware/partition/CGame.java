package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class CGame extends Game {

	/* Logging. */

	/** Tag for logging purposes. */
	private static final String TAG = CGame.class.getSimpleName();

	/** Whether debug output should be logged. */
//	private static final boolean DEBUG = true;

//	protected static final void debug(final String tag, final String message) {
//		if (DEBUG) {
//			Gdx.app.log(tag, message);
//		}
//	}
//
//	protected static final void log(final String tag, final String message) {
//		Gdx.app.log(tag, message);
//	}

	/* Virtual screen metrics. */

	/** The width of the virtual render area. */
	public final int virtualWidth;

	/** The height of the virtual render area. */
	public final int virtualHeight;

	/** The aspect ratio of the virtual render area. */
	private final float virtualAspectRatio;

	/**
	 * The app-global SpriteBatch. For performance reasons, a single SpriteBatch exists and is accessed from all Screens
	 * in the app
	 */
	SpriteBatch batch;

	ShapeRenderer shapeRenderer;

	/** The app-global camera. This is used by all Screens. */
	OrthographicCamera camera;

	/** Rectangle that represents the glViewport. */
	final Rectangle viewport = new Rectangle();

	/** The asset manager used by the loading screen to load all assets not directly required by the loading screen. */
	AssetManager manager;

	TextureAtlas loadingAtlas;
	TextureAtlas textureAtlas;

	public CGame(int virtualWidth, int virtualHeight) {

		// N.B. Gdx.app is null at this point

		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;

		virtualAspectRatio = (float) virtualWidth / (float) virtualHeight;
	}

	@Override
	public void create() {

		Gdx.app.debug(TAG, "create()");
		Gdx.app.debug(TAG, "Virtual screen size " + virtualWidth + "x" + virtualHeight);
		Gdx.app.debug(TAG, "screen density = " + Gdx.graphics.getDensity());
		Gdx.app.log(TAG, "logging level " + Gdx.app.getLogLevel());

		// Set up SpriteBatch
		batch = new SpriteBatch();

		// Set up camera
		camera = new OrthographicCamera(virtualWidth, virtualHeight);
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(virtualWidth / 2, virtualHeight / 2, 0);

		// Create asset manager
		manager = new AssetManager();

		// Load first screen
		this.setScreen(initialScreen());
	}

	protected abstract Screen initialScreen();

	@Override
	public void resize(final int width, final int height) {

		Gdx.app.debug(TAG, "resize(" + width + ", " + height + ")");

		// Calculate display aspect ratio
		final float displayAspectRatio = (float) width / (float) height;

		// Recalculate glViewport
		if (displayAspectRatio > virtualAspectRatio) {
			// Display is wider than the game
			viewport.setSize(height * virtualAspectRatio, height);
			viewport.setPosition((width - height * virtualAspectRatio) / 2, 0);
		} else if (displayAspectRatio < virtualAspectRatio) {
			// Display is taller than the game
			viewport.setSize(width, width / virtualAspectRatio);
			viewport.setPosition(0, (height - width / virtualAspectRatio) / 2);
		} else {
			// Display exactly matches game
			viewport.setSize(width, height);
			viewport.setPosition(0, 0);
		}

		// Pass resize() call to active Screen
		super.resize(width, height);
	}

	@Override
	public void render() {

		// Clear colour buffer to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Don't scissor this clear operation
		Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Update the camera
		camera.update();

		// Map rendered scene to centred viewport of correct aspect ratio
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		// Scissor buffer operations to the viewport
		Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Reset SpriteBatch color to white
		// TODO: This doesn't appear to be necessary - check this!
		batch.setColor(Color.WHITE);

		// Pass render() call to active Screen
		super.render();
	}

	@Override
	public void dispose() {

		Gdx.app.debug(TAG, "dispose()");

		// Dispose of stuff
		if (batch != null) {
			batch.dispose();
		}
		if (shapeRenderer != null) {
			shapeRenderer.dispose();
		}
		if (manager != null) {
			manager.dispose();
		}

		// Pass render() call to active Screen
		super.dispose();
	}
}
