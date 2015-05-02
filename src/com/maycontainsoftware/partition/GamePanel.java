package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * The card on which the game is actually played.
 * 
 * @author Charlie
 */
public class GamePanel extends Table {

	/** Tag for logging purposes. */
	public static final String TAG = GamePanel.class.getName();

	/**
	 * Constructor.
	 * 
	 * @param game
	 *            The Game instance.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 */
	public GamePanel(final PartitionGame game, final CardStack cardStack,
			final PlayerConfiguration playerConfiguration, final BoardConfiguration boardConfiguration) {

		// Get reference to main TextureAtlas
		final TextureAtlas atlas = game.textureAtlas;

		// GameBoard manages its own fixed-aspect behaviour, so just add the board to the screen at max available size
		final BaseGameBoard gameBoard = new GameBoard(game, atlas, cardStack.getWidth(), cardStack.getHeight(),
				playerConfiguration, boardConfiguration, cardStack);
		row();
		add(gameBoard).expand().fill();
	}
}
