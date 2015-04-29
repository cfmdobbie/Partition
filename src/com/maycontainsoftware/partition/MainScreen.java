package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

		// Width is screen width less padding on either side
		final float panelAreaWidth = game.virtualWidth - pad * 2;
		// Height is screen height less height of sound toggle button and three bits of padding
		final float panelAreaHeight = game.virtualHeight - soundImageSize - pad * 3;

		// Location is one set of padding away from the origin in each direction
		final float panelAreaX = pad;
		final float panelAreaY = pad;

		// Create initial panel
		// XXX: Temporary representative actor
		final Button initialActor = new Button(new TextureRegionDrawable(atlas.findRegion("play_up")));

		// Create the panel area
		final PanelArea panelArea = new PanelArea(initialActor);

		// XXX: Temporary listener to test push() function of panel area
		initialActor.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				panelArea.push(new Image(atlas.findRegion("play_down")));
				return true;
			}
		});

		// Set panel area position and size
		panelArea.setPosition(panelAreaX, panelAreaY);
		panelArea.setSize(panelAreaWidth, panelAreaHeight);

		// Add it to the root table
		root.addActor(panelArea);
	}
}
