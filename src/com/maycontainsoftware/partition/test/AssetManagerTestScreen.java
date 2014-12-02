package com.maycontainsoftware.partition.test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.maycontainsoftware.partition.BaseScreen;
import com.maycontainsoftware.partition.PartitionGame;

public class AssetManagerTestScreen extends BaseScreen {

	private final AssetManager manager;
	private int frames = 0;

	public AssetManagerTestScreen(final PartitionGame game) {
		super(game);

		manager = new AssetManager();

		manager.load("blue.png", Texture.class);
		manager.load("cyan.png", Texture.class);
		manager.load("green.png", Texture.class);
		manager.load("orange.png", Texture.class);
		manager.load("red.png", Texture.class);
		manager.load("violet.png", Texture.class);
		manager.load("yellow.png", Texture.class);
		// About 15 frames to here

		manager.load("segoeuiblack16.fnt", BitmapFont.class);
		manager.load("segoeuiblack24.fnt", BitmapFont.class);
		manager.load("segoeuiblack32.fnt", BitmapFont.class);
		manager.load("segoeuiblack48.fnt", BitmapFont.class);
		manager.load("segoeuiblack64.fnt", BitmapFont.class);
		// About 45 frames to here

		manager.load("Background.png", Texture.class);
		manager.load("BluePlayer.png", Texture.class);
		manager.load("BlueTile.png", Texture.class);
		manager.load("RedPlayer.png", Texture.class);
		manager.load("RedTile.png", Texture.class);
		manager.load("Tile.png", Texture.class);
		// About 57 frames to here

		manager.load("bloop.wav", Sound.class);
		manager.load("bzzt.wav", Sound.class);
		// About 61 frames to here
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		frames++;

		if (manager.update()) {
			System.out.println("Loading assets took " + frames + " frames");
			game.setScreen(new FontSizeTestScreen(game));
		}
	}
}
