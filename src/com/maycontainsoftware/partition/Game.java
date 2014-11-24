package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents an aborted attempt to design a data model for the game. These comments were added shortly
 * before the class was removed from the project, and serve only as a history in version control as to why this model
 * was dropped.
 * 
 * The initial plan was for a very verbose, easy to read data model, with separate classes for the game, the board, each
 * tile on the board, and for each player. The below is the state of the data model mid-development when it was decided
 * that this was not an appropriate approach for the problem. The primary issues are that this kind of model is not
 * suitable for implementing either an undo stack, or for any kind of goal-based AI algorithm. The first of these
 * requires a game state that can be easily stored and restored, while the second requires traversing many different
 * possible future states and caching or discarding trees of game states without affecting the current game.
 * 
 * @author Charlie
 */
public class Game {

	// List of players in the game
	private final List<Player> players;

	// Reference to board object
	private final Board board;

	// Construct a new game using the specified board layout
	public Game(final String boardLayout) {

		players = new ArrayList<Player>();
		board = new Board();

		// Generate the board from a 2D layout held as a String
		generateBoard(boardLayout);
		board.laceTiles();
	}

	private void generateBoard(final String boardLayout) {

		// Map of players to track players added to board
		final Map<Integer, Player> playerMap = new HashMap<Integer, Player>();

		// Split board layout into rows
		final String[] rows = boardLayout.split("\n");

		// Create array of tiles
		board.tiles = new Tile[rows[0].length()][rows.length];

		// Process board layout
		for (int r = 0; r < rows.length; r++) {
			for (int c = 0; c < rows[0].length(); c++) {
				final Tile tile = new Tile(c, r);
				final char ch = rows[r].charAt(c);
				if (ch == '.') {
					// Enabled tile
					tile.enabled = true;
				} else if (ch == '#') {
					// Disabled tile
					tile.enabled = false;
				} else if (ch >= '0' && ch <= '9') {
					// Tile is enabled and contains a player
					tile.enabled = true;
					final int playerIndex = ch - '0';
					final Player player = new Player();
					playerMap.put(playerIndex, player);
					// Connect tile and player
					player.tile = tile;
					tile.player = player;
				} else {
					throw new Error();
				}
				board.tiles[c][r] = tile;
			}
		}

		// Arrange players as list
		final int numberOfPlayers = playerMap.size();
		for (int i = 0; i < numberOfPlayers; i++) {
			final Player player = playerMap.get(i);
			players.add(player);
		}
	}

	public Board getBoard() {
		return board;
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public static class Player {

		private Tile tile;

		public Tile getTile() {
			return tile;
		}

		public void setTile(Tile tile) {
			this.tile = tile;
		}
	}

	public static class Board {

		private Tile[][] tiles;

		public Tile[][] getRawTiles() {
			return tiles;
		}

		public Set<Tile> getAllTiles() {
			final Set<Tile> set = new HashSet<Tile>();
			for (final Tile[] column : tiles) {
				for (final Tile tile : column) {
					set.add(tile);
				}
			}
			return set;
		}

		public Set<Tile> getReachableTiles() {
			throw new Error("Not Implemented");
		}

		public Set<Tile> getUnreachableTiles() {
			Set<Tile> set = getAllTiles();
			set.removeAll(getReachableTiles());
			return set;
		}

		public void laceTiles() {
			for (int c = 0; c < tiles.length; c++) {
				for (int r = 0; r < tiles[c].length; r++) {
					// North
					if (r > 0) {
						tiles[c][r].n = tiles[c][r - 1];
					}
					// East
					if (c < tiles.length - 1) {
						tiles[c][r].e = tiles[c + 1][r];
					}
					// South
					if (r < tiles[c].length - 1) {
						tiles[c][r].s = tiles[c][r + 1];
					}
					// West
					if (c > 0) {
						tiles[c][r].w = tiles[c - 1][r];
					}
				}
			}
		}
	}

	public static class Tile {

		private boolean enabled = true;

		private Player player;

		final int row;

		final int column;

		public Tile n;
		public Tile s;
		public Tile e;
		public Tile w;

		public Tile(final int column, final int row) {
			this.column = column;
			this.row = row;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public Player getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}
	}
}
