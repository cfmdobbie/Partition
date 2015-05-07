package com.maycontainsoftware.partition.gamestate;

public interface IAI {

	/**
	 * Pick the computer player's next move
	 * 
	 * @param state
	 *            The current game state
	 * @return A two-element byte array containing the desired move coordinates.
	 * @throws IllegalStateException
	 *             If game is not waiting for a move, or it is not this player's turn.
	 */
	public byte[] doMove(final GameState state);

	/**
	 * Pick the computer player's next shoot
	 * 
	 * @param state
	 *            The current game state
	 * @return A two-element byte array containing the desired shoot coordinates.
	 * @throws IllegalStateException
	 *             If game is not waiting for a shoot, or it is not this player's turn.
	 */
	public byte[] doShoot(final GameState state);
}
