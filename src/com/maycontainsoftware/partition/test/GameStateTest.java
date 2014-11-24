package com.maycontainsoftware.partition.test;

import com.maycontainsoftware.partition.GameState;

public class GameStateTest {
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
		}
	}
}
