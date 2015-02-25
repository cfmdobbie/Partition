package com.maycontainsoftware.partition;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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

		// TODO: Load different assets dependent on current horizontal screen resolution.

		// Extract required assets from the asset manager
		developerLogo = game.manager.get("developer_logo.png", Texture.class);
		loadingAtlas = game.manager.get("loading.atlas", TextureAtlas.class);

		// TODO: Really want tiled background to fade in over about 0.5 secs
		//root.setBackground(new TiledDrawable(loadingAtlas.findRegion("background")));

		// Developer logo
		root.row().padBottom(10.0f);
		root.add(new Image(developerLogo));

		// Loading bar graphic
		root.row();
		root.add(new LoadingBar(game.manager, loadingAtlas.findRegion("loading_bar_bg"), loadingAtlas
				.findRegion("loading_bar_fg")));

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
		//game.manager.load("yellow.png", Texture.class);
	}

	/** Enqueue all Sound assets required by the app. */
	private void enqueueSounds() {
		// TODO: Enqueue Sounds
		game.manager.load("error.wav", Sound.class);
		game.manager.load("explosion.wav", Sound.class);
		game.manager.load("ping.wav", Sound.class);
		game.manager.load("tone.wav", Sound.class);
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
			game.setScreen(new MainMenuScreen(game));
		}
	}

	/**
	 * The widget that displays the loading bar.
	 * 
	 * @author Charlie
	 */
	private static class LoadingBar extends Image {

		final TextureRegion foreground;
		final float offsetX;
		final float offsetY;
		final float minU;
		final float widthU;
		final AssetManager manager;

		public LoadingBar(final AssetManager manager, final TextureRegion background, final TextureRegion foreground) {
			super(background);

			this.manager = manager;

			// Copy the TextureRegion as we're going to mess with its U2 coord
			this.foreground = new TextureRegion(foreground);
			minU = foreground.getU();
			widthU = foreground.getU2() - minU;

			// Calculate offset of foreground wrt background
			offsetX = (background.getRegionWidth() - foreground.getRegionWidth()) / 2.0f;
			offsetY = (background.getRegionHeight() - foreground.getRegionHeight()) / 2.0f;
		}

		@Override
		public void draw(final SpriteBatch batch, final float parentAlpha) {

			// Draw the background
			super.draw(batch, parentAlpha);

			// Update the portion of the foreground bar to display
			foreground.setU2(minU + widthU * manager.getProgress());

			// Draw the foreground bar
			batch.draw(foreground, getX() + offsetX, getY() + offsetY);
		}
	}
}
