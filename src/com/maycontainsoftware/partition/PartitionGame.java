package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

	/** Asset manager. */
	final AssetManager manager = new AssetManager();

	/** Sound setting. For now, defaults to true and changes are not persisted. */
	boolean sound = true;

	/** Enumeration of player configurations. */
	public static enum PlayerConfiguration {
		TWO_PLAYER,
		ONE_PLAYER_VS_COMPUTER,
	}

	/** Enumeration of implemented boards. */
	public static enum BoardConfiguration {
		HUB("......\n......\n..0#..\n..#1..\n......\n......"),
		OPEN(".....\n.....\n..0..\n.....\n.....\n..1..\n.....\n....."),
		WALL(".......\n.......\n.......\n...#...\n..0#1..\n...#...\n.......\n.......\n......."),
		// TODO: HOLES *may* be a bust, need to play-test to determine whether it's worth keeping
		HOLES("...1...\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n...0..."),
		MAIN_MENU_DEMO("..0..");

		public final String boardSpec;

		BoardConfiguration(final String boardSpec) {
			this.boardSpec = boardSpec;
		}
	}

	@Override
	public void create() {
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		// Create camera of same dimensions as screen buffer
		camera = new OrthographicCamera(w, h);

		// Create rendering objects
		batch = new SpriteBatch();

		// Start on pre-loading screen
		setScreen(new PreloadingScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		// Update camera wrt changed screen dimensions
		camera.setToOrtho(false, width, height);

		super.resize(width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		manager.dispose();
	}

	@Override
	public void render() {

		// Update camera projection
		camera.update();

		// Update renderer projections
		batch.setProjectionMatrix(camera.combined);

		// Clear screenbuffer
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Render Screen
		super.render();
	}

	/** Play the "error" sound effect, if sounds are enabled. */
	void playError() {
		if (sound) {
			final Sound s = manager.get("error.wav", Sound.class);
			s.play();
		}
	}

	/** Play the "explosion" sound effect, if sounds are enabled. */
	void playExplosion() {
		if (sound) {
			final Sound s = manager.get("explosion.wav", Sound.class);
			s.play();
		}
	}

	/** Play the "ping" sound effect, if sounds are enabled. */
	void playPing() {
		if (sound) {
			final Sound s = manager.get("ping.wav", Sound.class);
			s.play();
		}
	}

	/** Play the "tone" sound effect, if sounds are enabled. */
	void playTone() {
		if (sound) {
			final Sound s = manager.get("tone.wav", Sound.class);
			s.play();
		}
	}
}
