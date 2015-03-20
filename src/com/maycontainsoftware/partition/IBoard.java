package com.maycontainsoftware.partition;

public interface IBoard {

	/** Notification that the game is over. */
	public void doGameOver();

	/** Notification that the game has ended in a stalemate. */
	public void doStalemate();
}
