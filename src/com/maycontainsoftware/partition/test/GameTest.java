package com.maycontainsoftware.partition.test;

import com.maycontainsoftware.partition.Game;
import com.maycontainsoftware.partition.Game.Tile;

public class GameTest {
	public static void main(String[] args) {

		final String[] boardLayouts = {
				"....\n.1#.\n.#2.\n....",
				"",
				"............",
				"1234\n5678",
				"###\n#1#\n###",
				//"\n",
		};
		
		for(final String boardLayout : boardLayouts) {
			
			System.out.println("Board: " + boardLayout);
			
			final Game game = new Game(boardLayout);
			
			System.out.println("\t#players: " + game.getNumberOfPlayers());
			final Tile[][] tiles = game.getBoard().getRawTiles();
			final int numberOfColumns = tiles.length;
			System.out.println("\t#columns: " + numberOfColumns);
			if(numberOfColumns > 0) {
				final int numberOfRows = tiles[0].length;
				System.out.println("\t#rows: " + numberOfRows);
				
				if(numberOfRows > 0) {
					final Tile t = tiles[0][0];
					System.out.println("\t(0, 0).n = " + t.n);
					System.out.println("\t(0, 0).e = " + t.e);
					System.out.println("\t(0, 0).s = " + t.s);
					System.out.println("\t(0, 0).w = " + t.w);
				}
			}
			System.out.println("\tRaw tiles: " + tiles);
		}
	}
}
