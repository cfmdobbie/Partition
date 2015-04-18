package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Scaling;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * A representation of a game board.
 * 
 * @author Charlie
 */
public class GameBoard extends FixedSizeWidgetGroup implements IBoard {

	/** Tag for logging purposes. */
	private static final String TAG = GameBoard.class.getName();

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

		Gdx.app.debug(TAG, "doGameOver()");

		if (isDemoMode) {

			this.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.1f), Actions.delay(0.5f),
					new Action() {
						@Override
						public boolean act(float delta) {
							arbiter.doReset();
							return true;
						}
					}, Actions.fadeIn(0.1f)));

		} else {
			// TODO: Game-over message, reset button
		}
	}

	@Override
	public void doStalemate() {

		Gdx.app.debug(TAG, "doStalemate()");

		if (isDemoMode) {

			this.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.1f), Actions.delay(0.5f),
					new Action() {
						@Override
						public boolean act(float delta) {
							arbiter.doReset();
							return true;
						}
					}, Actions.fadeIn(0.1f)));

		} else {
			// TODO: Stalemate message, reset button
		}
	}
}
