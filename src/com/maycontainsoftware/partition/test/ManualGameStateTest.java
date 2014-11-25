package com.maycontainsoftware.partition.test;

import com.maycontainsoftware.partition.GameState;

public class ManualGameStateTest {
	public static void main(String[] args) {
		
		final String[] boardLayouts = {
				"....\n.0#.\n.#1.\n....",
				"0",
				"..0....",
				"0123\n4567",
				"###\n#0#\n###",
		};
		
		for(final String boardLayout : boardLayouts) {
			
			System.out.println("Board: " + boardLayout);
			
			final GameState state = GameState.newGameState(boardLayout);
			
			System.out.println("\t#players: " + GameState.getNumberOfPlayers(state));
			System.out.println("\tpossibleMoves: " + GameState.getValidMoves(state));
			System.out.println("\tpossibleMoves.size: " + GameState.getValidMoves(state).size());
			System.out.println("\t(-1,0) is valid? " + GameState.isValidCoordinates(state, (byte)-1, (byte)0));
			System.out.println("\t(0,-1) is valid? " + GameState.isValidCoordinates(state, (byte)0, (byte)-1));
			System.out.println("\t(99,0) is valid? " + GameState.isValidCoordinates(state, (byte)99, (byte)0));
			System.out.println("\t(0,99) is valid? " + GameState.isValidCoordinates(state, (byte)0, (byte)99));
			System.out.println("\t(1,1) is valid? " + GameState.isValidCoordinates(state, (byte)1, (byte)1));
			System.out.println("\t#reachableTiles: " + GameState.getReachableTiles(state).size());
			System.out.println("\tValid to move to (0, 0)? " + GameState.isValidMove(state, new byte[] { 0, 0 }));
			System.out.println("\tValid to move to (1, 1)? " + GameState.isValidMove(state, new byte[] { 1, 1 }));
		}
	}
}
