package com.maycontainsoftware.partition.gamestate;

import java.util.Random;

/**
 * A simple async AI player. This player randomly picks moves.
 * 
 * @author Charlie
 */
public class SimpleAsyncAI extends AsyncAI {

	/** Tag for logging purposes. */
	public static final String TAG = SimpleAsyncAI.class.getName();

	/** This player's player number. */
	private final int playerNumber;

	/**
	 * Construct a new AI player.
	 * 
	 * @param playerNumber
	 */
	public SimpleAsyncAI(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	@Override
	protected void doThinking(GameState state) {

		// Check that the game is waiting for a move instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_MOVE) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::startThinking;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::startThinking;state.currentPlayerIndex="
					+ state.currentPlayerIndex);
		}

		// Determine available moves
		final byte[][] validMoves = GameState.getValidMoves(state).toArray(new byte[][] {});

		// Pick a random move
		final int randomMove = new Random().nextInt(validMoves.length);
		move = validMoves[randomMove];

		// Calculate the new state
		final GameState newState = GameState.apply(state, move);

		// Determine available shoots
		final byte[][] validShoots = GameState.getValidMoves(newState).toArray(new byte[][] {});

		// Pick a random shoot
		final int randomShoot = new Random().nextInt(validShoots.length);
		shoot = validShoots[randomShoot];
	}
}
