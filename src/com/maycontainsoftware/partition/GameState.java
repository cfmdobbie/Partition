package com.maycontainsoftware.partition;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GameState {

	// Whether a particular tile is enabled
	boolean[][] tileEnabled;

	// The current player index
	byte currentPlayerIndex;

	// The current turn phase
	byte turnPhase = PHASE_MOVE;

	// Defined turn phases
	final static byte PHASE_MOVE = 0x1;
	final static byte PHASE_SHOOT = 0x2;

	// The player board-coordinates, as playerCoords[#players][2]
	byte[][] playerCoords;

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

		return state;
	}

	public static int getNumberOfPlayers(final GameState state) {
		return state.playerCoords.length;
	}

	public static byte[] getPlayerCoords(final GameState state, final byte playerIndex) {
		return state.playerCoords[playerIndex];
	}

	public static boolean tileUnoccupied(final GameState state, final byte c, final byte r) {
		for (final byte[] coords : state.playerCoords) {
			if (coords[0] == c && coords[1] == r) {
				return false;
			}
		}
		return true;
	}

	public static Set<byte[]> getPossibleMoves(final GameState state) {

		// Coordinates of current player
		final byte[] startingCoords = state.playerCoords[state.currentPlayerIndex];
		final byte startingColumn = startingCoords[0];
		final byte startingRow = startingCoords[1];
		final byte numberOfColumns = (byte) state.tileEnabled.length;
		final byte numberOfRows = (byte) state.tileEnabled[0].length;

		// Set in which to store possible moves
		final Set<byte[]> possibleMoves = new TreeSet<byte[]>(new Comparator<byte[]>() {
			@Override
			public int compare(byte[] o1, byte[] o2) {
				return Arrays.equals(o1, o2) ? 0 : Arrays.hashCode(o1) - Arrays.hashCode(o2);
			}
		});

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

	// getReachableTiles
	// getUnreachableTiles
	// applyMoveSpec?

}
