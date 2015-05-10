package com.maycontainsoftware.partition.gamestate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An aggressive AI player. This player enumerates all possible moves, and picks a win if it is able to. Otherwise, it
 * tries to keep the game going by avoiding draws, stalemates and losses. When a choice is relevant, it aims to first
 * minimize the opponent's movement options, then maximize its own options.
 * 
 * @author Charlie
 */
public class EvaluatingAsyncAI extends AsyncAI {

	/** Tag for logging purposes. */
	public static final String TAG = EvaluatingAsyncAI.class.getName();

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
	 *            The AI player's number.
	 */
	public EvaluatingAsyncAI(final int playerNumber) {
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
	protected void doThinking(GameState state) {

		preMoveSetup();

		// Check that the game is waiting for a move instruction
		if (GameState.getTurnPhase(state) != GameState.PHASE_MOVE) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doThinking;state.turnPhase="
					+ GameState.getTurnPhase(state));
		}

		// Check that it's this player's turn
		if (state.currentPlayerIndex != playerNumber) {
			throw new IllegalStateException(TAG + "[" + playerNumber + "]::doThinking;state.currentPlayerIndex="
					+ state.currentPlayerIndex);
		}

		// Determine available moves
		final Set<byte[]> validMoves = GameState.getValidMoves(state);

		if (DEBUG_LOG) {
			System.out.println(TAG + "::doThinking;validMoves.size=" + validMoves.size());
		}

		// Iterate through all possible moves
		for (final byte[] move : validMoves) {

			// Determine the prospective new game state
			final GameState stateAfterMove = GameState.apply(state, move);

			// Determine available shoots
			final Set<byte[]> validShoots = GameState.getValidMoves(stateAfterMove);

			if (DEBUG_LOG) {
				System.out.println(TAG + "::doThinking;move[" + move[0] + "," + move[1] + "],validShoots.size="
						+ validShoots.size());
			}

			// Iterate through all possible shoots, given the current move
			for (final byte[] shoot : validShoots) {

				final GameState finalState = GameState.apply(stateAfterMove, shoot);
				final Turn turn = new Turn(move, shoot, finalState);

				if (GameState.isStalemate(finalState)) {
					// Stalemate
					if (DEBUG_LOG) {
						System.out.println(TAG + "::doThinking;move[" + move[0] + "," + move[1] + "],shoot[" + shoot[0]
								+ "," + shoot[1] + "] is a stalemate");
					}
					stalemates.add(turn);
				} else if (GameState.isGameOver(finalState)) {
					// Win, loss or draw
					if (GameState.isDraw(finalState)) {
						// Draw
						if (DEBUG_LOG) {
							System.out.println(TAG + "::doThinking;move[" + move[0] + "," + move[1] + "],shoot["
									+ shoot[0] + "," + shoot[1] + "] is a draw");
						}
						draws.add(turn);
					} else if (GameState.getWinningPlayer(finalState) == playerNumber) {
						// Win
						if (DEBUG_LOG) {
							System.out.println(TAG + "::doThinking;move[" + move[0] + "," + move[1] + "],shoot["
									+ shoot[0] + "," + shoot[1] + "] is a win");
						}
						wins.add(turn);
					} else {
						// Loss
						if (DEBUG_LOG) {
							System.out.println(TAG + "::doThinking;move[" + move[0] + "," + move[1] + "],shoot["
									+ shoot[0] + "," + shoot[1] + "] is a loss");
						}
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

		move = chosenTurn.move;
		shoot = chosenTurn.shoot;
	}

	/**
	 * Rank the turns in the given set and return the "best" turn present. Suitability is judged by minimizing the
	 * number of moves available to the other player while maximizing the number of moves left available to the self.
	 * 
	 * @param set
	 *            The set containing move/shoot pairs to pick from.
	 * @return The best move from the set.
	 * @throws IllegalArgumentException
	 *             if the specified set is empty.
	 */
	private Turn pickBest(final Set<Turn> set) {

		if (set.isEmpty()) {
			throw new IllegalArgumentException(TAG + "::pickBest;empty");
		}

		// Determine the turns most damaging to the other player
		final Set<Turn> mostDamaging = pickMostDamaging(set);

		if (DEBUG_LOG) {
			System.out.println(TAG + "::pickBest: number of move/shoot pairs available: " + mostDamaging.size());
		}

		if (mostDamaging.size() == 1) {
			// If there's only one option, just return it
			return mostDamaging.iterator().next();
		}

		// Out of the most damaging turns, determine the most personally beneficent
		final Set<Turn> mostBeneficient = pickMostBeneficient(mostDamaging);

		// Out of these, we're happy to pick any
		return randomElement(mostBeneficient);
	}

	/**
	 * Pick the most move/shoot pairs most damaging to the next player.
	 * 
	 * @param set
	 *            The set of available turns.
	 * @return A subset of the most damaging turns.
	 * @throws IllegalArgumentException
	 *             if the specified set is empty.
	 */
	private final Set<Turn> pickMostDamaging(final Set<Turn> set) {

		if (set.isEmpty()) {
			throw new IllegalArgumentException(TAG + "::pickMostDamaging;empty");
		}

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

		if (DEBUG_LOG) {
			for (final Integer i : ranked.keySet()) {
				System.out.println(TAG + "::pickMostDamaging;playerMoves=" + i + ",options=" + ranked.get(i).size());

			}
		}

		// Work out which set of turns leave the opponent with the fewest possible move options

		int minimumNextPlayerMoves = Integer.MAX_VALUE;
		Set<Turn> mostDamagingSet = null;

		for (int numberOfMoves : ranked.keySet()) {
			if (numberOfMoves < minimumNextPlayerMoves) {
				minimumNextPlayerMoves = numberOfMoves;
				mostDamagingSet = ranked.get(numberOfMoves);
			}
		}

		if (DEBUG_LOG) {
			System.out.println(TAG + "::pickMostDamaging: Minimum next player moves: " + minimumNextPlayerMoves
					+ ", number of options: " + mostDamagingSet.size());
		}

		return mostDamagingSet;
	}

	/**
	 * Out of a set of move/shoot pairs, generate a subset containing just the most personally beneficent turns.
	 * 
	 * @param set
	 *            The set of possible turns.
	 * @return A set containing the most personally beneficent turns out of the specified set.
	 * @throws IllegalArgumentException
	 *             if the specified set is empty.
	 */
	private final Set<Turn> pickMostBeneficient(final Set<Turn> set) {

		if (set.isEmpty()) {
			throw new IllegalArgumentException(TAG + "::pickMostBeneficient;empty");
		}

		final Map<Integer, Set<Turn>> ranked = new HashMap<Integer, Set<Turn>>();

		for (final Turn turn : set) {

			int availablePersonalMoves = GameState.getValidMoves(turn.endState, playerNumber).size();

			if (ranked.containsKey(availablePersonalMoves)) {
				ranked.get(availablePersonalMoves).add(turn);
			} else {
				final Set<Turn> newSet = new HashSet<Turn>();
				newSet.add(turn);
				ranked.put(availablePersonalMoves, newSet);
			}
		}

		// Work out a subset of turns which leaves the AI player with the most possible move options

		int maximumPersonalMoves = -1;
		Set<Turn> mostBeneficentSet = null;

		for (int numberOfMoves : ranked.keySet()) {
			if (numberOfMoves > maximumPersonalMoves) {
				maximumPersonalMoves = numberOfMoves;
				mostBeneficentSet = ranked.get(numberOfMoves);
			}
		}

		if (DEBUG_LOG) {
			System.out.println(TAG + "::pickMostBeneficient: Maximum personal moves: " + maximumPersonalMoves
					+ ", number of options: " + mostBeneficentSet.size());
		}

		return mostBeneficentSet;
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
