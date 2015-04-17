package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * A representation of a game board.
 * 
 * @author Charlie
 */
public class GameBoard extends FixedSizeWidgetGroup implements IBoard {

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Whether or not the game board is in "demo" mode. */
	private final boolean isDemoMode;

	/** The Arbiter that controls the game board. */
	private final Arbiter arbiter;

	/**
	 * Construct a new GameBoard.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param atlas
	 *            The TextureAtlas containing all graphics required to draw the board.
	 * @param width
	 *            The (fixed) board width.
	 * @param height
	 *            The (fixed) board height.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 * @param isDemoMode
	 *            Whether or not the game is running in demo mode, a mode which is intended for use on the main menu and
	 *            on the instructions screen.
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
		final Set<TileActor> tiles = new HashSet<TileActor>();
		for (byte c = 0; c < GameState.getNumberOfColumns(state); c++) {
			for (byte r = 0; r < GameState.getNumberOfRows(state); r++) {
				TileActor tile = new TileActor(atlas, c, r);
				tiles.add(tile);
			}
		}

		// Create the Arbiter
		arbiter = new Arbiter(state, this, players, tiles);

		// Direct tile input events to the arbiter's input method
		for (final TileActor tile : tiles) {
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

		for (final TileActor tile : tiles) {
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

		// Reset the arbiter to set the game to its initial state
		arbiter.doReset();
	}

	@Override
	public void doGameOver() {
		if (isDemoMode) {
			arbiter.doReset();
		} else {
			// TODO: Game-over message, reset button
		}
	}

	@Override
	public void doStalemate() {
		if (isDemoMode) {
			arbiter.doReset();
		} else {
			// TODO: Stalemate message, reset button
		}
	}

	/**
	 * Actor to represent a player component in the arbiter structure.
	 * 
	 * @author Charlie
	 */
	static class PlayerActor extends Group implements IPlayer {

		/** Names of the player token textures. */
		private static final String[] playerTextureNames = { "player_red", "player_blue" };

		/** Actor to represent the shadow. */
		private final Actor shadow;

		/** Actor to represent the player token. */
		private final Actor player;

		/** Actor to represent the target. */
		private final Actor target;

		/**
		 * Construct a new PlayerActor.
		 * 
		 * @param id
		 *            The player id.
		 * @param atlas
		 *            The main app TextureAtlas.
		 */
		public PlayerActor(final int id, final TextureAtlas atlas) {

			shadow = new Image(atlas.findRegion("shadow"));
			addActor(shadow);
			player = new Image(atlas.findRegion(playerTextureNames[id]));
			addActor(player);
			target = new Image(atlas.findRegion("target"));
			addActor(target);
		}

		@Override
		public void doPendingMove() {

			// Player token bounces up and down
			player.addAction(Actions.forever(Actions.sequence(
					Actions.moveBy(0.0f, getHeight() / 3, 0.5f, Interpolation.sine),
					Actions.moveBy(0.0f, -getHeight() / 3, 0.5f, Interpolation.sine))));
		}

		@Override
		public void doMove(final ITile targetTile, final Arbiter arbiter) {

			// Slide static player token to new location

			final TileActor tile = (TileActor) targetTile;
			player.clearActions();
			player.setPosition(0.0f, 0.0f);

			this.addAction(Actions.sequence(Actions.moveTo(tile.getX(), tile.getY(), 0.2f, Interpolation.sine),
					new Action() {
						@Override
						public boolean act(float delta) {
							arbiter.moveDone();
							return true;
						}
					}));
		}

		@Override
		public void doPendingShoot() {

			// Bouncing target graphic
			target.setColor(Color.WHITE);
			target.setPosition(getWidth() / 6, 0.0f);
			target.addAction(Actions.forever(Actions.sequence(
					Actions.moveBy(0.0f, getHeight() / 2, 0.5f, Interpolation.sine),
					Actions.moveBy(0.0f, -getHeight() / 2, 0.5f, Interpolation.sine))));
		}

		@Override
		public void doShoot(final ITile targetTile, final Arbiter arbiter) {

			// Stop bouncing target
			target.clearActions();
			target.setPosition(0.0f, 0.0f);
			target.setColor(Color.CLEAR);
		}

		@Override
		public void doReset(final ITile startingTile) {

			final TileActor tile = (TileActor) startingTile;

			// Tile must be validly positioned and sized
			this.setPosition(tile.getX(), tile.getY());
			this.setSize(tile.getWidth(), tile.getHeight());

			// Reset shadow
			shadow.setSize(getWidth(), getHeight());

			// Reset player
			player.setSize(getWidth(), getHeight());
			player.clearActions();
			player.setPosition(0.0f, 0.0f);

			// Reset target
			target.setSize(getWidth(), getHeight());
			target.clearActions();
			target.setColor(Color.CLEAR);
			target.setPosition(0.0f, 0.0f);
		}
	}

	/**
	 * Actor to represent a tile component in the arbiter structure.
	 * 
	 * @author Charlie
	 */
	static class TileActor extends Group implements ITile {

		/** Actor to represent the actual tile. */
		private final Actor tile;

		/** Actor to represent the graphical error notification. */
		private final Actor error;

		/** Column number. */
		private final byte column;

		/** Row number. */
		private final byte row;

		/**
		 * Construct a new TileActor.
		 * 
		 * @param atlas
		 *            The main app TextureAtlas.
		 * @param column
		 *            This tile's column.
		 * @param row
		 *            This tile's row.
		 */
		public TileActor(final TextureAtlas atlas, final byte column, final byte row) {

			// The actual tile
			tile = new Image(atlas.findRegion("tile"));
			this.addActor(tile);

			// Graphical error notification
			error = new Image(atlas.findRegion("tile_error"));
			this.addActor(error);

			this.column = column;
			this.row = row;
		}

		@Override
		protected void sizeChanged() {

			// Set sizes of child actors
			tile.setSize(getWidth(), getHeight());
			error.setSize(getWidth(), getHeight());

			super.sizeChanged();
		}

		@Override
		public byte[] getCoords() {
			return new byte[] { column, row };
		}

		@Override
		public void doError() {
			// Flash up error notification
			error.addAction(Actions.sequence(Actions.color(Color.WHITE), Actions.color(Color.CLEAR, 0.5f)));
		}

		@Override
		public void doShoot(final Arbiter arbiter) {
			// Fade tile out
			tile.addAction(Actions.sequence(Actions.color(Color.WHITE), Actions.color(Color.CLEAR, 0.15f),
					new Action() {
						@Override
						public boolean act(float delta) {
							arbiter.shootDone();
							return true;
						}
					}));
		}

		@Override
		public void doReset(final boolean enabled) {
			tile.setColor(enabled ? Color.WHITE : Color.CLEAR);
			error.setColor(Color.CLEAR);
		}
	}
}
