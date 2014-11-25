package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyScreen extends ScreenAdapter {

	public static final String TAG = MyScreen.class.getName();

	private final PartitionGame game;

	public MyScreen(final PartitionGame game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {

		// Screen dimensions
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		// Temporary background
		game.shapeRenderer.begin(ShapeType.Filled);
		final float squareSize = w / 9;
		for (int x = 0; x < w / squareSize; x++) {
			for (int y = 0; y < h / squareSize; y++) {
				game.shapeRenderer.setColor((x % 2 == y % 2) ? Color.DARK_GRAY : Color.GRAY);
				game.shapeRenderer.rect(x * squareSize, y * squareSize, squareSize, squareSize);
			}
		}
		game.shapeRenderer.end();

	}

	@Override
	public void resize(int width, int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}
	}
}
