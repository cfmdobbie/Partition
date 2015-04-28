package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

		// Temporary actor to show working area on screen
		final Actor black = new Image(atlas.findRegion("black"));
		black.setPosition(pad, pad);
		black.setWidth(game.virtualWidth - pad * 2);
		black.setHeight(game.virtualHeight - pad * 3 - soundImageSize);
		root.addActor(black);
	}
}
