package com.maycontainsoftware.partition.arbiter;

public interface ITile {

	/**
	 * Return the coordinates of the tile represented by the object.
	 * 
	 * @return a byte[2] containing the logical tile coordinates.
	 */
	public byte[] getCoords();

	/** Notification that an erroneous move has been performed against this tile. */
	public void doError();

	/**
	 * Notification that this tile has been shot.
	 * 
	 * @param arbiter
	 *            The Arbiter that needs to be notified that the entire shoot action has been handled, by calling
	 *            shootDone(). Note that the player is also notified that it has shot, but only one of these two should
	 *            notify the Arbiter.
	 */
	public void doShoot(Arbiter arbiter);

	/**
	 * Notification that the tile has been reset.
	 * 
	 * @param enabled
	 *            Whether the tile should start enabled.
	 */
	public void doReset(boolean enabled);
}
