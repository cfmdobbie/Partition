package com.maycontainsoftware.partition.gamestate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An aggressive AI player. This player enumerates all possible moves, and picks a win if it is able to. Otherwise, it
 * tries to keep the game going by avoiding draws, stalemates and losses. When a choice is relevant, it aims to minimize
 * the opponent's movement options.
 * 
 * @author Charlie
 */
public class EvaluatingAI1 implements IAI {

	/** Tag for logging purposes. */
	public static final String TAG = EvaluatingAI1.class.getName();

	/** Whether or not to output debug messages. */
	private static final boolean DEBUG_LOG = false;

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
	public EvaluatingAI1(final int playerNumber) {
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

		if (DEBUG_LOG) {
			System.out.println("Starting doMove at time: " + System.currentTimeMillis() + " ms");
		}

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

				final GameState finalState = GameState.apply(stateAfterMove, shoot);
				final Turn turn = new Turn(move, shoot, finalState);

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
			// Pick a random winning move - don't care which
			chosenTurn = randomElement(wins);
		} else if (!other.isEmpty()) {
			// Pick the best move available
			chosenTurn = pickBest(other);
		} else if (!draws.isEmpty()) {
			// Pick a random drawn move - don't care which
			chosenTurn = randomElement(draws);
		} else if (!stalemates.isEmpty()) {
			// Pick a random stalemate - don't care which
			chosenTurn = randomElement(stalemates);
		} else if (!losses.isEmpty()) {
			// Pick a random losing move - don't care which
			chosenTurn = randomElement(losses);
		}

		if (DEBUG_LOG) {
			System.out.println("Ending doMove at time: " + System.currentTimeMillis() + " ms");
		}

		return chosenTurn.move;
	}

	@Override
	public byte[] doShoot(final GameState state) {

		// Return the previously chosen shoot
		return chosenTurn.shoot;
	}

	/**
	 * Rank the turns in the given set and return the "best" turn present. Suitability is judged by minimizing the
	 * number of moves available to the other player while maximizing the number of moves left available to the self.
	 * 
	 * @param set
	 *            The set containing move/shoot pairs to pick from.
	 * @return The best move from the set.
	 */
	private Turn pickBest(final Set<Turn> set) {

		// Map to contain (score) -> (turns with that score)
		final Map<Integer, Set<Turn>> ranked = new HashMap<Integer, Set<Turn>>();

		for (final Turn turn : set) {

			// Evaluation is based purely on how well we limit the opponents options
			// In actual fact, we only limit the *next player's* options, which for a two-player game is the same thing.
			int nextPlayerMoves = GameState.getValidMoves(turn.endState).size();

			if (ranked.containsKey(nextPlayerMoves)) {
				ranked.get(nextPlayerMoves).add(turn);
			} else {
				final Set<Turn> newSet = new HashSet<Turn>();
				newSet.add(turn);
				ranked.put(nextPlayerMoves, newSet);
			}
		}

		// Work out which set of turns leave the opponent with the fewest possible move options

		int minimumNextPlayerMoves = Integer.MAX_VALUE;
		Set<Turn> bestSet = null;

		for (int numberOfMoves : ranked.keySet()) {
			if (numberOfMoves < minimumNextPlayerMoves) {
				minimumNextPlayerMoves = numberOfMoves;
				bestSet = ranked.get(numberOfMoves);
			}
		}

		if (DEBUG_LOG) {
			System.out.println("Minimum next player moves: " + minimumNextPlayerMoves + ", number of options: "
					+ bestSet.size());
		}

		// Pick a random move/shoot pair out of the "best" options

		return randomElement(bestSet);
	}

	/**
	 * Get a random element from a set of turns.
	 * 
	 * @param set
	 *            A set of Turn objects.
	 * @return A random element from the specified set.
	 * @throws IllegalArgumentException
	 *             if the specified set is empty.
	 */
	private Turn randomElement(final Set<Turn> set) {

		if (set.isEmpty()) {
			throw new IllegalArgumentException(TAG + "::randomElement;empty");
		}

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
	 * Simple class to hold move and shoot coordinates, plus resulting game states.
	 * 
	 * @author Charlie
	 */
	private static class Turn {
		final byte[] move;
		final byte[] shoot;
		final GameState endState;

		public Turn(final byte[] move, final byte[] shoot, final GameState endState) {
			this.move = move;
			this.shoot = shoot;
			this.endState = endState;
		}
	}
}
