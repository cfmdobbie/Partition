package com.maycontainsoftware.partition.gamestate;

/**
 * Interface for an AI that processes its turn asynchronously.
 * 
 * @author Charlie
 */
public interface IAsyncAI {

	/**
	 * Notification that the AI should start considering its next move. This method should return promptly, so
	 * implementations should execute all logic in a separate thread.
	 * 
	 * @param state
	 *            The current game state.
	 * @param callback
	 *            The callback to notify when thinking is complete.
	 */
	public void startThinking(final GameState state, final IThinkingCompleteCallback callback);

	/**
	 * Get the chosen move instruction.
	 * 
	 * @return A two-element byte array containing coordinates of the move location.
	 */
	public byte[] getMove();

	/**
	 * Get the chosen shoot instruction.
	 * 
	 * @return A two-element byte array containing coordinates of the shoot location.
	 */
	public byte[] getShoot();

	/**
	 * The callback that must be called once the AI has decided on its next move.
	 * 
	 * @author Charlie
	 */
	public static interface IThinkingCompleteCallback {
		/**
		 * Notify the callback that thinking is complete. Note that this method will be executed from a separate thread,
		 * so implementations will need to take that into account before processing any results.
		 */
		public void thinkingComplete();
	}
}
