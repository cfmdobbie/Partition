package com.maycontainsoftware.partition.gamestate;

/**
 * Concrete implementation of IAsyncAI, which executes the AI logic in a separate thread.
 * 
 * @author Charlie
 */
public abstract class AsyncAI implements IAsyncAI {

	/** The chosen move, as a two-byte array. */
	protected byte[] move;

	/** The chosen move, as a two-byte array. */
	protected byte[] shoot;

	@Override
	public final byte[] getMove() {
		return move;
	}

	@Override
	public final byte[] getShoot() {
		return shoot;
	}

	@Override
	public final void startThinking(final GameState state, final IThinkingCompleteCallback callback) {

		// Execute the AI logic in a new thread
		new Thread() {
			public void run() {

				// Allow subclass to think
				doThinking(state);

				// Notify the callback that thinking is complete
				callback.thinkingComplete();

			};
		}.start();
	}

	/**
	 * Method to be implemented by subclasses that performs the actual AI logic, and sets the move and shoot class
	 * members.
	 * 
	 * @param state
	 *            The current game state.
	 */
	protected abstract void doThinking(final GameState state);
}
