package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
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
public class GameBoard extends FixedAspectContainer {

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Reference to the current game state. */
	private GameState state;

	// Temporary textures
	private final TextureRegion tileTexture;
	private final TextureRegion redPlayerTexture;
	private final TextureRegion bluePlayerTexture;
	private final TextureRegion shadowTexture;
	private final TextureRegion targetTexture;

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

	/** Whether or not the game board is in "demo" mode. */
	private final boolean demoMode;

	private static enum TurnState {
		PENDING_MOVE,
		MOVING,
		PENDING_SHOOT,
		SHOOTING,
		WIN_CHECK,
		SWITCHING_PLAYERS,
		STALEMATE_CHECK,
	}

	private TurnState turnState;

	// Clock used for animation purposes, measures cumulative time in seconds
	private float animTime = 0.0f;

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
			final BoardConfiguration boardConfiguration, final boolean demoMode) {

		// Save reference to the game instance
		this.game = game;

		// Start in PENDING_MOVE state
		this.turnState = TurnState.PENDING_MOVE;

		// Save board configuration
		this.playerConfiguration = playerConfiguration;
		this.boardConfiguration = boardConfiguration;
		this.demoMode = demoMode;

		// Store references to required Textures
		tileTexture = atlas.findRegion("tile");
		redPlayerTexture = atlas.findRegion("player_red");
		bluePlayerTexture = atlas.findRegion("player_blue");
		shadowTexture = atlas.findRegion("shadow");
		targetTexture = atlas.findRegion("target");

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

		final Table t = new Table();
		for (byte r = 0; r < boardRows; r++) {
			t.row();
			for (byte c = 0; c < boardColumns; c++) {
				t.add(new TileActor(tileTexture, r, c)).expand().fill();
			}
		}

		setChild(t);
		setAspect(getDesiredAspect());
	}

	@Override
	public void act(float delta) {
		// Allow superclass to process any Actions
		super.act(delta);

		// Update animation timer
		animTime += delta;

		switch (turnState) {
		case MOVING:
			if (animTime >= 1.0f) {
				turnState = TurnState.PENDING_SHOOT;
			}
			break;
		case PENDING_MOVE:
			// No activity
			break;
		case PENDING_SHOOT:
			// No activity
			break;
		case SHOOTING:
			turnState = TurnState.WIN_CHECK;
			break;
		case STALEMATE_CHECK:
			if (GameState.isStalemate(state)) {
				// XXX
				state = GameState.newGameState(boardConfiguration.boardSpec);
				turnState = TurnState.PENDING_MOVE;
			} else {
				turnState = TurnState.PENDING_MOVE;
			}
			break;
		case SWITCHING_PLAYERS:
			turnState = TurnState.STALEMATE_CHECK;
			break;
		case WIN_CHECK:
			if (GameState.isGameOver(state)) {
				// XXX
				state = GameState.newGameState(boardConfiguration.boardSpec);
				turnState = TurnState.PENDING_MOVE;
			} else {
				turnState = TurnState.SWITCHING_PLAYERS;
			}
			break;
		}
	}

	private void doTouchUp(final byte[] tileCoord) {

		switch (turnState) {
		case MOVING:
			// Ignore
			break;
		case PENDING_MOVE:
			try {
				state = GameState.apply(state, tileCoord);
				game.playPing();
				turnState = TurnState.MOVING;
				// Reset animation time
				animTime = 0.0f;
			} catch (Error e) {
				Gdx.app.debug(GameScreen.TAG, "Invalid move!");
				game.playError();
			}
			break;
		case PENDING_SHOOT:
			try {
				state = GameState.apply(state, tileCoord);
				game.playExplosion();
				turnState = TurnState.SHOOTING;
			} catch (Error e) {
				Gdx.app.debug(GameScreen.TAG, "Invalid move!");
				game.playError();
			}
			break;
		case SHOOTING:
			// Ignore
			break;
		case STALEMATE_CHECK:
			// Ignore
			break;
		case SWITCHING_PLAYERS:
			// Ignore
			break;
		case WIN_CHECK:
			// Ignore
			break;
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		super.draw(batch, parentAlpha);

		/*
		// Remember current transformation matrix
		final Matrix4 transformMatrix = batch.getTransformMatrix().cpy();

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

			// Determine whether the player is moving
			final boolean playerMoving = p == state.currentPlayerIndex && state.turnPhase == GameState.PHASE_MOVE;

			// Determine whether the player is shooting
			final boolean playerShooting = p == state.currentPlayerIndex && state.turnPhase == GameState.PHASE_SHOOT;

			// Get the player's coordinates
			final byte[] coords = GameState.getPlayerCoords(state, p);

			// Determine the player texture
			final TextureRegion playerTexture = p == 0 ? redPlayerTexture : bluePlayerTexture;

			// Draw the player's shadow
			game.batch.draw(shadowTexture, coords[0], boardRows - 1 - coords[1], 1.0f, 1.0f);

			// Calculate the player token's Y offset
			float playerYOffset = 0.0f;
			if (playerMoving) {
				playerYOffset = 0.125f * (float) (-Math.cos(animTime * 2 * Math.PI * 1.5f)) + 0.125f;
			}

			// Draw the player
			game.batch.draw(playerTexture, coords[0], boardRows - 1 - coords[1] + playerYOffset, 1.0f, 1.0f);

			if (playerShooting) {
				// Draw the target
				float targetYOffset = 0.125f * (float) (-Math.cos(animTime * 2 * Math.PI * 1.5f)) + 0.25f;
				game.batch
						.draw(targetTexture, coords[0] + 0.25f, boardRows - 1 - coords[1] + targetYOffset, 1.0f, 1.0f);
			}
		}

		// Reset transformation matrix
		batch.setTransformMatrix(transformMatrix);
		*/
	}

	// Desired aspect ratio
	public float getDesiredAspect() {
		return boardColumns / (float) boardRows;
	}

	public byte[] actorCoordToTileCoord(final float px, final float py) {

		// Determine board size
		final float w = getWidth();
		final float h = getHeight();

		Gdx.app.debug(GameScreen.TAG, "Board size: " + w + "x" + h);
		Gdx.app.debug(GameScreen.TAG, "Touch location: (" + px + "," + py + ")");

		// Convert to a coordinate in board space
		final byte c = (byte) (px / (w / boardColumns));
		final byte r = (byte) ((-(py - h)) / (h / boardRows));

		Gdx.app.debug(GameScreen.TAG, "Touch is on tile: (" + c + "," + r + ")");

		return new byte[] { c, r };
	}

	class TileActor extends Image {
		private final byte[] coords;

		public TileActor(final TextureRegion tileTextureRegion, final byte row, final byte column) {
			super(tileTextureRegion);

			coords = new byte[] { row, column };

			addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					System.out.println("Touch:");
					System.out.println(coords);
					return true;
				};
			});
		}
	}
}
