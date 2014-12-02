package com.maycontainsoftware.partition;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * The loading screen. This screen displays a very simple progress bar while loading the game assets.
 * 
 * @author Charlie
 */
public class LoadingScreen extends BaseScreen {

	/** The developer logo texture, already loaded synchronously by the preloading screen. */
	private final Texture developerLogo;

	/** Atlas of assets required for this screen, already loaded asynchronously by the preloading screen. */
	private final TextureAtlas loadingAtlas;

	/**
	 * Construct a new LoadingScreen. This will be the second screen loaded in the app, straight after the
	 * PreloadingScreen.
	 * 
	 * @param game
	 */
	public LoadingScreen(final PartitionGame game) {
		super(game);

		// Extract required assets from the asset manager
		developerLogo = game.manager.get("developer_logo.png", Texture.class);
		loadingAtlas = game.manager.get("loading.atlas", TextureAtlas.class);

		root.setBackground(new TiledDrawable(loadingAtlas.findRegion("background")));

		// Extra 64px padding to offset other graphic
		root.row().height(64.0f);
		root.add();

		// Developer logo (in centre of screen)
		root.row().padTop(10.0f).padBottom(10.0f);
		root.add(new Image(developerLogo));

		// Loading graphic
		root.row();
		root.add(new Image(loadingAtlas.findRegion("loading")));

		// Enqueue all assets required for the app
		enqueueAssets();
	}

	/** Enqueue all assets required by the app. */
	private void enqueueAssets() {
		enqueueTextures();
		enqueueSounds();
		enqueueAtlases();
		enqueueFonts();
		enqueueSkins();
	}

	/** Enqueue all Texture assets required by the app. */
	private void enqueueTextures() {
		// TODO: Enqueue Textures
		game.manager.load("yellow.png", Texture.class);
		game.manager.load("Background.png", Texture.class);
	}

	/** Enqueue all Sound assets required by the app. */
	private void enqueueSounds() {
		// TODO: Enqueue Sounds
		game.manager.load("bloop.wav", Sound.class);
		game.manager.load("bzzt.wav", Sound.class);
	}

	/** Enqueue all BitmapFont assets required by the app. */
	private void enqueueFonts() {
		// TODO: Enqueue BitmapFonts
		game.manager.load("segoeuiblack16.fnt", BitmapFont.class);
		game.manager.load("segoeuiblack24.fnt", BitmapFont.class);
		game.manager.load("segoeuiblack32.fnt", BitmapFont.class);
		game.manager.load("segoeuiblack48.fnt", BitmapFont.class);
		game.manager.load("segoeuiblack64.fnt", BitmapFont.class);
	}

	/** Enqueue all TextureAtlas assets required by the app. */
	private void enqueueAtlases() {
		// TODO: Enqueue TextureAtlases
		game.manager.load("atlas.atlas", TextureAtlas.class);
	}

	/** Enqueue all Skin assets required by the app. */
	private void enqueueSkins() {
		// TODO: Enqueue Skins
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Continue loading of assets
		if (game.manager.update()) {
			// All assets loaded!
			// TODO: Want to fade screen out once all asset loading is complete
			game.setScreen(new TopMenuScreen(game));
		}
	}
}
