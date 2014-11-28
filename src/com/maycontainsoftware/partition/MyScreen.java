package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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

	private Stage stage;

	private final Table root;

	/** Reference to the current game state. */
	private GameState state;

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

	// Temporary textures
	final Texture tileTexture = new Texture(Gdx.files.internal("Tile.png"));
	final Texture redPlayerTexture = new Texture(Gdx.files.internal("RedPlayer.png"));
	final Texture bluePlayerTexture = new Texture(Gdx.files.internal("BluePlayer.png"));

	/**
	 * Constructor
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public MyScreen(final PartitionGame game) {
		this.game = game;
		this.state = GameState.newGameState(BOARD_4);

		// Create the root table
		root = new Table();
		root.setFillParent(true);

		// Temporary background effect applied to root Table
		final Texture backgroundTexture = new Texture(Gdx.files.internal("Background.png"));
		root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

		// Determine board size
		boardColumns = this.state.tileEnabled.length;
		boardRows = this.state.tileEnabled[0].length;
		final float boardAspect = boardColumns / (float) boardRows;

		// Board container
		final Actor child = new Image(new Texture(Gdx.files.internal("yellow.png")));
		final FixedAspectContainer boardContainer = new FixedAspectContainer(child, boardAspect);
		root.row();
		root.add(boardContainer).expand().fill();
	}

	@Override
	public void render(float delta) {

		stage.act(delta);
		stage.draw();

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

		// Start drawing
		game.batch.begin();

		// Draw tiles
		for (int c = 0; c < boardColumns; c++) {
			for (int r = 0; r < boardRows; r++) {
				if (this.state.tileEnabled[c][r]) {
					final float x = c * tileWidth;
					final float y = screenHeight - (r + 1) * tileHeight;
					game.batch.draw(tileTexture, x + tileWidth * 0.1f, y + tileHeight * 0.1f, tileWidth * 0.8f,
							tileHeight * 0.8f);
				}
			}
		}

		// Draw players
		// FUTURE: This is hard-coded for two players at this time. Should improve this.
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			final byte[] coords = GameState.getPlayerCoords(state, p);
			final float x = coords[0] * tileWidth;
			final float y = screenHeight - (coords[1] + 1) * tileHeight;
			game.batch.draw(p == 0 ? redPlayerTexture : bluePlayerTexture, x + tileWidth * 0.1f, y + tileHeight * 0.1f,
					tileWidth * 0.8f, tileHeight * 0.8f);
		}

		// Stop drawing
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Dispose of old Stage
		if (stage != null) {
			stage.dispose();
			stage = null;
		}

		// Create new Stage
		stage = new Stage(width, height, true, game.batch);

		// Add root Table to the Stage
		stage.addActor(root);

		// Redirect input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Remember screen dimensions
		screenWidth = width;
		screenHeight = height;

		// Determine tile size
		tileWidth = screenWidth / boardColumns;
		tileHeight = screenHeight / boardRows;
	}
}
