package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Actor to represent a tile component in the arbiter structure.
 * 
 * @author Charlie
 */
class TileActor extends Group implements ITile {

	/** Tag for logging purposes. */
	private static final String TAG = TileActor.class.getName();

	/** Actor to represent the actual tile. */
	private final Actor tile;

	/** Actor to represent the graphical error notification. */
	private final Actor error;

	/** Column number. */
	private final byte column;

	/** Row number. */
	private final byte row;

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
	public TileActor(final TextureAtlas atlas, final byte column, final byte row) {

		// The actual tile
		tile = new Image(atlas.findRegion("tile"));
		this.addActor(tile);

		// Graphical error notification
		error = new Image(atlas.findRegion("tile_error"));
		this.addActor(error);

		this.column = column;
		this.row = row;
	}

	@Override
	protected void sizeChanged() {

		// Set sizes of child actors
		tile.setSize(getWidth(), getHeight());
		error.setSize(getWidth(), getHeight());

		super.sizeChanged();
	}

	@Override
	public byte[] getCoords() {
		return new byte[] { column, row };
	}

	@Override
	public void doError() {

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doError()");

		// Flash up error notification
		error.addAction(Actions.sequence(Actions.color(Color.WHITE), Actions.color(Color.CLEAR, 0.5f)));
	}

	@Override
	public void doShoot(final Arbiter arbiter) {

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doShoot()");

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

		Gdx.app.debug(TAG, "Tile [" + getCoords()[0] + "," + getCoords()[1] + "] doReset()");

		tile.setColor(enabled ? Color.WHITE : Color.CLEAR);
		error.setColor(Color.CLEAR);
	}
}