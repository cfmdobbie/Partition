package com.maycontainsoftware.partition;

/**
 * Enumeration of player configurations.
 * 
 * @author Charlie
 */
public enum PlayerConfiguration {
	ONE_PLAYER(false),
	TWO_PLAYER(false, false),
	ONE_PLAYER_VS_COMPUTER(false, true),
	TWO_COMPUTERS(true, true);

	/** Flags to show which players are computer-controlled. */
	private final boolean[] isComputerPlayer;

	/**
	 * Construct a new PlayerConfiguration.
	 * 
	 * @param isComputerPlayer
	 *            Array/varargs of flags to determine which players are computer-controlled.
	 */
	private PlayerConfiguration(boolean... isComputerPlayer) {
		this.isComputerPlayer = isComputerPlayer;
	}

	/**
	 * @return The number of players.
	 */
	public final int getNumberOfPlayers() {
		return isComputerPlayer.length;
	}

	/**
	 * @param playerNumber
	 *            The player number.
	 * @return True if the player is computer-controlled, false otherwise.
	 */
	public final boolean isComputerPlayer(final int playerNumber) {
		return isComputerPlayer[playerNumber];
	}
}