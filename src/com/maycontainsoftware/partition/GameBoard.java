package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Scaling;
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
public class GameBoard extends FixedSizeWidgetGroup implements IBoard {

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Whether or not the game board is in "demo" mode. */
	private final boolean isDemoMode;

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
	public GameBoard(final PartitionGame game, final TextureAtlas atlas, final float width, final float height,
			final PlayerConfiguration playerConfiguration, final BoardConfiguration boardConfiguration,
			final boolean isDemoMode) {

		super(width, height);

		// Save reference to the game instance
		this.game = game;

		// Save board configuration
		this.isDemoMode = isDemoMode;

		// Create new game state
		final GameState state = GameState.newGameState(boardConfiguration.boardSpec);

		// Players
		final List<PlayerActor> players = new ArrayList<PlayerActor>(GameState.getNumberOfPlayers(state));
		for (int i = 0; i < GameState.getNumberOfPlayers(state); i++) {
			players.add(new PlayerActor(i, atlas));
		}

		// Tiles
		final Set<TileActor> tileSet = new HashSet<TileActor>();
		final TileActor[][] tiles = new TileActor[GameState.getNumberOfColumns(state)][GameState.getNumberOfRows(state)];
		for (byte c = 0; c < GameState.getNumberOfColumns(state); c++) {
			for (byte r = 0; r < GameState.getNumberOfRows(state); r++) {
				TileActor tile = new TileActor(atlas, c, r);
				tiles[c][r] = tile;
				tileSet.add(tile);
			}
		}

		final Arbiter arbiter = new Arbiter(state, this, players, tileSet);

		// Direct tile input events to the arbiter's input method
		for (final TileActor tile : tileSet) {
			tile.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					arbiter.input(tile);
					return true;
				}
			});
		}

		// Create user interface

		// Determine the area to be occupied by the board
		final Vector2 boardSize = Scaling.fit.apply(GameState.getNumberOfColumns(state),
				GameState.getNumberOfRows(state), width, height);

		// Determine the position of the board
		final Vector2 boardPosition = new Vector2((width - boardSize.x) / 2, (height - boardSize.y) / 2);

		// Determine the size of an individual tile (note: tiles are square!)
		final float tileSize = boardSize.x / GameState.getNumberOfColumns(state);

		for (final TileActor tile : tileSet) {
			// Determine tile coordinates
			final byte[] coords = tile.getCoords();
			final byte x = coords[0];
			final byte y = coords[1];

			// Add tile to the board
			this.addActor(tile);

			// Set tile size
			tile.setSize(tileSize, tileSize);

			// Set tile position
			tile.setPosition(boardPosition.x + tileSize * x, boardPosition.y + boardSize.y - tileSize * (y + 1));
		}

		// Add players to the board
		for (PlayerActor player : players) {
			this.addActor(player);
		}

		arbiter.doReset();
	}

	@Override
	public void doGameOver() {
		// TODO
	}

	@Override
	public void doStalemate() {
		// TODO
	}

	static class PlayerActor extends Actor implements IPlayer {

		private static final String[] playerTextureNames = { "player_red", "player_blue" };

		private final TextureRegion playerTexture;

		private final TextureRegion shadowTexture;

		private final TextureRegion targetTexture;

		private TileActor tile;

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
		public void draw(final SpriteBatch batch, final float parentAlpha) {

			batch.draw(shadowTexture, getX(), getY(), getWidth(), getHeight());
			batch.draw(playerTexture, getX(), getY(), getWidth(), getHeight());
		}

		@Override
		public void doReset(final ITile startingTile) {

			tile = (TileActor) startingTile;

			// Tile must be validly positioned
			this.setPosition(tile.getX(), tile.getY());
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
			return new byte[] { column, row };
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
