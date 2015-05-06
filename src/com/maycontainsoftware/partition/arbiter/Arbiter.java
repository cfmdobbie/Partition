package com.maycontainsoftware.partition.arbiter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maycontainsoftware.partition.PlayerConfiguration;
import com.maycontainsoftware.partition.gamestate.GameState;
import com.maycontainsoftware.partition.gamestate.IAI;
import com.maycontainsoftware.partition.gamestate.RandomAI2;

/**
 * The Arbiter is the class that manages the logical flow of the game. It is responsible for remembering what turn state
 * the game is in, holding the current logical state, accepting input and feedback from the interactive part of the
 * application, and informing logical game elements of events that have occurred that they may be interested in.
 * 
 * @author Charlie
 */
public class Arbiter {

	/** Logging flag to control direct sysout logging. */
	private static final boolean DEBUG_LOG = false;

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
	private final List<? extends IPlayer> players;

	/** The logical tile components. */
	private final Set<? extends ITile> tiles;

	/** The player configuration. */
	final PlayerConfiguration playerConfiguration;

	/** The computer AI players. */
	private final Map<Integer, IAI> ai;

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
	public Arbiter(final GameState initialGameState, final IBoard board, final List<? extends IPlayer> players,
			final Set<? extends ITile> tiles, final PlayerConfiguration playerConfiguration) {

		// Remember the initial game state - will need to use it to reset the game
		this.initialGameState = initialGameState;

		// Remember the player configuration
		this.playerConfiguration = playerConfiguration;

		// Create computer AI players
		ai = new HashMap<Integer, IAI>();
		for (int i = 0; i < playerConfiguration.getNumberOfPlayers(); i++) {
			if (playerConfiguration.isComputerPlayer(i)) {
				ai.put(i, new RandomAI2(i));
			}
		}

		if (DEBUG_LOG) {
			System.out.println("<init>, initialGameState:");
			// Player 0 coordinates
			final byte[] coords = initialGameState.playerCoords[0];
			System.out.println("Player 0 coords: [" + coords[0] + "," + coords[1] + "]");
		}

		// Store references to the other participants in the logical game process
		this.board = board;
		this.players = players;
		this.tiles = tiles;
	}

	/** Accept a selection event on a tile. */
	public void input(final ITile tile) {

		if (playerConfiguration.isComputerPlayer(activePlayerNumber)) {
			if (DEBUG_LOG) {
				System.out.println("Arbiter::input;not_player_turn");
			}
			return;
		}

		switch (turnState) {
		case PENDING_MOVE:
			// We were waiting for a move
			doMove(tile);
			break;
		case PENDING_SHOOT:
			// We were waiting for a shoot
			doShoot(tile);
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

	private void doMove(final ITile tile) {
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
	}

	private void doShoot(final ITile tile) {
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
	}

	/** Receive notification that a move event has been completed by the application components. */
	public void moveDone() {

		// Check that we were moving
		if (turnState != GameTurnState.MOVING) {
			throw new IllegalStateException("Arbiter::moveDone;incorrect_turnState:" + turnState);
		}

		// Now waiting for a decision on which tile to shoot
		turnState = GameTurnState.PENDING_SHOOT;

		if (playerConfiguration.isComputerPlayer(activePlayerNumber)) {
			players.get(activePlayerNumber).doAiPendingShoot(this);
		} else {
			// Tell the player that it is now pending a shoot
			players.get(activePlayerNumber).doPendingShoot();
		}
	}

	/** Receive notification that a shoot event has been completed by the application components. */
	public void shootDone() {

		// Check that were were shooting
		if (turnState != GameTurnState.SHOOTING) {
			throw new IllegalStateException("Arbiter::shootDone;incorrect_turnState:" + turnState);
		}

		// Check for a win
		turnState = GameTurnState.WIN_CHECK;

		if (GameState.isGameOver(state)) {
			// Update the turn state
			turnState = GameTurnState.WON;

			// Determine which tiles are unreachable
			final Set<ITile> unreachable = getUnreachableEnabledTiles();

			// Calculate the player territories
			final Map<IPlayer, Set<ITile>> playerTerritories = getPlayerTerritories();

			// Need to determine whether there was an outright winner, or a draw between one or more players
			// Determine the winners from the claimed territories
			final Set<IPlayer> winners = getWinningPlayers(playerTerritories);

			// Tell the board
			if (winners.size() == 1) {
				// One winner - it's an outright win
				board.doWin(winners.toArray(new IPlayer[] {})[0], playerTerritories, unreachable);
			} else {
				// Multiple winners - it's a draw
				board.doDraw(winners, playerTerritories, unreachable);
			}
		} else {
			// Nobody has won, continue

			// Now switching players
			turnState = GameTurnState.SWITCHING_PLAYERS;

			// Get the new player number from the game state
			activePlayerNumber = state.currentPlayerIndex;

			// Now need to check for a stalemate
			turnState = GameTurnState.STALEMATE_CHECK;

			if (GameState.isStalemate(state)) {

				// Update the turn state
				turnState = GameTurnState.STALEMATE;

				// Determine which tiles are unreachable
				final Set<ITile> unreachable = getUnreachableEnabledTiles();

				// Tell the board
				board.doStalemate(unreachable);
			} else {
				// Continue to state of pending a decision on which tile to move to
				turnState = GameTurnState.PENDING_MOVE;

				if (playerConfiguration.isComputerPlayer(activePlayerNumber)) {
					players.get(activePlayerNumber).doAiPendingMove(this);
				} else {
					// Tell the player it is now pending a move
					players.get(activePlayerNumber).doPendingMove();
				}
			}
		}
	}

	/** Receive notification that the AI player should proceed with its move. */
	public void aiProceedWithMove() {
		if (!playerConfiguration.isComputerPlayer(activePlayerNumber)) {
			throw new IllegalStateException("Arbiter::aiProceedWithMove;not_computer_turn:" + activePlayerNumber);
		}

		if (turnState != GameTurnState.PENDING_MOVE) {
			throw new IllegalStateException("Arbiter::aiProceedWithMove;not_pending_move");
		}

		// Locate AI player
		final IAI ai = this.ai.get(activePlayerNumber);

		if (ai == null) {
			throw new IllegalStateException("Arbiter::aiProceedWithMove;no_ai_exists");
		}

		final byte[] coords = ai.doMove(state);
		final ITile tile = findTileByCoords(coords);
		doMove(tile);
	}

	/** Receive notification that the AI player should proceed with its shoot. */
	public void aiProceedWithShoot() {
		if (!playerConfiguration.isComputerPlayer(activePlayerNumber)) {
			throw new IllegalStateException("Arbiter::aiProceedWithShoot;not_computer_turn:" + activePlayerNumber);
		}

		if (turnState != GameTurnState.PENDING_SHOOT) {
			throw new IllegalStateException("Arbiter::aiProceedWithShoot;not_pending_shoot");
		}

		// Locate AI player
		final IAI ai = this.ai.get(activePlayerNumber);

		if (ai == null) {
			throw new IllegalStateException("Arbiter::aiProceedWithShoot;no_ai_exists");
		}

		final byte[] coords = ai.doShoot(state);
		final ITile tile = findTileByCoords(coords);
		doShoot(tile);
	}

	/** Reset the game to the original state. */
	public void doReset() {

		if (DEBUG_LOG) {
			System.out.println("doReset, initialGameState:");
			System.out.println("Player 0 coords: [" + initialGameState.playerCoords[0][0] + ","
					+ initialGameState.playerCoords[0][1] + "]");

			if (state != null) {
				System.out.println("doReset, current state:");
				System.out.println("Player 0 coords: [" + state.playerCoords[0][0] + "," + state.playerCoords[0][1]
						+ "]");
			}
		}

		// Reset to the initial game state
		state = GameState.duplicate(initialGameState);

		// Always start waiting for the first move
		this.turnState = GameTurnState.PENDING_MOVE;

		// Retrieve the current player from the game state
		this.activePlayerNumber = state.currentPlayerIndex;

		for (final ITile tile : tiles) {

			// Determine the tile coordinates
			final byte[] coords = tile.getCoords();

			// Determine whether the tile should be enabled or disabled
			final boolean enabled = state.tileEnabled[coords[0]][coords[1]];

			if (DEBUG_LOG) {
				System.out.println("doReset, resetting tile [" + coords[0] + "," + coords[1] + "]");
			}

			// Reset the tile
			tile.doReset(enabled);
		}

		for (int i = 0; i < players.size(); i++) {

			// Determine the player coordinates
			final byte[] coords = state.playerCoords[i];

			if (DEBUG_LOG) {
				System.out.println("doReset, player " + i + " coords: " + coords[0] + "," + coords[1]);
			}

			// Locate the logical tile that the player is on
			final ITile tile = findTileByCoords(coords);
			// Reset the player
			players.get(i).doReset(tile);
		}

		if (playerConfiguration.isComputerPlayer(activePlayerNumber)) {
			players.get(activePlayerNumber).doAiPendingMove(this);
		} else {
			// Notify active player that it is their turn
			players.get(activePlayerNumber).doPendingMove();
		}
	}

	/**
	 * Get all enabled unreachable tiles. This method is intended to be used at the end of a game to notify the IBoard
	 * of the final state, but is valid to be called at any time.
	 * 
	 * @return A Set of all unreachable ITiles
	 */
	private Set<ITile> getUnreachableEnabledTiles() {
		final Set<ITile> unreachable = new HashSet<ITile>();
		final Set<byte[]> stateUnreachable = GameState.getUnreachableEnabledTiles(state);
		for (final byte[] coords : stateUnreachable) {
			unreachable.add(findTileByCoords(coords));
		}
		return unreachable;
	}

	/**
	 * Get final player territories. This method is only valid to call at the end of a game, and only when either a
	 * single player has won, or a draw condition exists - i.e. not in the case of a stalemate.
	 * 
	 * @return A map of IPlayers to their respective territories, in the form of a Set of ITiles.
	 */
	private Map<IPlayer, Set<ITile>> getPlayerTerritories() {
		// Territory
		final Map<IPlayer, Set<ITile>> territory = new HashMap<IPlayer, Set<ITile>>();
		for (int i = 0; i < GameState.getNumberOfPlayers(state); i++) {
			final IPlayer player = players.get(i);
			final Set<ITile> reachable = new HashSet<ITile>();
			final Set<byte[]> stateReachable = GameState.getReachableTiles(state, i);
			for (final byte[] coords : stateReachable) {
				reachable.add(findTileByCoords(coords));
			}
			territory.put(player, reachable);
		}
		return territory;
	}

	/**
	 * Determine the winning players. This may be a single player in the case of an outright win, or multiple players in
	 * the case of a draw.
	 * 
	 * @param territory
	 *            The current player territories.
	 * @return A list of the winning players.
	 */
	private Set<IPlayer> getWinningPlayers(final Map<IPlayer, Set<ITile>> territory) {
		// Collect together the player scores
		final Map<IPlayer, Integer> playerScores = new HashMap<IPlayer, Integer>(territory.size());
		for (final IPlayer player : territory.keySet()) {
			playerScores.put(player, territory.get(player).size());
		}
		// Determine the top score
		int topScore = 0;
		for (final IPlayer player : playerScores.keySet()) {
			if (topScore < playerScores.get(player)) {
				topScore = playerScores.get(player);
			}
		}
		// Determine all the top-scoring players
		final Set<IPlayer> winners = new HashSet<IPlayer>(1);
		for (final IPlayer player : playerScores.keySet()) {
			if (playerScores.get(player) == topScore) {
				winners.add(player);
			}
		}

		return winners;
	}

	/**
	 * Find the logical tile component that relates to the given coordinates.
	 * 
	 * @param coords
	 *            The given coordinates.
	 * @return The logical tile component with the given coordinates, or null if no such tile exists.
	 */
	private ITile findTileByCoords(final byte[] coords) {
		for (final ITile tile : tiles) {
			if (Arrays.equals(tile.getCoords(), coords)) {
				return tile;
			}
		}
		return null;
	}
}
