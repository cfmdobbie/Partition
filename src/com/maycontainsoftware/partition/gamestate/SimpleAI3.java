package com.maycontainsoftware.partition.gamestate;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A simple AI player. This player enumerates all possible moves, and picks a win if it is able to. Otherwise, it tries
 * to keep the game going by avoiding draws, stalemates and losses.
 * 
 * @author Charlie
 */
public class SimpleAI3 implements IAI {

	/** Tag for logging purposes. */
	public static final String TAG = SimpleAI3.class.getName();

	/** This player's player number. */
	private final int playerNumber;

	/** The chosen turn. */
	private Turn chosenTurn;

	/** Set of available winning turns. */
	private Set<Turn> wins = new HashSet<Turn>();

	/** Set of available losing turns. */
	private Set<Turn> losses = new HashSet<Turn>();

	/** Set of available drawn turns. */
	private Set<Turn> draws = new HashSet<Turn>();

	/** Set of available stalemate turns. */
	private Set<Turn> stalemates = new HashSet<Turn>();

	/** Set of all other available turns. */
	private Set<Turn> other = new HashSet<Turn>();

	/**
	 * Construct a new AI player.
	 * 
	 * @param playerNumber
	 */
	public SimpleAI3(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	/** Clear all sets of Turns, in preparation for calculating the next move/shoot pair. */
	private void preMoveSetup() {
		wins.clear();
		losses.clear();
		draws.clear();
		stalemates.clear();
		other.clear();
	}

	@Override
	public byte[] doMove(final GameState state) {

		// System.out.println(System.currentTimeMillis());

		preMoveSetup();

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
		final Set<byte[]> validMoves = GameState.getValidMoves(state);

		// Iterate through all possible moves
		for (final byte[] move : validMoves) {

			// Determine the prospective new game state
			final GameState stateAfterMove = GameState.apply(state, move);

			// Determine available shoots
			final Set<byte[]> validShoots = GameState.getValidMoves(stateAfterMove);

			// Iterate through all possible shoots, given the current move
			for (final byte[] shoot : validShoots) {

				final Turn turn = new Turn(move, shoot);

				final GameState finalState = GameState.apply(stateAfterMove, shoot);

				if (GameState.isStalemate(finalState)) {
					// Stalemate
					stalemates.add(turn);
				} else if (GameState.isGameOver(finalState)) {
					// Win, loss or draw
					if (GameState.isDraw(finalState)) {
						// Draw
						draws.add(turn);
					} else if (GameState.getWinningPlayer(finalState) == playerNumber) {
						// Win
						wins.add(turn);
					} else {
						// Loss
						losses.add(turn);
					}
				} else {
					// Game continues
					other.add(turn);
				}
			}
		}

		// Now need to pick a move/shoot pair
		if (!wins.isEmpty()) {
			// Able to win!
			// System.out.println("Picking a WIN");
			chosenTurn = randomElement(wins);
		} else if (!other.isEmpty()) {
			// Can't win - but can keep playing!
			// System.out.println("Picking an other move");
			chosenTurn = randomElement(other);
		} else if (!draws.isEmpty()) {
			// Can't win, can't keep playing - but can force a draw!
			// System.out.println("Picking a DRAW");
			chosenTurn = randomElement(draws);
		} else if (!stalemates.isEmpty()) {
			// Can't win, can't keep playing, can't draw - but can force a stalemate!
			// System.out.println("Picking a STALEMATE");
			chosenTurn = randomElement(stalemates);
		} else if (!losses.isEmpty()) {
			// Can't win, can't keep playing, can't draw, no stalemates - this is a loss! :-(
			// System.out.println("Picking a LOSS");
			chosenTurn = randomElement(losses);
		}

		// System.out.println(System.currentTimeMillis());

		return chosenTurn.move;
	}

	@Override
	public byte[] doShoot(final GameState state) {

		// Return the previously chosen shoot
		return chosenTurn.shoot;
	}

	/**
	 * Get a random element from a set of turns.
	 * 
	 * @param set
	 *            A set of Turn objects.
	 * @return A random element from the specified set.
	 */
	private Turn randomElement(final Set<Turn> set) {
		int random = new Random().nextInt(set.size());
		int i = 0;
		for (final Turn turn : set) {
			if (i == random) {
				return turn;
			}
			i++;
		}

		throw new RuntimeException(TAG + "::randomElement;no_more_elements");
	}

	/**
	 * Simple class to hold move and shoot coordinates.
	 * 
	 * @author Charlie
	 */
	private static class Turn {
		final byte[] move;
		final byte[] shoot;

		public Turn(final byte[] move, final byte[] shoot) {
			this.move = move;
			this.shoot = shoot;
		}
	}
}
