package com.maycontainsoftware.partition.gamestate;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A simple AI player. This player enumerates all possible moves, then picks a random one.
 * 
 * @author Charlie
 */
public class SimpleAI2 implements IAI {

	/** Tag for logging purposes. */
	public static final String TAG = SimpleAI2.class.getName();

	/** This player's player number. */
	private final int playerNumber;

	/** The chosen turn. */
	private Turn chosenTurn;

	/**
	 * Construct a new AI player.
	 * 
	 * @param playerNumber
	 */
	public SimpleAI2(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	@Override
	public byte[] doMove(final GameState state) {

		System.out.println(System.currentTimeMillis());

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

		// Generate a set of all possible move/shoot pairs

		final Set<Turn> turns = new HashSet<Turn>();

		// Determine available moves
		final byte[][] validMoves = GameState.getValidMoves(state).toArray(new byte[][] {});

		for (final byte[] move : validMoves) {

			// Determine the prospective new game state
			final GameState newState = GameState.apply(state, move);

			// Determine available shoots
			final byte[][] validShoots = GameState.getValidMoves(newState).toArray(new byte[][] {});

			for (final byte[] shoot : validShoots) {
				turns.add(new Turn(move, shoot));
			}
		}

		// Pick a random move/shoot pair
		final Turn[] turnArray = turns.toArray(new Turn[] {});
		chosenTurn = turnArray[new Random().nextInt(turnArray.length)];

		System.out.println(System.currentTimeMillis());

		// Return the chosen move
		return chosenTurn.move;
	}

	@Override
	public byte[] doShoot(final GameState state) {

		// Return the previously chosen shoot
		return chosenTurn.shoot;
	}

	private static class Turn {
		final byte[] move;
		final byte[] shoot;

		public Turn(final byte[] move, final byte[] shoot) {
			this.move = move;
			this.shoot = shoot;
		}
	}
}
