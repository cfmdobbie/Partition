package com.maycontainsoftware.partition;

import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.maycontainsoftware.partition.arbiter.IPlayer;
import com.maycontainsoftware.partition.arbiter.ITile;

/**
 * A game board that works in demo mode - i.e. doesn't display any end-of-game message, but just silently resets the
 * board.
 * 
 * @author Charlie
 */
public class DemoGameBoard extends BaseGameBoard {

	/** Tag for logging purposes. */
	private static final String TAG = DemoGameBoard.class.getName();

	/**
	 * Construct a new DemoGameBoard.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param atlas
	 *            The TextureAtlas containing all graphics required to draw the board.
	 * @param width
	 *            The (fixed) board width.
	 * @param height
	 *            The (fixed) board height.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 */
	public DemoGameBoard(final PartitionGame game, final TextureAtlas atlas, final float width, final float height,
			final PlayerConfiguration playerConfiguration, final BoardConfiguration boardConfiguration) {
		super(game, atlas, width, height, playerConfiguration, boardConfiguration, true);
	}

	@Override
	public void doWin(IPlayer winner, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doWin()");

		// Demo mode, just reset
		demoModeBoardReset();
	}

	@Override
	public void doDraw(Set<IPlayer> winners, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doDraw()");

		// Demo mode, just reset
		demoModeBoardReset();
	}

	@Override
	public void doStalemate(Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doStalemate()");

		// Demo mode, just reset
		demoModeBoardReset();
	}

	private void demoModeBoardReset() {
		// Wait, fade out, wait, fade in as a reset board
		this.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeOut(0.25f), Actions.delay(0.5f), new Action() {
			@Override
			public boolean act(float delta) {
				arbiter.doReset();
				return true;
			}
		}, Actions.fadeIn(0.25f)));
	}
}
