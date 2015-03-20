package com.maycontainsoftware.partition;

import java.util.List;
import java.util.Set;

public class Arbiter {

	private static enum GameTurnState {
		PENDING_MOVE,
		MOVING,
		PENDING_SHOOT,
		SHOOTING,
		WIN_CHECK,
		SWITCHING_PLAYERS,
		STALEMATE_CHECK,
	}

	private GameTurnState turnState;

	private final GameState initialGameState;

	private GameState state;
	
	private final IBoard board;

	private final List<IPlayer> players;

	private final Set<ITile> tiles;

	private int activePlayerNumber;

	public Arbiter(GameState initialGameState, IBoard board, List<IPlayer> players, Set<ITile> tiles) {

		// Always start waiting for the first move
		this.turnState = GameTurnState.PENDING_MOVE;
		
		// Remember the initial game state - will need to use it to reset the game
		this.initialGameState = initialGameState;
		
		// Current state is a duplicate of the initial game state
		this.state = GameState.duplicate(initialGameState);
		
		// Store references to the other participants in the logical game process
		this.board = board;
		this.players = players;
		this.tiles = tiles;
		
		// Retrieve the current player from the game state
		this.activePlayerNumber = state.currentPlayerIndex;
	}

	public void input(ITile tile) {
		switch (turnState) {
		case PENDING_MOVE:
			// We were waiting for a move
			if (GameState.isValidMove(state, tile.getCoords())) {
				// Move is valid
				// Apply the action to get a new game state
				state = GameState.apply(state, tile.getCoords());
				
				// Update the current logical game turn phase
				turnState = GameTurnState.MOVING;
				
				// Tell the player that it should move
				players.get(activePlayerNumber).doMove(tile, this);
			} else {
				// Not a valid action - tell ITile
				tile.doError();
			}
			break;
		case PENDING_SHOOT:
			// We were waiting for a shoot
			if (GameState.isValidMove(state, tile.getCoords())) {
				// Apply the action to get a new game state
				state = GameState.apply(state, tile.getCoords());
				
				// Update the current logical game turn phase
				turnState = GameTurnState.SHOOTING;
				
				// Tell the player that it should shoot
				players.get(activePlayerNumber).doShoot(tile, this);
				
				// Tell the tile that it has been shot
				tile.doShoot(this);
			} else {
				// Not a valid action - tell ITile
				tile.doError();
			}
			break;
		case MOVING:
		case SHOOTING:
		case STALEMATE_CHECK:
		case SWITCHING_PLAYERS:
		case WIN_CHECK:
		default:
			// Not a valid phase for accepting user input - ignore
			break;
		}
	}

	public void moveDone() {
		// Check that were were moving
		assert (turnState == GameTurnState.MOVING);

		// Now waiting for a decision on which tile to shoot
		turnState = GameTurnState.PENDING_SHOOT;
		
		// Tell the player that it is now pending a shoot
		players.get(activePlayerNumber).doPendingShoot();
	}

	public void shootDone() {
		// Check that were were shooting
		assert (turnState == GameTurnState.SHOOTING);

		// Check for a win
		turnState = GameTurnState.WIN_CHECK;

		if(GameState.isGameOver(state)) {
			// Someone has won - tell the board
			board.doGameOver();
		} else {
			// Nobody has one, continue
			
			// Now switching players
			turnState = GameTurnState.SWITCHING_PLAYERS;
			
			// Get the new player number from the game state
			activePlayerNumber = state.currentPlayerIndex;
			
			// Now need to check for a stalemate
			turnState = GameTurnState.STALEMATE_CHECK;
			
			if(GameState.isStalemate(state)) {
				// Game is a stalemate - tell the board
				board.doStalemate();
			} else {
				// Continue to pending a decision on which tile to move to
				turnState = GameTurnState.PENDING_MOVE;
				// Tell the player it is now pending a move
				players.get(activePlayerNumber).doPendingMove();
			}
		}
	}
}
