package com.maycontainsoftware.partition;

/**
 * Enumeration of implemented boards.
 * 
 * @author Charlie
 */
public enum BoardConfiguration {
	HUB("......\n......\n..0#..\n..#1..\n......\n......"),
	OPEN(".....\n.....\n..0..\n.....\n.....\n..1..\n.....\n....."),
	WALL(".......\n.......\n.......\n...#...\n..0#1..\n...#...\n.......\n.......\n......."),
	HOLES("...1...\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n...0..."),
	SMALL(".....\n.#0#.\n.....\n.#1#.\n....."), // Too easy?
	CORE(".......\n.##0##.\n.#...#.\n.......\n.#...#.\n.##1##.\n......."), // Too easy?
	CHEQUER("#.#0#.#\n.#.#.#.\n#.#.#.#\n.#.#.#.\n#.#.#.#\n.#.#.#.\n#.#.#.#\n.#.#.#.\n#.#1#.#\n"), // Too easy?
	STRATEGO(".......\n...0...\n.......\n.#.#.#.\n.#.#.#.\n.#.#.#.\n.......\n...1...\n......."), // Promising?
	MAIN_MENU_DEMO("..0.."),
	LOSE_DEMO(".01."),
	DRAW_DEMO(".01"),
	STALEMATE_DEMO("0.1");

	public final String boardSpec;

	private BoardConfiguration(final String boardSpec) {
		this.boardSpec = boardSpec;
	}
}