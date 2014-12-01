package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Screen to asynchronously load any assets required for the loading screen.
 * 
 * @author Charlie
 */
public class PreloadingScreen extends BaseScreen {

	/** Developer logo texture, loaded synchronously by the constructor. */
	private final Texture developerLogo;

	/**
	 * Construct a PreloadingScreen object. This will be the first screen opened by the app.
	 * 
	 * @param game
	 */
	public PreloadingScreen(final PartitionGame game) {
		super(game);

		// Queue developer logo
		game.manager.load("developer_logo.png", Texture.class);

		// Force asset to load
		game.manager.finishLoading();

		// Get developer logo from manager
		developerLogo = game.manager.get("developer_logo.png", Texture.class);

		// Set up user interface - developer logo in centre of screen
		root.row();
		root.add(new Image(developerLogo));

		// Enqueue any assets required for the proper loading screen
		game.manager.load("loading.atlas", TextureAtlas.class);
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
