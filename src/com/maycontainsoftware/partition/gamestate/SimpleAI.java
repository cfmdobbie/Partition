package com.maycontainsoftware.partition.gamestate;

import java.util.Random;

/**
 * A simple AI player. This player randomly picks moves.
 * 
 * @author Charlie
 */
public class SimpleAI implements IAI {

	/** Tag for logging purposes. */
	public static final String TAG = SimpleAI.class.getName();

	/** This player's player number. */
	private final int playerNumber;

	/**
	 * Construct a new AI player.
	 * 
	 * @param playerNumber
	 */
	public SimpleAI(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	@Override
	public byte[] doMove(final GameState state) {

		// Check that the game is waiting for a move instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_MOVE) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doMove;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doMove;state.currentPlayerIndex="
					+ state.currentPlayerIndex);
		}

		// Determine available moves
		final byte[][] validMoves = GameState.getValidMoves(state).toArray(new byte[][] {});

		// Pick a random move
		final int randomMove = new Random().nextInt(validMoves.length);

		// Return it
		return validMoves[randomMove];
	}

	@Override
	public byte[] doShoot(final GameState state) {

		// Check that the game is waiting for a shoot instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_SHOOT) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doShoot;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doShoot;state.currentPlayerIndex="
					+ state.currentPlayerIndex);
		}

		// Determine available shoots
		final byte[][] validShoots = GameState.getValidMoves(state).toArray(new byte[][] {});

		// Pick a random shoot
		final int randomShoot = new Random().nextInt(validShoots.length);

		// Return it
		return validShoots[randomShoot];
	}
}
