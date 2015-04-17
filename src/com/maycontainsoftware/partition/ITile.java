package com.maycontainsoftware.partition;

public interface ITile {

	/**
	 * Return the coordinates of the tile represented by the object.
	 * 
	 * @return a byte[2] containing the logical tile coordinates.
	 */
	public byte[] getCoords();

	/** Notification that an erroneous move has been performed against this tile. */
	// Error beep, flash up visual error notification
	public void doError();

	/**
	 * Notification that this tile has been shot.
	 * 
	 * @param arbiter
	 *            The Arbiter that needs to be notified that the entire shoot action has been handled, by calling
	 *            shootDone(). Note that the player is also notified that it has shot, but only one of these two should
	 *            notify the Arbiter.
	 */
	// anim tile disappear
	public void doShoot(Arbiter<? extends ITile, ? extends IPlayer<? extends ITile>> arbiter);

	/**
	 * Notification that the tile has been reset.
	 * 
	 * @param enabled
	 *            Whether the tile should start enabled.
	 */
	public void doReset(boolean enabled);
}
