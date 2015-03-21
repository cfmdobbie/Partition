package com.maycontainsoftware.partition;

public interface IPlayer {

	/** Notification that this player is now pending shoot. */
	// start periodic anim bounce
	public void doPendingMove();

	/**
	 * Notification that this player should move to the specified tile.
	 * 
	 * @param targetTile
	 *            The tile to move to.
	 * @param arbiter
	 *            The Arbiter to notify once all actions are complete using the moveDone() callback.
	 */
	// stop anim bounce, anim move to new location
	public void doMove(ITile targetTile, Arbiter arbiter);

	/** Notification that this player is now pending a shoot action. */
	// start target periodic anim bounce, show target
	public void doPendingShoot();

	/**
	 * Notification that this player has performed a shoot action.
	 * 
	 * @param targetTile
	 *            The tile that has been shot.
	 * @param arbiter
	 *            The Arbiter that needs to be notified that the entire shoot action has been handled, by calling
	 *            shootDone(). Note that the tile is also notified that it has been shot, but only one of these two
	 *            should notify the Arbiter.
	 */
	// stop target bounce, hide target
	public void doShoot(ITile targetTile, Arbiter arbiter);

	/**
	 * Reset player, position on starting tile.
	 * 
	 * @param startingTile
	 *            The starting tile.
	 */
	public void doReset(ITile startingTile);
}
