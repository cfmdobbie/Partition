package com.maycontainsoftware.partition.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.maycontainsoftware.partition.Arbiter;
import com.maycontainsoftware.partition.GameState;
import com.maycontainsoftware.partition.IBoard;
import com.maycontainsoftware.partition.IPlayer;
import com.maycontainsoftware.partition.ITile;

/** Simple test of the Arbiter's ability to run a game. */
public class ArbiterGameTest {
	public static void main(String[] args) {

		GameState state = GameState.newGameState("0.\n.1");

		// Board
		TestBoard board = new TestBoard();

		// Players
		List<IPlayer> players = new ArrayList<IPlayer>();
		for (int i = 0; i < GameState.getNumberOfPlayers(state); i++) {
			players.add(new TestPlayer(i));
		}

		// Tiles
		final int columns = GameState.getNumberOfColumns(state);
		final int rows = GameState.getNumberOfRows(state);
		ITile[][] tileArray = new ITile[columns][rows];
		Set<ITile> tiles = new HashSet<ITile>();
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				TestTile tile = new TestTile(c, r);
				tileArray[c][r] = tile;
				tiles.add(tile);
			}
		}

		Arbiter arbiter = new Arbiter(state, board, players, tiles);

		// Move
		arbiter.input(tileArray[1][0]);
		// Shoot
		arbiter.input(tileArray[0][0]);

		// Move
		arbiter.input(tileArray[0][0]);
		arbiter.input(tileArray[1][0]);
		arbiter.input(tileArray[0][1]);

		// Shoot
		arbiter.input(tileArray[1][1]);
	}

	static class TestBoard implements IBoard {

		@Override
		public void doStalemate() {
			System.out.println("Stalemate");
		}

		@Override
		public void doGameOver() {
			System.out.println("Game Over");
		}
	}

	static class TestPlayer implements IPlayer {

		final int playerNumber;

		public TestPlayer(int playerNumber) {
			this.playerNumber = playerNumber;
		}

		@Override
		public void doPendingMove() {
			System.out.println("Player " + playerNumber + " pending move");
		}

		@Override
		public void doMove(ITile targetTile, Arbiter arbiter) {
			System.out.println("Player " + playerNumber + " moving");
			arbiter.moveDone();
		}

		@Override
		public void doPendingShoot() {
			System.out.println("Player " + playerNumber + " pending shoot");
		}

		@Override
		public void doShoot(ITile targetTile, Arbiter arbiter) {
			System.out.println("Player " + playerNumber + " shooting");
		}
	}

	static class TestTile implements ITile {

		int c, r;

		public TestTile(int c, int r) {
			this.c = c;
			this.r = r;
		}

		@Override
		public byte[] getCoords() {
			return new byte[] { (byte) c, (byte) r };
		}

		@Override
		public void doError() {
			System.out.println("Tile [" + c + "," + r + "] acknowledges error");
		}

		@Override
		public void doShoot(Arbiter arbiter) {
			System.out.println("Tile [" + c + "," + r + "] has been shot");
			arbiter.shootDone();
		}
	}
}
