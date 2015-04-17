package com.maycontainsoftware.partition;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The Arbiter is the class that manages the logical flow of the game. It is responsible for remembering what turn state
 * the game is in, holding the current logical state, accepting input and feedback from the interactive part of the
 * application, and informing logical game elements of events that have occurred that they may be interested in.
 * 
 * @author Charlie
 */
public class Arbiter<T extends ITile, P extends IPlayer<T>> {

	/** The different turn states the game can be in. */
	private static enum GameTurnState {
		PENDING_MOVE,
		MOVING,
		PENDING_SHOOT,
		SHOOTING,
		WIN_CHECK,
		SWITCHING_PLAYERS,
		STALEMATE_CHECK,
		WON,
		STALEMATE,
	}

	/** The current game turn state. */
	private GameTurnState turnState;

	/** The initial logical game state. */
	private final GameState initialGameState;

	/** The current logical game state. */
	private GameState state;

	/** A reference to the logical board component. */
	private final IBoard board;

	/** The logical player components. */
	private final List<P> players;

	/** The logical tile components. */
	private final Set<T> tiles;

	/** The currently active player. */
	private int activePlayerNumber;

	/**
	 * Create a new Arbiter.
	 * 
	 * @param initialGameState
	 *            The initial game state; the state to which we must return if the game is reset.
	 * @param board
	 *            The logical board component.
	 * @param players
	 *            The logical player components.
	 * @param tiles
	 *            The logical tile components.
	 */
	public Arbiter(final GameState initialGameState, final IBoard board, final List<P> players,
			final Set<T> tiles) {

		// Always start waiting for the first move
		this.turnState = GameTurnState.PENDING_MOVE;

		// Remember the initial game state - will need to use it to reset the game
		this.initialGameState = initialGameState;

		// Current state is a duplicate of the initial game state
		this.state = GameState.duplicate(initialGameState);

		// Store references to the other participants in the logical game process
		this.board = board;
		this.players = players;
		this.tiles = tiles;

		// Retrieve the current player from the game state
		this.activePlayerNumber = state.currentPlayerIndex;
	}

	/** Accept a selection event on a tile. */
	public void input(final T tile) {
		switch (turnState) {
		case PENDING_MOVE:
			// We were waiting for a move
			if (GameState.isValidMove(state, tile.getCoords())) {
				// Move is valid
				// Apply the action to get a new game state
				state = GameState.apply(state, tile.getCoords());

				// Update the current logical game turn phase
				turnState = GameTurnState.MOVING;

				// Tell the player that it should move
				players.get(activePlayerNumber).doMove(tile, this);
			} else {
				// Not a valid action - tell ITile
				tile.doError();
			}
			break;
		case PENDING_SHOOT:
			// We were waiting for a shoot
			if (GameState.isValidMove(state, tile.getCoords())) {
				// Apply the action to get a new game state
				state = GameState.apply(state, tile.getCoords());

				// Update the current logical game turn phase
				turnState = GameTurnState.SHOOTING;

				// Tell the player that it should shoot
				players.get(activePlayerNumber).doShoot(tile, this);

				// Tell the tile that it has been shot
				tile.doShoot(this);
			} else {
				// Not a valid action - tell ITile
				tile.doError();
			}
			break;
		case MOVING:
		case SHOOTING:
		case STALEMATE_CHECK:
		case SWITCHING_PLAYERS:
		case WIN_CHECK:
		case WON:
		case STALEMATE:
		default:
			// Not a valid phase for accepting user input on a tile - ignore
			break;
		}
	}

	/** Receive notification that a move event has been completed by the application components. */
	public void moveDone() {
		// Check that we were moving
		assert (turnState == GameTurnState.MOVING);

		// Now waiting for a decision on which tile to shoot
		turnState = GameTurnState.PENDING_SHOOT;

		// Tell the player that it is now pending a shoot
		players.get(activePlayerNumber).doPendingShoot();
	}

	/** Receive notification that a shoot event has been completed by the application components. */
	public void shootDone() {
		// Check that were were shooting
		assert (turnState == GameTurnState.SHOOTING);

		// Check for a win
		turnState = GameTurnState.WIN_CHECK;

		if (GameState.isGameOver(state)) {
			// Someone has won - tell the board
			board.doGameOver();
			// And update the turn state
			turnState = GameTurnState.WON;
		} else {
			// Nobody has won, continue

			// Now switching players
			turnState = GameTurnState.SWITCHING_PLAYERS;

			// Get the new player number from the game state
			activePlayerNumber = state.currentPlayerIndex;

			// Now need to check for a stalemate
			turnState = GameTurnState.STALEMATE_CHECK;

			if (GameState.isStalemate(state)) {
				// Game is a stalemate - tell the board
				board.doStalemate();
				// And update the turn state
				turnState = GameTurnState.STALEMATE;
			} else {
				// Continue to state of pending a decision on which tile to move to
				turnState = GameTurnState.PENDING_MOVE;
				// Tell the player it is now pending a move
				players.get(activePlayerNumber).doPendingMove();
			}
		}
	}

	/** Reset the game to the original state. */
	public void doReset() {

		// Reset to the initial game state
		state = GameState.duplicate(initialGameState);
		// Start on the initial game turn state - pending the first move
		turnState = GameTurnState.PENDING_MOVE;

		for (final T tile : tiles) {
			// Determine the tile coordinates
			final byte[] coords = tile.getCoords();
			// Determine whether the tile should be enabled or disabled
			final boolean enabled = state.tileEnabled[coords[0]][coords[1]];
			// Reset the tile
			tile.doReset(enabled);
		}

		for (int i = 0; i < players.size(); i++) {
			// Determine the player coordinates
			final byte[] coords = state.playerCoords[i];
			// Locate the logical tile that the player is on
			final T tile = findTileByCoords(coords);
			// Reset the player
			players.get(i).doReset(tile);
		}
	}

	/**
	 * Find the logical tile component that relates to the given coordinates.
	 * 
	 * @param coords
	 *            The given coordinates.
	 * @return The logical tile component with the given coordinates, or null if no such tile exists.
	 */
	private T findTileByCoords(final byte[] coords) {
		for (final T tile : tiles) {
			if (Arrays.equals(tile.getCoords(), coords)) {
				return tile;
			}
		}
		return null;
	}
}
