package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyScreen extends ScreenAdapter {

	public static final String TAG = MyScreen.class.getName();

	private final PartitionGame game;
	private GameState state;

	private static final Color[] PLAYER_COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA,
			Color.CYAN, Color.PINK, Color.ORANGE, };

	public static final String BOARD_1 = "....\n.0#.\n.#1.\n....";
	public static final String BOARD_2 = "#.....#\n..0.1..\n.7#.#2.\n.......\n.6#.#3.\n..5.4..\n#.....#";

	public MyScreen(final PartitionGame game) {
		this.game = game;
		this.state = GameState.newGameState(BOARD_2);
	}

	@Override
	public void render(float delta) {

		// Screen dimensions
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		// Temporary background
		game.shapeRenderer.begin(ShapeType.Filled);
		final float squareSize = w / 13;
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
					final float x = c * tileWidth;
					final float y = h - (r + 1) * tileHeight;
					game.shapeRenderer.rect(x + tileWidth * 0.1f, y + tileHeight * 0.1f, tileWidth * 0.8f,
							tileHeight * 0.8f);
				}
			}
		}
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			game.shapeRenderer.setColor(PLAYER_COLORS[p]);
			final byte[] coords = GameState.getPlayerCoords(state, p);
			final float x = coords[0] * tileWidth;
			final float y = h - (coords[1] + 1) * tileHeight;
			game.shapeRenderer
					.ellipse(x + tileWidth * 0.2f, y + tileHeight * 0.2f, tileWidth * 0.6f, tileHeight * 0.6f);
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
