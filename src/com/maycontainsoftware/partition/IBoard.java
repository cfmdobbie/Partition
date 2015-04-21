package com.maycontainsoftware.partition;

import java.util.Map;
import java.util.Set;

public interface IBoard {

	/**
	 * Notification that the game has been won outright.
	 * 
	 * @param winner
	 *            The winning player.
	 * @param playerTerritories
	 *            The separated player territories.
	 * @param unreachable
	 *            The unreachable tiles.
	 */
	public void doWin(IPlayer winner, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable);

	/**
	 * Notification that the game is a draw.
	 * 
	 * @param winners
	 *            The winning players.
	 * @param playerTerritories
	 *            The separated player territories.
	 * @param unreachable
	 *            The unreachable tiles.
	 */
	public void doDraw(Set<IPlayer> winners, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable);

	/**
	 * Notification that the game has ended in a stalemate.
	 * 
	 * @param unreachable
	 *            The unreachable tiles.
	 */
	public void doStalemate(Set<ITile> unreachable);
}
