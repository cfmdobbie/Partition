package com.maycontainsoftware.partition.gamestate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Representation of game state with a very minimal memory-footprint. Rough calculations indicate that a single state of
 * a two-player game on a 10x10 board will occupy approximately 127 bytes. This becomes useful when needing to hold a
 * history of moves, or needing to traverse possible future moves.
 * 
 * @author Charlie
 */
public class GameState {

	/** Whether a particular tile is enabled. */
	public boolean[][] tileEnabled;

	/** The current player index. */
	public byte currentPlayerIndex;

	/** The current turn phase. */
	private byte turnPhase;

	// Defined turn phases

	/** Constant to represent the move phase. */
	public static final byte PHASE_MOVE = 0x1;

	/** Constant to represent the shoot phase. */
	public static final byte PHASE_SHOOT = 0x2;

	/** The player board-coordinates, as playerCoords[#players][2]. */
	public byte[][] playerCoords;

	/** Deltas to apply to a coordinate to locate surrounding tiles. */
	private static final byte[][] COORDINATE_DELTAS = new byte[][] {
	/* SW */{ -1, -1 },
	/* W */{ -1, 0 },
	/* NW */{ -1, 1 },
	/* N */{ 0, 1 },
	/* NE */{ 1, 1 },
	/* E */{ 1, 0 },
	/* SE */{ 1, -1 },
	/* S */{ 0, -1 }, };

	/**
	 * Generate a new game state from a board layout specified as a String. In a board layout rows are separated by
	 * '\n', all rows must be the same length, enabled tiles are represented by '.', disabled tiles by '#' and players
	 * by a zero-based player index to a maximum of 9.
	 */
	public static GameState newGameState(final String boardLayout) {

		final GameState state = new GameState();

		// Map of players to track players added to board
		final Map<Integer, byte[]> playerCoordinateMap = new HashMap<Integer, byte[]>();

		// Split board layout into rows
		final String[] rows = boardLayout.split("\n");

		// Create array of tiles
		state.tileEnabled = new boolean[rows[0].length()][rows.length];

		// Process board layout
		for (byte r = 0; r < rows.length; r++) {
			for (byte c = 0; c < rows[0].length(); c++) {
				final char ch = rows[r].charAt(c);
				if (ch == '.') {
					// Enabled tile
					state.tileEnabled[c][r] = true;
				} else if (ch == '#') {
					// Disabled tile
					state.tileEnabled[c][r] = false;
				} else if (ch >= '0' && ch <= '9') {
					// Tile is enabled and contains a player
					state.tileEnabled[c][r] = true;
					final int playerIndex = ch - '0';
					playerCoordinateMap.put(playerIndex, new byte[] { c, r });
				} else {
					throw new Error();
				}
			}
		}

		// Arrange players as list
		state.playerCoords = new byte[playerCoordinateMap.size()][];
		for (int i = 0; i < state.playerCoords.length; i++) {
			state.playerCoords[i] = playerCoordinateMap.get(i);
		}

		// Start with player 0
		state.currentPlayerIndex = 0;

		// First action is always to move
		state.turnPhase = PHASE_MOVE;

		return state;
	}

	/**
	 * Return the number of columns on the board.
	 * 
	 * @param state
	 *            The game state.
	 * @return The number of columns.
	 */
	public static int getNumberOfColumns(final GameState state) {
		return state.tileEnabled.length;
	}

	/**
	 * Return the number of rows on the board.
	 * 
	 * @param state
	 *            The game state.
	 * @return The number of rows.
	 */
	public static int getNumberOfRows(final GameState state) {
		return state.tileEnabled[0].length;
	}

	/**
	 * Return the number of players in the game represented by the specified state.
	 * 
	 * @param state
	 *            The game state.
	 * @return The number of players.
	 */
	public static int getNumberOfPlayers(final GameState state) {
		return state.playerCoords.length;
	}

	/**
	 * Return the coordinates of the specified player.
	 * 
	 * @param state
	 *            The game state.
	 * @param playerIndex
	 *            The player index (zero-based).
	 * @return The player coordinates as a two-element byte array.
	 */
	public static byte[] getPlayerCoords(final GameState state, final int playerIndex) {
		return state.playerCoords[playerIndex];
	}

	/**
	 * Whether a tile contains a player.
	 * 
	 * @param state
	 *            The game state.
	 * @param c
	 *            The column of the tile.
	 * @param r
	 *            The row of the tile.
	 * @return False if any player exists on the specified tile, true otherwise.
	 */
	public static boolean tileUnoccupied(final GameState state, final byte c, final byte r) {
		for (final byte[] coords : state.playerCoords) {
			if (coords[0] == c && coords[1] == r) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Whether specified coordinated are valid on the board represented by the specified game state.
	 * 
	 * @param state
	 *            The game state.
	 * @param c
	 *            The column coordinate.
	 * @param r
	 *            The row coordinate.
	 * @return True if the coordinates are valid, false otherwise.
	 */
	public static boolean isValidCoordinates(final GameState state, final byte c, final byte r) {
		final byte numberOfColumns = (byte) state.tileEnabled.length;
		final byte numberOfRows = (byte) state.tileEnabled[0].length;
		return c >= 0 && c < numberOfColumns && r >= 0 && r < numberOfRows;
	}

	/**
	 * All possible single moves from the current state, for the current player as per the game state.
	 * 
	 * @param state
	 *            The game state.
	 * @return A set of two-element byte arrays giving the coordinates of all tiles that either can be moved to or can
	 *         be shot from the current player's position.
	 */
	public static Set<byte[]> getValidMoves(final GameState state) {
		return getValidMoves(state, state.currentPlayerIndex);
	}

	/**
	 * All possible single moves from the current state for the specified player.
	 * 
	 * @param state
	 *            The game state.
	 * @return A set of two-element byte arrays giving the coordinates of all tiles that either can be moved to or can
	 *         be shot from the specified player's position.
	 */
	public static Set<byte[]> getValidMoves(final GameState state, final int playerNumber) {
		// Coordinates of current player
		final byte[] startingCoords = state.playerCoords[playerNumber];
		final byte startingColumn = startingCoords[0];
		final byte startingRow = startingCoords[1];

		// Set in which to store possible moves
		final Set<byte[]> possibleMoves = new CoordinateSet();

		// Apply coordinate deltas in turn to find reachable tiles
		for (final byte[] delta : COORDINATE_DELTAS) {
			// Initial coordinate
			byte c = startingColumn;
			byte r = startingRow;

			while (true) {
				// Apply delta
				c += delta[0];
				r += delta[1];
				// Check for coordinate out of range
				if (!isValidCoordinates(state, c, r)) {
					break;
				}
				// Check for disabled tile
				if (!state.tileEnabled[c][r]) {
					break;
				}
				// Check for occupied tile
				if (!GameState.tileUnoccupied(state, c, r)) {
					break;
				}
				// Otherwise, add and continue
				possibleMoves.add(new byte[] { c, r });
			}
		}

		return possibleMoves;
	}

	/**
	 * Whether or not a move is valid. Some optimisations could be made to this method, for example an initial
	 * sanity-check that the current coordinate and the new coordinate are aligned. It might be faster to check
	 * alignment, determine direction and then walk the board to check intermediate squares rather than determine all
	 * valid moves then check whether the new coordinate is one of them.
	 * 
	 * @param state
	 *            The game state.
	 * @param newCoord
	 *            The new coordinate.
	 * @return True if the new coordinate is a valid move, false otherwise.
	 */
	public static boolean isValidMove(final GameState state, final byte[] newCoord) {
		Set<byte[]> validMoves = getValidMoves(state);
		return validMoves.contains(newCoord);
	}

	/**
	 * A set of all tiles on the board
	 * 
	 * @param state
	 *            The game state.
	 * @return A Set containing two-element bytes arrays holding coordinates of all tiles.
	 */
	public static Set<byte[]> getAllTiles(final GameState state) {

		final Set<byte[]> allTiles = new CoordinateSet();
		for (byte c = 0; c < state.tileEnabled.length; c++) {
			for (byte r = 0; r < state.tileEnabled[c].length; r++) {
				allTiles.add(new byte[] { c, r });
			}
		}
		return allTiles;
	}

	/**
	 * Get the set of all reachable tile coordinates, based on the current player's coordinates.
	 * 
	 * @param state
	 *            The game state.
	 * @return The set of all reachable tile coordinates.
	 */
	public static Set<byte[]> getReachableTiles(final GameState state) {
		return getReachableTiles(state, state.currentPlayerIndex);
	}

	/**
	 * Get the set of all reachable tile coordinates, based on the specified player's coordinates.
	 * 
	 * @param state
	 *            The game state.
	 * @param playerIndex
	 *            The specifed player's index.
	 * @return The set of all reachable tile coordinates.
	 */
	public static Set<byte[]> getReachableTiles(final GameState state, int playerIndex) {

		final Set<byte[]> reachableTiles = new CoordinateSet();
		final byte[] startingCoordinate = state.playerCoords[playerIndex];

		getReachableTiles(state, reachableTiles, startingCoordinate);

		return reachableTiles;
	}

	/**
	 * Internal method for use in finding reachable tiles. This method is heavily recursive.
	 * 
	 * @param state
	 *            The game state.
	 * @param reachableTiles
	 *            The accumulated Set of all reachable tile coordinates.
	 * @param coord
	 *            The next coordinate to search from.
	 */
	private static void getReachableTiles(final GameState state, final Set<byte[]> reachableTiles, final byte[] coord) {

		final byte c = coord[0];
		final byte r = coord[1];

		if (!isValidCoordinates(state, c, r)) {
			return;
		} else if (!state.tileEnabled[c][r]) {
			return;
		} else if (reachableTiles.contains(coord)) {
			return;
		} else {
			reachableTiles.add(coord);
			for (final byte[] delta : COORDINATE_DELTAS) {
				final byte newC = (byte) (c + delta[0]);
				final byte newR = (byte) (r + delta[1]);
				final byte[] newCoord = new byte[] { newC, newR };
				getReachableTiles(state, reachableTiles, newCoord);
			}
		}
	}

	/**
	 * All tiles on the board that are enabled but cannot be reached.
	 * 
	 * @param state
	 *            The game state.
	 * @return A set of two-element byte arrays containing tile coordinates of all tiles that are enabled but cannot be
	 *         reached.
	 */
	public static Set<byte[]> getUnreachableEnabledTiles(final GameState state) {
		// All tiles
		final Set<byte[]> tiles = GameState.getAllTiles(state);
		// Remove reachable tiles
		for (int i = 0; i < getNumberOfPlayers(state); i++) {
			tiles.removeAll(GameState.getReachableTiles(state, i));
		}
		// Collect together only those that are enabled
		final Set<byte[]> enabledTiles = new HashSet<byte[]>();
		for (final byte[] coords : tiles) {
			if (state.tileEnabled[coords[0]][coords[1]]) {
				enabledTiles.add(coords);
			}
		}
		return enabledTiles;
	}

	/**
	 * Duplicate a state.
	 * 
	 * @param state
	 *            The game state.
	 * @return A duplicate of the specified state.
	 */
	public static GameState duplicate(final GameState state) {

		final GameState duplicate = new GameState();

		// Current player - straight copy
		duplicate.currentPlayerIndex = state.currentPlayerIndex;
		// Player coordinates - nested array copy
		duplicate.playerCoords = new byte[state.playerCoords.length][];
		for (int i = 0; i < duplicate.playerCoords.length; i++) {
			duplicate.playerCoords[i] = Arrays.copyOf(state.playerCoords[i], state.playerCoords[i].length);
		}
		// Enabled tiles - nested array copy
		duplicate.tileEnabled = new boolean[state.tileEnabled.length][];
		for (int i = 0; i < duplicate.tileEnabled.length; i++) {
			duplicate.tileEnabled[i] = Arrays.copyOf(state.tileEnabled[i], state.tileEnabled[i].length);
		}
		// Turn phase - straight copy
		duplicate.turnPhase = state.turnPhase;

		return duplicate;
	}

	/**
	 * Apply a move/shoot to a game state.
	 * 
	 * @param state
	 *            The current game state.
	 * @param coord
	 *            The coordinate of the move/shoot, as a byte[2].
	 * @return The new game state.
	 */
	public static GameState apply(final GameState state, final byte[] coord) {
		final byte c = coord[0];
		final byte r = coord[1];
		if (!isValidCoordinates(state, c, r)) {
			throw new Error();
		} else if (!isValidMove(state, coord)) {
			throw new Error();
		} else {
			final GameState newState = duplicate(state);

			switch (state.turnPhase) {
			case PHASE_MOVE:
				newState.playerCoords[newState.currentPlayerIndex][0] = c;
				newState.playerCoords[newState.currentPlayerIndex][1] = r;
				newState.turnPhase = PHASE_SHOOT;
				break;
			case PHASE_SHOOT:
				newState.tileEnabled[c][r] = false;
				newState.turnPhase = PHASE_MOVE;
				// Next player
				newState.currentPlayerIndex++;
				newState.currentPlayerIndex %= getNumberOfPlayers(newState);
				break;
			default:
				throw new Error();
			}

			return newState;
		}
	}

	/**
	 * Whether or not the game is over. The game is declared to be over when every player is isolated from every other
	 * player.
	 * 
	 * A special case exists for one-player games - with one player there is no other player to be isolated from, so the
	 * game can never be declared over. (Note that the stalemate condition still exists when that one player has no
	 * moves left.)
	 * 
	 * @param state
	 *            The game state.
	 * @return True if the game is over, false otherwise.
	 */
	public static boolean isGameOver(final GameState state) {

		final int numberOfPlayers = getNumberOfPlayers(state);

		if (numberOfPlayers == 1) {
			// One-player games are never "over"
			return false;
		}

		for (int player = 0; player < numberOfPlayers - 1; player++) {
			final Set<byte[]> reachableTiles = getReachableTiles(state, player);
			for (int otherPlayer = player + 1; otherPlayer < numberOfPlayers; otherPlayer++) {
				final byte[] otherCoord = getPlayerCoords(state, otherPlayer);
				if (reachableTiles.contains(otherCoord)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Whether or not the game is a draw. The game is a draw if multiple players share the top score.
	 * 
	 * @param state
	 *            The game state.
	 * @return True if the game is a draw, false otherwise.
	 * @throws IllegalStateException
	 *             if the game is not over.
	 */
	public static boolean isDraw(final GameState state) {

		if (!isGameOver(state)) {
			// Not valid to call this method if the game isn't over!
			throw new IllegalStateException("GameState::isDraw;!isGameOver");
		}

		boolean draw = false;
		int mostReachable = 0;

		for (int p = 0; p < getNumberOfPlayers(state); p++) {
			int numberReachable = getReachableTiles(state, p).size();

			if (numberReachable > mostReachable) {
				// This is the new top score
				mostReachable = numberReachable;
				draw = false;
			} else if (numberReachable == mostReachable) {
				// Top score is now a draw
				draw = true;
			}
		}

		return draw;
	}

	/**
	 * Whether or not the game is an outright win. The game is an outright win if the top score is not shared between
	 * two or more players.
	 * 
	 * @param state
	 *            The game state.
	 * @return True if the game is an outright win, false otherwise.
	 * @throws IllegalStateException
	 *             if the game is not over.
	 */
	public static boolean isOutrightWin(final GameState state) {
		return !isDraw(state);
	}

	/**
	 * Get the winning player. This is only valid to call when the game is over, and did not end in a draw.
	 * 
	 * @param state
	 *            The game state.
	 * @return The winning player's player number.
	 * @throws IllegalStateException
	 *             if the game is not over or was not an outright win.
	 */
	public static int getWinningPlayer(final GameState state) {
		if (!isOutrightWin(state)) {
			// Not valid to call this method if there isn't an outright winner
			throw new IllegalStateException("GameState::getWinningPlayer;!isOutrightWin");
		}

		int mostReachable = 0;
		int winningPlayer = -1;

		for (int p = 0; p < getNumberOfPlayers(state); p++) {
			int numberReachable = getReachableTiles(state, p).size();

			if (numberReachable > mostReachable) {
				// This is the new top score
				mostReachable = numberReachable;
				winningPlayer = p;
			}
		}

		return winningPlayer;
	}

	/**
	 * Whether or not game has become a stalemate. The game is declared to be a stalemate if at any point no move can be
	 * played. This will need to be reconsidered with >2 players, as one isolated player running out of space should not
	 * cause other players to stalemate. Game over condition takes priority over stalemate - if a player has no valid
	 * moves but all players are isolated anyway, game is over and not considered a stalemate.
	 * 
	 * @param state
	 *            The game state.
	 * @return True if the game is a stalemate, false otherwise.
	 */
	public static boolean isStalemate(final GameState state) {
		return !isGameOver(state) && getValidMoves(state).size() == 0;
	}

	/**
	 * A Set that contains arrays of bytes and respects array equality rather than array referential equality, which
	 * means arrays containing the same elements (in the same order!) are considered identical regardless of whether the
	 * array references are pointing to the same objects.
	 * 
	 * @author Charlie
	 */
	static class CoordinateSet extends TreeSet<byte[]> {
		/** Default serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** Default constructor. */
		public CoordinateSet() {
			super(new Comparator<byte[]>() {
				@Override
				public int compare(byte[] o1, byte[] o2) {
					return Arrays.equals(o1, o2) ? 0 : Arrays.hashCode(o1) - Arrays.hashCode(o2);
				}
			});
		}
	}

	/**
	 * Returns the current turn phase.
	 * 
	 * @param state
	 *            The game state.
	 * @return Either PHASE_MOVE or PHASE_SHOOT.
	 */
	public static byte getTurnPhase(final GameState state) {
		return state.turnPhase;
	}
}
