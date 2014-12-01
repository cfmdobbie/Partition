package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Screen to asynchronously load any assets required for the loading screen.
 * 
 * @author Charlie
 */
public class PreloadingScreen extends BaseScreen {

	/** Developer logo texture, loaded synchronously by the constructor. */
	private final Texture mcsLogo;

	/**
	 * Construct a PreloadingScreen object. This will be the first screen opened by the app.
	 * 
	 * @param game
	 */
	public PreloadingScreen(final PartitionGame game) {
		super(game);

		// Queue MCS logo
		game.manager.load("mcs_logo.png", Texture.class);

		// Force asset to load
		game.manager.finishLoading();

		// Get MCS logo from manager
		mcsLogo = game.manager.get("mcs_logo.png", Texture.class);

		// Set up user interface
		root.row().padTop(100.0f);
		root.add(new Image(mcsLogo));
		root.row().expandY();
		root.add();

		// Enqueue any assets required for the proper loading screen
		// TODO
		game.manager.load("loading.png", Texture.class);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Continue loading of assets
		if (game.manager.update()) {
			// Enough assets loaded to open proper loading screen
			game.setScreen(new LoadingScreen(game));
		}
	}
}
