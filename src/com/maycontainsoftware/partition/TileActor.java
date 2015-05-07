package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.maycontainsoftware.general.CSoundEngine;
import com.maycontainsoftware.partition.arbiter.Arbiter;
import com.maycontainsoftware.partition.arbiter.ITile;

/**
 * Actor to represent a tile component in the arbiter structure.
 * 
 * @author Charlie
 */
class TileActor extends Group implements ITile {

	/** Tag for logging purposes. */
	private static final String TAG = TileActor.class.getName();

	/** Reference to the sound engine. */
	private final CSoundEngine soundEngine;

	/** Actor to represent the actual tile. */
	private final Actor tile;

	/** Actor to represent the graphical error notification. */
	private final Actor error;

	/** Actor to represent a red highlight. */
	private final Actor redHighlight;

	/** Actor to represent a blue highlight. */
	private final Actor blueHighlight;

	/** Column number. */
	private final byte column;

	/** Row number. */
	private final byte row;

	/** Whether or not the tile is currently enabled. */
	private boolean enabled;

	/**
	 * Construct a new TileActor.
	 * 
	 * @param atlas
	 *            The main app TextureAtlas.
	 * @param column
	 *            This tile's column.
	 * @param row
	 *            This tile's row.
	 */
	public TileActor(final TextureAtlas atlas, final CSoundEngine soundEngine, final byte column, final byte row) {

		// Save reference to the sound engine
		this.soundEngine = soundEngine;

		// The actual tile
		tile = new Image(atlas.findRegion("tile"));
		this.addActor(tile);

		// Graphical error notification
		error = new Image(atlas.findRegion("tile_error"));
		this.addActor(error);

		// Red highlight
		redHighlight = new Image(atlas.findRegion("highlight_red"));
		this.addActor(redHighlight);

		// Blue highlight
		blueHighlight = new Image(atlas.findRegion("highlight_blue"));
		this.addActor(blueHighlight);

		this.column = column;
		this.row = row;
	}

	@Override
	protected void sizeChanged() {

		// Set sizes of child actors
		tile.setSize(getWidth(), getHeight());
		error.setSize(getWidth(), getHeight());
		redHighlight.setSize(getWidth(), getHeight());
		blueHighlight.setSize(getWidth(), getHeight());

		super.sizeChanged();
	}

	@Override
	public byte[] getCoords() {
		return new byte[] { column, row };
	}

	/** Notify tile that at the end of the game it was unreachable. */
	public void endUnreachable() {
		if (!enabled) {
			throw new RuntimeException("TileActor::endOwned;enabled");
		}

		tile.clearActions();
		tile.setColor(Color.WHITE);
		tile.addAction(Actions.sequence(Actions.delay((row + column) * 0.05f), Actions.color(Color.CLEAR, 0.1f)));
	}

	/**
	 * Notify the tile that at the end of the game it existed on a patch occupied by a single player.
	 * 
	 * @param playerNumber
	 *            The player number of the player that owns this tile.
	 */
	public void endOwned(final int playerNumber) {
		if (!enabled) {
			throw new RuntimeException("TileActor::endOwned;enabled");
		}

		switch (playerNumber) {
		case 0:
			doRedHighlight();
			break;
		case 1:
			doBlueHighlight();
			break;
		}
	}

	/** Notify the tile that at the end of the game it existed on a patch occupied by multiple players. */
	public void endShared() {
		if (!enabled) {
			throw new RuntimeException("TileActor::endOwned;enabled");
		}
	}

	/** Activate a red highlight on this tile. */
	private void doRedHighlight() {
		redHighlight.clearActions();
		redHighlight.setColor(Color.CLEAR);
		redHighlight
				.addAction(Actions.sequence(Actions.delay((row + column) * 0.05f), Actions.color(Color.WHITE, 0.1f)));
	}

	/** Activate a blue highlight on this tile. */
	private void doBlueHighlight() {
		blueHighlight.clearActions();
		blueHighlight.setColor(Color.CLEAR);
		blueHighlight.addAction(Actions.sequence(Actions.delay((row + column) * 0.05f),
				Actions.color(Color.WHITE, 0.1f)));
	}

	@Override
	public void doError() {

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doError()");

		if (enabled) {
			// Play the error sound effect
			soundEngine.play(SoundEngine.SoundId.ERROR);

			// Flash up error notification
			error.clearActions();
			error.addAction(Actions.sequence(Actions.color(Color.WHITE), Actions.color(Color.CLEAR, 0.5f)));
		}
	}

	@Override
	public void doShoot(final Arbiter arbiter) {

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doShoot()");

		// No longer enabled
		enabled = false;

		// Play the tile destruction sound effect
		soundEngine.play(SoundEngine.SoundId.EXPLOSION);

		// Fade tile out
		tile.addAction(Actions.sequence(Actions.color(Color.WHITE), Actions.color(Color.CLEAR, 0.15f), new Action() {
			@Override
			public boolean act(float delta) {
				arbiter.shootDone();
				return true;
			}
		}));
	}

	@Override
	public void doReset(final boolean enabled) {

		this.enabled = enabled;

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doReset()");

		tile.setColor(enabled ? Color.WHITE : Color.CLEAR);
		error.setColor(Color.CLEAR);
		redHighlight.setColor(Color.CLEAR);
		blueHighlight.setColor(Color.CLEAR);
	}
}
