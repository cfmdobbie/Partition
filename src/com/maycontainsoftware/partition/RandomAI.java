package com.maycontainsoftware.partition;

import java.util.Random;

import com.maycontainsoftware.partition.gamestate.GameState;

/**
 * A simple AI player. This player randomly picks moves.
 * 
 * @author Charlie
 */
public class RandomAI {

	/** Tag for logging purposes. */
	public static final String LOG = RandomAI.class.getName();

	/** This player's player number. */
	private final int playerNumber;

	/**
	 * Construct a new AI player.
	 * 
	 * @param playerNumber
	 */
	public RandomAI(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Pick the computer player's next move
	 * 
	 * @param state
	 *            The current game state
	 * @return A two-element byte array containing the desired move coordinates.
	 * @throws IllegalStateException
	 *             If game is not waiting for a move, or it is not this player's turn.
	 */
	public byte[] doMove(final GameState state) {

		// Check that the game is waiting for a move instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_MOVE) {
			throw new IllegalStateException("RandomAI[" + playerNumber + "]::doMove;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException("RandomAI[" + playerNumber + "]::doMove;state.currentPlayerIndex="
					+ state.currentPlayerIndex);
		}

		// Determine available moves
		final byte[][] validMoves = GameState.getValidMoves(state).toArray(new byte[][] {});

		// Pick a random move
		final int randomMove = new Random().nextInt(validMoves.length);

		// Return it
		return validMoves[randomMove];
	}

	/**
	 * Pick the computer player's next shoot
	 * 
	 * @param state
	 *            The current game state
	 * @return A two-element byte array containing the desired shoot coordinates.
	 * @throws IllegalStateException
	 *             If game is not waiting for a shoot, or it is not this player's turn.
	 */
	public byte[] doShoot(final GameState state) {

		// Check that the game is waiting for a shoot instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_SHOOT) {
			throw new IllegalStateException("RandomAI[" + playerNumber + "]::doShoot;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException("RandomAI[" + playerNumber + "]::doShoot;state.currentPlayerIndex="
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
