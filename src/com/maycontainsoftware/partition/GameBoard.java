package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
public class GameBoard extends FixedAspectContainer implements IBoard {

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Reference to the current game state. */
	//private GameState state;

	// Board dimensions
	/** Number of columns on the current board. */
	//private final int boardColumns;

	/** Number of rows on the board. */
	//private final int boardRows;

	/** The chosen player configuration. */
	//@SuppressWarnings("unused")
	//private final PlayerConfiguration playerConfiguration;

	/** The chosen board configuration. */
	//private final BoardConfiguration boardConfiguration;

	/** Whether or not the game board is in "demo" mode. */
	private final boolean demoMode;

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

		// Save board configuration
		//this.playerConfiguration = playerConfiguration;
		//this.boardConfiguration = boardConfiguration;
		this.demoMode = demoMode;

		// Create new game state
		final GameState state = GameState.newGameState(boardConfiguration.boardSpec);

		// Players
		final List<PlayerActor> players = new ArrayList<PlayerActor>(GameState.getNumberOfPlayers(state));
		for(int i = 0 ; i < GameState.getNumberOfPlayers(state); i++) {
			players.add(new PlayerActor(i, atlas));
		}

		// Tiles
		final Set<ITile> tileSet = new HashSet<ITile>();
		final TileActor[][] tiles = new TileActor[GameState.getNumberOfColumns(state)][GameState.getNumberOfRows(state)];
		for(byte c = 0 ; c < GameState.getNumberOfColumns(state) ; c++) {
			for(byte r = 0 ; r < GameState.getNumberOfRows(state) ; r++) {
				TileActor tile = new TileActor(atlas, c, r);
				tiles[c][r] = tile;
				tileSet.add(tile);
			}
		}

		final Arbiter arbiter = new Arbiter(state, this, players, tileSet);

		// Create user interface
		final Table t = new Table();
		for(int r = 0 ; r < GameState.getNumberOfRows(state) ; r++) {
			t.row();
			for(int c = 0 ; c < GameState.getNumberOfColumns(state) ; c++) {
				t.add(tiles[c][r]).expand().fill();
			}
		}

		for(PlayerActor player : players) {
			t.addActor(player);
		}

		setChild(t);
		setAspect(GameState.getNumberOfColumns(state) / (float)GameState.getNumberOfRows(state));

		arbiter.doReset();
	}

//		case PENDING_MOVE:
//			try {
//				state = GameState.apply(state, tileCoord);
//				game.playPing();
//				turnState = TurnState.MOVING;
//				// Reset animation time
//				animTime = 0.0f;
//			} catch (Error e) {
//				Gdx.app.debug(GameScreen.TAG, "Invalid move!");
//				game.playError();
//			}
//			break;
//		case PENDING_SHOOT:
//			try {
//				state = GameState.apply(state, tileCoord);
//				game.playExplosion();
//				turnState = TurnState.SHOOTING;
//			} catch (Error e) {
//				Gdx.app.debug(GameScreen.TAG, "Invalid move!");
//				game.playError();
//			}
//			break;

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

//	public byte[] actorCoordToTileCoord(final float px, final float py) {
//
//		// Determine board size
//		final float w = getWidth();
//		final float h = getHeight();
//
//		Gdx.app.debug(GameScreen.TAG, "Board size: " + w + "x" + h);
//		Gdx.app.debug(GameScreen.TAG, "Touch location: (" + px + "," + py + ")");
//
//		// Convert to a coordinate in board space
//		final byte c = (byte) (px / (w / boardColumns));
//		final byte r = (byte) ((-(py - h)) / (h / boardRows));
//
//		Gdx.app.debug(GameScreen.TAG, "Touch is on tile: (" + c + "," + r + ")");
//
//		return new byte[] { c, r };
//	}

	@Override
	public void doGameOver() {
		// TODO
	}

	@Override
	public void doStalemate() {
		// TODO
	}

	static class PlayerActor extends Actor implements IPlayer {

		private static final String[] playerTextureNames = {"player_red", "player_blue"};

		private final TextureRegion playerTexture;

		private final TextureRegion shadowTexture;

		private final TextureRegion targetTexture;

		private TileActor tile;

		private boolean pendingFirstDraw = true;

		public PlayerActor(final int id, final TextureAtlas atlas) {

			playerTexture = atlas.findRegion(playerTextureNames[id]);
			shadowTexture = atlas.findRegion("shadow");
			targetTexture = atlas.findRegion("target");
		}

		@Override
		public void doPendingMove() {
		}

		@Override
		public void doMove(final ITile targetTile, final Arbiter arbiter) {
		}

		@Override
		public void doPendingShoot() {
		}

		@Override
		public void doShoot(final ITile targetTile, final Arbiter arbiter) {
		}

		@Override
		public void act(float delta) {

//			if(pendingFirstDraw) {
//				// We were waiting for the tiles to be correctly laid out, but as the player is being drawn, this
//				// must now have happened.
//				pendingFirstDraw = false;
//				// Update player size and position from the tile
//				this.setPosition(tile.getX(), tile.getY());
//				System.out.println("Set pos to: " + getX() + "," + getY());
//				this.setSize(tile.getWidth(), tile.getHeight());
//				System.out.println("Set size to: " + getWidth() + "," + getHeight());
//			}
//			System.out.println("Have set pos to: " + getX() + "," + getY());
//
//			System.out.println("Tile's X: " + tile.getX());

			super.act(delta);
		}

		@Override
		public void draw(final SpriteBatch batch, final float parentAlpha) {

			batch.draw(shadowTexture, getX(), getY(), getWidth(), getHeight());
			batch.draw(playerTexture, getX(), getY(), getWidth(), getHeight());
		}

		@Override
		public void doReset(final ITile startingTile) {

			tile = (TileActor)startingTile;

			if(!pendingFirstDraw) {
				// Tile must be validly positioned
				this.setPosition(tile.getX(), tile.getY());
			}
		}
	}

	static class TileActor extends Actor implements ITile {

		private final TextureRegion tileTexture;

		private final byte column;

		private final byte row;

		private boolean enabled;

		public TileActor(final TextureAtlas atlas, final byte column, final byte row) {

			tileTexture = atlas.findRegion("tile");

			this.column = column;

			this.row = row;
		}

		@Override
		public byte[] getCoords() {
			return new byte[]{column, row};
		}

		@Override
		public void doError() {
		}

		@Override
		public void doShoot(final Arbiter arbiter) {
		}

		@Override
		public void draw(final SpriteBatch batch, final float parentAlpha) {
			if (enabled) {
				batch.draw(tileTexture, getX(), getY(), getWidth(), getHeight());
			}
		}

		@Override
		public void doReset(final boolean enabled) {
			this.enabled = enabled;
		}
	}
}
