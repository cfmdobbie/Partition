package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * The main screen of the application.
 * 
 * @author Charlie
 */
public class MainScreen extends CScreen<PartitionGame> {

	/** Tag for logging purposes. */
	private static final String LOG = MainScreen.class.getName();

	/**
	 * Construct a new MainScreen.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
	public MainScreen(final PartitionGame game) {
		super(game);

		Gdx.app.debug(LOG, "MainScreen.<init>");

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		// Static textures background
		root.setBackground(new TiledDrawable(atlas.findRegion("background")));

		// UI spacing
		final float pad = 10.0f;

		// Sound toggle button
		final float soundImageSize = 40.0f;
		final Actor soundToggle = new SoundToggleButton(game, atlas);
		soundToggle.setX(game.virtualWidth - pad - soundImageSize);
		soundToggle.setY(game.virtualHeight - pad - soundImageSize);
		root.addActor(soundToggle);

		// Area with interchangeable panels containing the actual screen content

		// Create the card stack
		final CardStack cardStack = new CardStack();

		// Set card stack size
		// Width is screen width
		final float panelAreaWidth = game.virtualWidth;
		// Height is screen height less height of sound toggle button and two bits of padding
		final float panelAreaHeight = game.virtualHeight - soundImageSize - pad * 2;
		cardStack.setSize(panelAreaWidth, panelAreaHeight);

		// Add it to the root table
		root.addActor(cardStack);

		// Now the card stack is set up, set the initial card
		cardStack.setInitialCard(new MainPanel(game, cardStack));
	}
}
