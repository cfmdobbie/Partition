package com.maycontainsoftware.partition;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Representation of game state with a very minimal memory-footprint. Rough calculations indicate that a two-player game
 * on a 10x10 board will occupy approximately 127 bytes.
 * 
 * @author Charlie
 */
public class GameState {

	// Whether a particular tile is enabled
	boolean[][] tileEnabled;

	// The current player index
	byte currentPlayerIndex;

	// The current turn phase
	byte turnPhase;

	// Defined turn phases
	final static byte PHASE_MOVE = 0x1;
	final static byte PHASE_SHOOT = 0x2;

	// The player board-coordinates, as playerCoords[#players][2]
	byte[][] playerCoords;

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
	public static byte[] getPlayerCoords(final GameState state, final byte playerIndex) {
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
	 * All possible single moves from the current state. Note that the player to move is extracted from the game state.
	 * 
	 * @param state
	 *            The game state.
	 * @return A set of two-element byte arrays giving the coordinates of all tiles that either can be moved to or can
	 *         be shot from the current player's position.
	 */
	public static Set<byte[]> getValidMoves(final GameState state) {

		// Coordinates of current player
		final byte[] startingCoords = state.playerCoords[state.currentPlayerIndex];
		final byte startingColumn = startingCoords[0];
		final byte startingRow = startingCoords[1];
		final byte numberOfColumns = (byte) state.tileEnabled.length;
		final byte numberOfRows = (byte) state.tileEnabled[0].length;

		// Set in which to store possible moves
		final Set<byte[]> possibleMoves = new CoordinateSet();

		// Apply coordinate deltas in turn to find reachable tiles
		final byte[][] coordinateDeltas = new byte[][] {
		/* SW */{ -1, -1 },
		/* W */{ -1, 0 },
		/* NW */{ -1, 1 },
		/* N */{ 0, 1 },
		/* NE */{ 1, 1 },
		/* E */{ 1, 0 },
		/* SE */{ 1, -1 },
		/* S */{ 0, -1 }, };
		for (final byte[] delta : coordinateDeltas) {
			// Initial coordinate
			byte c = startingColumn;
			byte r = startingRow;

			while (true) {
				// Apply delta
				c += delta[0];
				r += delta[1];
				// Check for coordinate out of range
				if (c < 0 || c >= numberOfColumns || r < 0 || r >= numberOfRows) {
					break;
				}
				// Check for disabled tile
				if (!state.tileEnabled[c][r]) {
					break;
				}
				// Otherwise, only add if tile unoccupied
				if (GameState.tileUnoccupied(state, c, r)) {
					possibleMoves.add(new byte[] { c, r });
				}
			}
		}

		return possibleMoves;
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

	public static Set<byte[]> getReachableTiles(final GameState state) {
		@SuppressWarnings("unused")
		final Set<byte[]> reachableTiles = new CoordinateSet();

		throw new Error();
		// TODO: Implement getReachableTiles()
	}

	/**
	 * All tiles on the board that cannot be reached.
	 * 
	 * @param state
	 *            The game state.
	 * @return A set of two-element byte arrays containing tile coordinates of all tiles that cannot be reached.
	 */
	public static Set<byte[]> getUnreachableTiles(final GameState state) {
		final Set<byte[]> tiles = GameState.getAllTiles(state);
		tiles.removeAll(GameState.getReachableTiles(state));
		return tiles;
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

	// applyMoveSpec?
}
