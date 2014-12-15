package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * GameBoard draws the Partition board at unit scale, but uses OpenGL modelview matrix transformations to transform this
 * unit-scale render into a render at the correct size and location. This avoids issues with resizing - the board is
 * always drawn at unit scale, so no board elements need to be updated upon resize.
 * 
 * Note that this class is not static at this time!
 * 
 * @author Charlie
 */
public class GameBoard extends Widget {

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Reference to the current game state. */
	private GameState state;

	// Temporary textures
	private final TextureRegion tileTexture;
	private final TextureRegion redPlayerTexture;
	private final TextureRegion bluePlayerTexture;

	// Board dimensions
	/** Number of columns on the current board. */
	private final int boardColumns;

	/** Number of rows on the board. */
	private final int boardRows;

	/** The chosen player configuration. */
	@SuppressWarnings("unused")
	private final PlayerConfiguration playerConfiguration;

	/** The chosen board configuration. */
	private final BoardConfiguration boardConfiguration;

	/**
	 * Construct a new GameBoard.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param atlas
	 *            The TextureAtlas containing all graphics required to draw the board.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 */
	public GameBoard(final PartitionGame game, final TextureAtlas atlas, final PlayerConfiguration playerConfiguration,
			final BoardConfiguration boardConfiguration) {

		// Save reference to the game instance
		this.game = game;

		// Save player and board configuration
		this.playerConfiguration = playerConfiguration;
		this.boardConfiguration = boardConfiguration;

		// Store references to required Textures
		tileTexture = atlas.findRegion("tile");
		redPlayerTexture = atlas.findRegion("player_red0");
		bluePlayerTexture = atlas.findRegion("player_blue0");

		// Create new game state
		this.state = GameState.newGameState(boardConfiguration.boardSpec);

		// Determine board size
		boardColumns = this.state.tileEnabled.length;
		boardRows = this.state.tileEnabled[0].length;

		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float px, float py, int pointer, int button) {
				final byte[] tileCoord = actorCoordToTileCoord(px, py);
				doTouchUp(tileCoord);
			}
		});
	}

	public float getDesiredAspect() {
		return boardColumns / (float) boardRows;
	}

	public byte[] actorCoordToTileCoord(final float px, final float py) {

		// Determine board size
		final float w = getWidth();
		final float h = getHeight();

		if (PartitionGame.DEBUG) {
			Gdx.app.log(GameScreen.TAG, "Board size: " + w + "x" + h);
			Gdx.app.log(GameScreen.TAG, "Touch location: (" + px + "," + py + ")");
		}

		// Convert to a coordinate in board space
		final byte c = (byte) (px / (w / boardColumns));
		final byte r = (byte) ((-(py - h)) / (h / boardRows));

		if (PartitionGame.DEBUG) {
			Gdx.app.log(GameScreen.TAG, "Touch is on tile: (" + c + "," + r + ")");
		}

		return new byte[] { c, r };
	}

	private void doTouchUp(final byte[] tileCoord) {
		try {
			final byte phase = state.turnPhase;
			state = GameState.apply(state, tileCoord);
			switch (phase) {
			case GameState.PHASE_MOVE:
				game.playPing();
				break;
			case GameState.PHASE_SHOOT:
				game.playExplosion();
				break;
			}
		} catch (Error e) {
			if (PartitionGame.DEBUG) {
				Gdx.app.log(GameScreen.TAG, "Invalid move!");
			}
			game.playError();
		}

		// TEMP: Reset board if game cannot proceed
		if (GameState.isGameOver(state)) {
			state = GameState.newGameState(boardConfiguration.boardSpec);
		} else if (GameState.isStalemate(state)) {
			state = GameState.newGameState(boardConfiguration.boardSpec);
		}

		// Update status message
		// updateStatusMessage();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		// Remember current transformation matrix
		final Matrix4 transformMatrix = batch.getTransformMatrix();

		// New transformation matrix, initially identical to the current matrix
		final Matrix4 newTransform = new Matrix4(transformMatrix);
		// Translate to widget's (x,y) coordinates, so (0,0) is in correct location
		newTransform.translate(getX(), getY(), 0.0f);
		// Scale up by the size of the widget, then down by the size of the board
		newTransform.scale(getWidth() / boardColumns, getHeight() / boardRows, 1.0f);
		// Use this new transformation matrix
		batch.setTransformMatrix(newTransform);

		// Draw tiles
		for (int c = 0; c < boardColumns; c++) {
			for (int r = 0; r < boardRows; r++) {
				if (state.tileEnabled[c][r]) {
					game.batch.draw(tileTexture, c, boardRows - 1 - r, 1.0f, 1.0f);
				}
			}
		}

		// Draw players
		// FUTURE: This is hard-coded for two players at this time. Should improve this.
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			final byte[] coords = GameState.getPlayerCoords(state, p);
			game.batch.draw(p == 0 ? redPlayerTexture : bluePlayerTexture, coords[0], boardRows - 1 - coords[1], 1.0f,
					1.0f);
		}

		// Reset transformation matrix
		batch.setTransformMatrix(transformMatrix);
	}
}