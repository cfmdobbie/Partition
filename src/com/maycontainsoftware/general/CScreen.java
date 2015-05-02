package com.maycontainsoftware.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Generic base screen for all screens in a CGame app. This screen holds a Stage and the root Table to which Actors are
 * to be added. Subclasses must ensure they pass render() and resize() calls to super.
 * 
 * @author Charlie
 */
public class CScreen<G extends CGame> extends ScreenAdapter {

	/** Tag for logging purposes. */
	public static final String TAG = CScreen.class.getName();

	/** Debug flag for controlling whether to draw debug output for root tables in CScreen Stages. */
	private static final boolean DEBUG_ROOT_TABLES = false;

	/** Reference to the CGame instance. */
	protected final G game;

	/** Reference to the Stage. */
	protected Stage stage;

	/** The root table which exists on the Stage, to which all UI elements are to be added. */
	protected final Table root;

	/**
	 * Construct a new CScreen instance.
	 * 
	 * @param game
	 *            The CGame subclass.
	 */
	public CScreen(final G game) {
		this.game = game;

		// Create the stage
		stage = new Stage(game.virtualWidth, game.virtualHeight, true, game.batch);
		// Apply the standard camera to this screen's stage
		stage.setCamera(game.camera);

		// Create the root table
		root = new Table();
		// Add root Table to the Stage, full-screen
		root.setFillParent(true);
		stage.addActor(root);

		// Redirect input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Draw debug lines if required
		if (DEBUG_ROOT_TABLES) {
			root.debug();
		}

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
	public void render(final float delta) {
		// Update and draw the Stage
		stage.act(delta);
		stage.draw();

		// Draw debug lines if required
		if (DEBUG_ROOT_TABLES) {
			Table.drawDebug(stage);
		}
	}

	@Override
	public void resize(final int width, final int height) {
		Gdx.app.debug(TAG, "resize(" + width + ", " + height + ")");

		// Update the stage's viewport with respect to the CGame's calculated viewport
		stage.setViewport(game.virtualWidth, game.virtualHeight, false, game.viewport.x, game.viewport.y,
				game.viewport.width, game.viewport.height);
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
