package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Base screen for all resizeable screens in the app. This screen holds the current Stage instance and a persistent
 * Table to which Actors are to be added. Subclasses must ensure they pass render() and resize() calls to super.
 * 
 * @author Charlie
 */
public class BaseScreen extends ScreenAdapter {

	/** Tag for logging purposes. */
	public static final String TAG = BaseScreen.class.getName();

	/** Debug flag for controlling whether to drag debug output for root tables in BaseScreen Stages. */
	private static final boolean DEBUG_ROOT_TABLES = false;

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Reference to the Stage. The Stage is re-created on every invocation of resize(). */
	protected Stage stage;

	/**
	 * The root table containing all UI elements. Created on class construction and persistent for the lifetime of the
	 * class.
	 */
	protected final Table root;

	// Current screen dimensions

	/** Current screen width in pixels. Update on every call to resize(). */
	protected float screenWidth;

	/** Current screen height in pixels. Update on every call to resize(). */
	protected float screenHeight;

	/**
	 * Construct a new BaseScreen instance.
	 * 
	 * @param game
	 */
	public BaseScreen(final PartitionGame game) {
		this.game = game;

		// Create the root table
		root = new Table();
		root.setFillParent(true);

		if (DEBUG_ROOT_TABLES) {
			root.debug();
		}
	}

	@Override
	public void render(final float delta) {
		if (stage != null) {
			// Update and draw the Stage
			stage.act(delta);
			stage.draw();

			if (PartitionGame.DEBUG) {
				Table.drawDebug(stage);
			}
		}
	}

	@Override
	public void resize(final int width, final int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Dispose of old Stage
		if (stage != null) {
			stage.dispose();
			stage = null;
		}

		// Create new Stage
		stage = new Stage(width, height, true, game.batch);

		// Add root Table to the Stage
		stage.addActor(root);

		// Redirect input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Remember screen dimensions
		screenWidth = width;
		screenHeight = height;

		// Check whether screen is to handle the back button (and the escape key)
		if (handleBack()) {
			// Catch back button
			Gdx.input.setCatchBackKey(true);
			stage.getRoot().addListener(new InputListener() {
				@Override
				public boolean keyDown(InputEvent event, int keycode) {
					if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								doBack();
							}
						});
						return true;
					} else {
						return super.keyDown(event, keycode);
					}
				}
			});
		} else {
			// Don't catch back button
			Gdx.input.setCatchBackKey(false);
		}
	}

	@Override
	public void dispose() {
		if (stage != null) {
			stage.dispose();
		}
	}

	/**
	 * Whether or not the screen wants to handle the "back" button on a mobile device, or the "escape" key on a
	 * computer. Subclasses will override this method to return true if they wish to handle these functions.
	 * 
	 * @return False
	 */
	protected boolean handleBack() {
		return false;
	}

	/**
	 * The action to take when the "back" button or the "escape" key is pressed. This method is executed on the main
	 * loop, outside of any rendering.
	 */
	protected void doBack() {
	}
}
