package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyScreen extends ScreenAdapter {

	public static final String TAG = MyScreen.class.getName();

	private final PartitionGame game;
	private GameState state;

	public MyScreen(final PartitionGame game) {
		this.game = game;
		this.state = GameState.newGameState("....\n.0#.\n.#1.\n....");
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

		final int columns = this.state.tileEnabled.length;
		final int rows = this.state.tileEnabled[0].length;

		final float tileWidth = w / columns;
		final float tileHeight = h / rows;

		game.shapeRenderer.begin(ShapeType.Filled);
		game.shapeRenderer.setColor(Color.WHITE);
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				if (this.state.tileEnabled[c][r]) {
					game.shapeRenderer.rect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
				}
			}
		}
		game.shapeRenderer.setColor(Color.RED);
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			final byte[] coords = GameState.getPlayerCoords(state, p);
			game.shapeRenderer.rect(coords[0] * tileWidth + tileWidth * 0.1f, coords[1] * tileHeight + tileHeight
					* 0.1f, tileWidth * 0.8f, tileHeight * 0.8f);
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
