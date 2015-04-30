package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * The main screen of the application.
 * 
 * @author Charlie
 */
public class MainScreen extends CScreen<PartitionGame> {

	/** Tag for logging purposes. */
	private static final String LOG = MainScreen.class.getName();

	/** The card stack. */
	final CardStack cardStack;

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
		final float topBarButtonSize = 40.0f;

		// Sound toggle button
		final Actor soundToggle = new SoundToggleButton(game, atlas);
		soundToggle.setX(game.virtualWidth - pad - topBarButtonSize);
		soundToggle.setY(game.virtualHeight - pad - topBarButtonSize);
		root.addActor(soundToggle);

		// Back button
		final BackButton back = new BackButton(game, atlas);
		back.setX(pad);
		back.setY(game.virtualHeight - pad - topBarButtonSize);
		root.addActor(back);

		// When the back button is pressed, go back one card in the stack
		back.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				back();
				return true;
			}
		});

		// Area with interchangeable panels containing the actual screen content

		// Create the card stack
		cardStack = new CardStack();

		// Set the back button to listen for stack changed events
		cardStack.addListener(back);

		// Set card stack size
		// Width is screen width
		final float panelAreaWidth = game.virtualWidth;
		// Height is screen height less height of sound toggle button and two bits of padding
		final float panelAreaHeight = game.virtualHeight - topBarButtonSize - pad * 2;
		cardStack.setSize(panelAreaWidth, panelAreaHeight);

		// Add it to the root table
		root.addActor(cardStack);

		// Now the card stack is set up, set the initial card
		cardStack.setInitialCard(new MainPanel(game, cardStack));
	}

	/** Go back one card in the card stack. */
	private void back() {
		cardStack.pop();
	}
}
