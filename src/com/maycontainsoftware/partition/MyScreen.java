package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

/**
 * A Screen instance.
 * 
 * @author Charlie
 */
public class MyScreen extends ScreenAdapter {

	/** Tag for logging purposes. */
	public static final String TAG = MyScreen.class.getName();

	/** Reference to the Game instance. */
	private final PartitionGame game;

	/** Reference to the current game state. */
	private GameState state;

	/** Player colors, arranged from best to worst. */
	private static final Color[] PLAYER_COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.YELLOW, };

	// TEMP: Board configurations used for testing
	public static final String BOARD_1 = "....\n.0#.\n.#1.\n....";
	public static final String BOARD_2 = "#.....#\n..0.1..\n.7#.#2.\n.......\n.6#.#3.\n..5.4..\n#.....#";
	public static final String BOARD_3 = "......\n......\n..0#..\n..#1..\n......\n......";
	public static final String BOARD_4 = ".....\n.....\n..0..\n.....\n.....\n..1..\n.....\n.....";

	// Board dimensions
	private final int boardColumns;
	private final int boardRows;

	// Screen dimensions
	private float screenWidth;
	private float screenHeight;

	// Tile size
	private float tileWidth;
	private float tileHeight;

	/**
	 * Constructor
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public MyScreen(final PartitionGame game) {
		this.game = game;
		this.state = GameState.newGameState(BOARD_4);

		// Determine board size
		boardColumns = this.state.tileEnabled.length;
		boardRows = this.state.tileEnabled[0].length;
	}

	@Override
	public void render(float delta) {

		// Process any touch input
		if (Gdx.input.justTouched()) {
			// Get touch location
			final Vector3 pos = new Vector3();
			pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			game.camera.unproject(pos);
			// The Vector3 "pos" now contains the touch location

			// Convert to a coordinate in board space
			final byte r = (byte) ((-(pos.y - screenHeight)) / tileHeight);
			final byte c = (byte) (pos.x / tileWidth);

			if (PartitionGame.DEBUG) {
				Gdx.app.log(TAG, "Touch event on (" + c + "," + r + ")");
			}
			// state.tileEnabled[c][r] = !state.tileEnabled[c][r];

			try {
				state = GameState.apply(state, new byte[] { c, r });
			} catch (Error e) {
				if (PartitionGame.DEBUG) {
					Gdx.app.log(TAG, "Invalid move!");
				}
			}

			// TEMP: Reset board if game cannot proceed
			if (GameState.isGameOver(state)) {
				state = GameState.newGameState(BOARD_4);
			} else if (GameState.isStalemate(state)) {
				state = GameState.newGameState(BOARD_4);
			}
		}

		// Temporary background
		game.shapeRenderer.begin(ShapeType.Filled);
		final float squareSize = screenWidth / 13;
		for (int x = 0; x < screenWidth / squareSize; x++) {
			for (int y = 0; y < screenHeight / squareSize; y++) {
				game.shapeRenderer.setColor((x % 2 == y % 2) ? Color.DARK_GRAY : Color.GRAY);
				game.shapeRenderer.rect(x * squareSize, y * squareSize, squareSize, squareSize);
			}
		}
		game.shapeRenderer.end();

		// Start drawing
		game.shapeRenderer.begin(ShapeType.Filled);

		// Draw tiles
		game.shapeRenderer.setColor(Color.WHITE);
		for (int c = 0; c < boardColumns; c++) {
			for (int r = 0; r < boardRows; r++) {
				if (this.state.tileEnabled[c][r]) {
					final float x = c * tileWidth;
					final float y = screenHeight - (r + 1) * tileHeight;
					game.shapeRenderer.rect(x + tileWidth * 0.1f, y + tileHeight * 0.1f, tileWidth * 0.8f,
							tileHeight * 0.8f);
				}
			}
		}

		// Draw players
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			game.shapeRenderer.setColor(PLAYER_COLORS[p]);
			final byte[] coords = GameState.getPlayerCoords(state, p);
			final float x = coords[0] * tileWidth;
			final float y = screenHeight - (coords[1] + 1) * tileHeight;
			game.shapeRenderer
					.ellipse(x + tileWidth * 0.2f, y + tileHeight * 0.2f, tileWidth * 0.6f, tileHeight * 0.6f);
		}

		// Stop drawing
		game.shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// When the screen is resized, need to re-layout the UI

		// Remember screen dimensions
		screenWidth = width;
		screenHeight = height;

		// Determine tile size
		tileWidth = screenWidth / boardColumns;
		tileHeight = screenHeight / boardRows;
	}
}
