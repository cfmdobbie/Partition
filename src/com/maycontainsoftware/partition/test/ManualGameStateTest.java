package com.maycontainsoftware.partition.test;

import com.maycontainsoftware.partition.GameState;

public class ManualGameStateTest {
	public static void main(String[] args) {

		final String[] boardLayouts = {
				// Simple 4x4, 2 players, 2 gaps
				"....\n.0#.\n.#1.\n....",
				// 1x1 with 1 player
				"0",
				// Strip with one player
				"..0....",
				// 4x2 filled with 8 players
				"0123\n4567",
				// 3x3, 1 player on only valid square
				"###\n#0#\n###", };

		for (final String boardLayout : boardLayouts) {

			System.out.println("Board: " + boardLayout);

			final GameState state = GameState.newGameState(boardLayout);

			System.out.println("\t#players: " + GameState.getNumberOfPlayers(state));
			System.out.println("\tpossibleMoves: " + GameState.getValidMoves(state));
			System.out.println("\tpossibleMoves.size: " + GameState.getValidMoves(state).size());
			System.out.println("\t(-1,0) is valid? " + GameState.isValidCoordinates(state, (byte) -1, (byte) 0));
			System.out.println("\t(0,-1) is valid? " + GameState.isValidCoordinates(state, (byte) 0, (byte) -1));
			System.out.println("\t(99,0) is valid? " + GameState.isValidCoordinates(state, (byte) 99, (byte) 0));
			System.out.println("\t(0,99) is valid? " + GameState.isValidCoordinates(state, (byte) 0, (byte) 99));
			System.out.println("\t(1,1) is valid? " + GameState.isValidCoordinates(state, (byte) 1, (byte) 1));
			System.out.println("\t#reachableTiles: " + GameState.getReachableTiles(state).size());
			System.out.println("\tValid to move to (0, 0)? " + GameState.isValidMove(state, new byte[] { 0, 0 }));
			System.out.println("\tValid to move to (1, 1)? " + GameState.isValidMove(state, new byte[] { 1, 1 }));
			System.out.println("\tisGameOver? " + GameState.isGameOver(state));
			System.out.println("\tisStalemate? " + GameState.isStalemate(state));
		}

		{
			System.out.println();
			System.out.println("==== Test move ====");
			GameState state = GameState.newGameState(boardLayouts[0]);
			System.out.println("Valid moves: " + GameState.getValidMoves(state).size());
			System.out.println("Move to (0, 0)");
			state = GameState.apply(state, new byte[] { 0, 0 });
			System.out.println("Valid moves: " + GameState.getValidMoves(state).size());
		}
	}
}
